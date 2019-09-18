package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
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
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> {
            messageQueued.set(true);
            return null;
        }).when(mockMessageAction).queue();

        MessageChannel mockMessageChannel = mock(MessageChannel.class);
        when(mockMessageChannel.sendMessage(anyString())).thenReturn(mockMessageAction);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn("");
        when(mockCommandEvent.getChannel()).thenReturn(mockMessageChannel);

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);

        assertTrue(messageQueued.get());
    }

    @Test
    public void testExecuteWithArgument()
    {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();
        when(mockMessageAction.append(anyString())).thenReturn(mockMessageAction);

        RestAction mockRestAction = mock(RestAction.class);
        doAnswer(invocation -> null).when(mockRestAction).queue();

        MessageChannel mockMessageChannel = mock(MessageChannel.class);
        when(mockMessageChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        when(mockMessageChannel.sendMessage((MessageEmbed) any())).thenReturn(mockMessageAction);
        when(mockMessageChannel.sendTyping()).thenReturn(mockRestAction);

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(new YoutubeAudioTrack(new AudioTrackInfo("1", "", 999999999, "", true, ""), new YoutubeAudioSourceManager()));

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        AudioPlayerSendHandler audioPlayerSendHandler = new AudioPlayerSendHandler(mockAudioPlayer, new TrackScheduler());
        when(mockAudioManager.getSendingHandler()).thenReturn(audioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn("fallen kingdom");
        when(mockCommandEvent.getChannel()).thenReturn(mockMessageChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);

        PlayCommand playCommand = new PlayCommand(playerManager);
        playCommand.execute(mockCommandEvent);

        await().atMost(10, SECONDS).until(() -> audioPlayerSendHandler.getTrackScheduler().getQueue().size()>0);
        List<AudioTrack> queue = audioPlayerSendHandler.getTrackScheduler().getQueue();
        assertTrue(queue.size()>0);
        assertTrue(queue.get(0) instanceof YoutubeAudioTrack);
    }
}
