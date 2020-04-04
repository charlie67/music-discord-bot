package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VoiceChannelUtilsTest
{

    @Test
    public void canJoinVoiceChannelSuccessfully()
    {
        VoiceChannel mockVoiceChannel = mock(VoiceChannel.class);

        GuildVoiceState mockGuildVoiceState = mock(GuildVoiceState.class);
        when(mockGuildVoiceState.inVoiceChannel()).thenReturn(true);
        when(mockGuildVoiceState.getChannel()).thenReturn(mockVoiceChannel);

        AudioManager mockAudioManager = mock(AudioManager.class);

        ArgumentCaptor<AudioSendHandler> audioSendHandlerArgumentCaptor = ArgumentCaptor.forClass(AudioSendHandler.class);
        doAnswer(invocation -> null).when(mockAudioManager).setSendingHandler(audioSendHandlerArgumentCaptor.capture());

        ArgumentCaptor<VoiceChannel> voiceChannelArgumentCaptor = ArgumentCaptor.forClass(VoiceChannel.class);
        doAnswer(invocation -> null).when(mockAudioManager).openAudioConnection(voiceChannelArgumentCaptor.capture());

        Member mockMember = mock(Member.class);
        when(mockMember.getVoiceState()).thenReturn(mockGuildVoiceState);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        ArgumentCaptor<AudioEventListener> audioEventListenerArgumentCaptor =
                ArgumentCaptor.forClass(AudioEventListener.class);
        AudioPlayer mockPlayer = mock(AudioPlayer.class);
        doAnswer(invocation -> null).when(mockPlayer).addListener(audioEventListenerArgumentCaptor.capture());

        AudioPlayerManager mockAudioPlayerManager = mock(AudioPlayerManager.class);
        when(mockAudioPlayerManager.createPlayer()).thenReturn(mockPlayer);

        VoiceChannelUtils.joinVoiceChannel(mockMember, mockGuild, mockAudioPlayerManager);

        assertTrue(audioSendHandlerArgumentCaptor.getValue() instanceof AudioPlayerSendHandler);
        assertTrue(audioEventListenerArgumentCaptor.getValue() instanceof TrackScheduler);
        assertEquals(mockVoiceChannel, voiceChannelArgumentCaptor.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canJoinVoiceChannelFailsGracefullyWhenVoiceStateIsNull()
    {
        Member mockMember = mock(Member.class);
        when(mockMember.getVoiceState()).thenReturn(null);

        VoiceChannelUtils.joinVoiceChannel(mockMember, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canJoinVoiceChannelFailsGracefullyWhenVoiceStateIsNotConnected()
    {

        GuildVoiceState mockGuildVoiceState = mock(GuildVoiceState.class);
        when(mockGuildVoiceState.inVoiceChannel()).thenReturn(false);

        Member mockMember = mock(Member.class);
        when(mockMember.getVoiceState()).thenReturn(mockGuildVoiceState);

        VoiceChannelUtils.joinVoiceChannel(mockMember, null, null);
    }

    @Test
    public void canGetAudioPlayerSendHandlerSuccessfully()
    {
        AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        AudioPlayerSendHandler returnedAudioPlayerSendHandler = VoiceChannelUtils.getAudioPlayerSendHandler(mockGuild);
        assertEquals(mockAudioPlayerSendHandler, returnedAudioPlayerSendHandler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingAudioPlayerSendHandlerFailsSuccessfully()
    {
        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(false);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        AudioPlayerSendHandler returnedAudioPlayerSendHandler = VoiceChannelUtils.getAudioPlayerSendHandler(mockGuild);
    }
}