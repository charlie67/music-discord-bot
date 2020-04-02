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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayTopCommandTest
{
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecute()
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

        PlayTopCommand playTopCommand = new PlayTopCommand(playerManager);
        playTopCommand.execute(mockCommandEvent);

        await().atMost(10, SECONDS).until(() -> audioPlayerSendHandler.getTrackScheduler().getQueue().size()>0);
        List<AudioTrack> queue = audioPlayerSendHandler.getTrackScheduler().getQueue();
        assertTrue(queue.size()>0);
        assertTrue(queue.get(0) instanceof YoutubeAudioTrack);
        AudioTrack firstTopTrack = queue.get(0);

        when(mockCommandEvent.getArgs()).thenReturn("A song 2");
        playTopCommand.execute(mockCommandEvent);

        await().atMost(10, SECONDS).until(() -> audioPlayerSendHandler.getTrackScheduler().getQueue().size()>1);
        queue = audioPlayerSendHandler.getTrackScheduler().getQueue();
        assertTrue(queue.size()>1);
        assertTrue(queue.get(0) instanceof YoutubeAudioTrack);
        assertNotEquals(firstTopTrack, queue.get(0));
    }
}
