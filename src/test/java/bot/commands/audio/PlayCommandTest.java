package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.TextChannelResponses;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testUtils.AudioTestMocker.createMockCommandEventForPlayCommandWhereAudioGetsPlayed;
import static testUtils.AudioTestMocker.createMockCommandEventForPlayCommandWhereChannelCantBeFound;
import static testUtils.AudioTestMocker.createMockCommandEventForPlayCommandWhereGuildCantBeFound;
import static testUtils.AudioTestMocker.createMockCommandEventForPlayCommandWhereItErrorsOut;
import static testUtils.AudioTestMocker.createMockCommandEventForPlayCommandWhereMemberCantBeFound;

@RunWith(MockitoJUnitRunner.class)
public class PlayCommandTest
{
    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteWithNoArgument()
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Answer<Void> messageQueuedAnswer = invocation ->
        {
            messageQueued.set(true);
            return null;
        };

        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(stringArgumentCaptor,
                "mockTextChannelId",
                "mockMemberId", "mockGuildId", "", messageQueuedAnswer);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);

        assertTrue(messageQueued.get());
        assertEquals(stringArgumentCaptor.getValue(), TextChannelResponses.NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithNullGuildId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(
                "mockTextChannelId", "mockMemberId", null, "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyGuildId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(
                "mockTextChannelId", "mockMemberId", "", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithNullTextChannelId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(null,
                "mockMemberId", "mockGuildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyTextChannelId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut("",
                "mockMemberId", "mockGuildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithNullMemberId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(
                "textChannelId", null, "mockGuildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyMemberId() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(
                "textChannelId", "", "mockGuildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayFailsWithExceptionWhenGuildWithIdCantBeFound() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereGuildCantBeFound(
                "textChannelId", "memberId", "guildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayFailsWithExceptionWhenChannelWithIdCantBeFound() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereChannelCantBeFound(
                "textChannelId", "memberId", "guildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayFailsWithExceptionWhenMemberWithIdCantBeFound() throws IllegalArgumentException
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereMemberCantBeFound(
                "textChannelId", "memberId", "guildId", "");

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test
    public void testExecuteWithArgument()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessageEmbed> messageEmbedArgumentCaptor = ArgumentCaptor.forClass(MessageEmbed.class);
        Answer<Void> messageQueuedAnswer = invocation -> null;

        AudioTrack mockAudioTrack = new YoutubeAudioTrack(new AudioTrackInfo("1", "", 999999999, "", true, ""),
                new YoutubeAudioSourceManager());

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(mockAudioTrack);
        AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(mockAudioPlayer,
                new TrackScheduler());

        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereAudioGetsPlayed(stringArgumentCaptor,
                "textChannelId", "mockMemberId", "mockGuildId", "Fallen Kingdom", true,
                audioPlayerSendHandler, messageEmbedArgumentCaptor, messageQueuedAnswer);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);

        await().atMost(10, SECONDS).until(() -> audioPlayerSendHandler.getTrackScheduler().getQueue().size() > 0);
        List<AudioTrack> queue = audioPlayerSendHandler.getTrackScheduler().getQueue();
        assertTrue(queue.size() > 0);
        assertTrue(queue.get(0) instanceof YoutubeAudioTrack);
        assertTrue(stringArgumentCaptor.getAllValues().get(1).startsWith("Fallen Kingdom"));
    }
}
