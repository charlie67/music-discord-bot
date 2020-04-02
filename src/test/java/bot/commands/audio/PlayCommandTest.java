package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.TextChannelResponses;
import bot.commands.audio.utils.TrackScheduler;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static bot.commands.audio.TestUtils.createMockCommandEventForAudioWithArguments;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayCommandTest
{
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteWithNoArgument()
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "mockTextChannelId", "mockMemberId", "mockGuildId", "", messageQueued);

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
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "mockTextChannelId", "mockMemberId", null, "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyGuildId() throws IllegalArgumentException
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "mockTextChannelId", "mockMemberId", "", "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithNullTextChannelId() throws IllegalArgumentException
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, null, "mockMemberId", "mockGuildId", "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyTextChannelId() throws IllegalArgumentException
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "", "mockMemberId", "mockGuildId", "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithNullMemberId() throws IllegalArgumentException
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "textChannelId", null, "mockGuildId", "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteWithEmptyMemberId() throws IllegalArgumentException
    {
        AtomicBoolean messageQueued = new AtomicBoolean(false);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = createMockCommandEventForAudioWithArguments(stringArgumentCaptor, "textChannelId", "", "mockGuildId", "", messageQueued);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);
    }

    @Test
    public void testExecuteWithArgument()
    {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        MessageAction mockMessageAction = mock(MessageAction.class);
        when(mockMessageAction.append(anyString())).thenReturn(mockMessageAction);

        RestAction mockRestAction = mock(RestAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(new YoutubeAudioTrack(new AudioTrackInfo("1", "", 999999999, "", true, ""), new YoutubeAudioSourceManager()));

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn("mockMemberId");

        VoiceChannel mockVoiceChannel = mock(VoiceChannel.class);
        ArrayList<Member> memberList = new ArrayList<>();
        memberList.add(mockMember);
        when(mockVoiceChannel.getMembers()).thenReturn(memberList);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getConnectedChannel()).thenReturn(mockVoiceChannel);
        AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(mockAudioPlayer, new TrackScheduler());
        when(mockAudioManager.getSendingHandler()).thenReturn(audioPlayerSendHandler);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendTyping()).thenReturn(mockRestAction);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
        when(mockTextChannel.getId()).thenReturn("mockMessageChannelId");

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getId()).thenReturn("mockGuildId");
        when(mockGuild.getTextChannelById(anyString())).thenReturn(mockTextChannel);
        when(mockGuild.getMemberById(anyString())).thenReturn(mockMember);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        JDA mockJDA = mock(JDA.class);
        when(mockJDA.getGuildById(anyString())).thenReturn(mockGuild);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getJDA()).thenReturn(mockJDA);
        when(mockCommandEvent.getArgs()).thenReturn("Fallen Kingdom");
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getMember()).thenReturn(mockMember);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);

        await().atMost(10, SECONDS).until(() -> audioPlayerSendHandler.getTrackScheduler().getQueue().size()>0);
        List<AudioTrack> queue = audioPlayerSendHandler.getTrackScheduler().getQueue();
        assertTrue(queue.size()>0);
        assertTrue(queue.get(0) instanceof YoutubeAudioTrack);
    }
}
