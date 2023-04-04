package bot.commands.audio.utils;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public class VoiceChannelServiceTest {

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  //    @Test
  //    public void canJoinVoiceChannelSuccessfully()
  //    {
  //        VoiceChannel mockVoiceChannel = mock(VoiceChannel.class);
  //
  //        GuildVoiceState mockGuildVoiceState = mock(GuildVoiceState.class);
  //        when(mockGuildVoiceState.inVoiceChannel()).thenReturn(true);
  //        when(mockGuildVoiceState.getChannel()).thenReturn(mockVoiceChannel);
  //
  //        AudioManager mockAudioManager = mock(AudioManager.class);
  //
  //        ArgumentCaptor<AudioSendHandler> audioSendHandlerArgumentCaptor =
  // ArgumentCaptor.forClass(AudioSendHandler.class);
  //        doAnswer(invocation ->
  // null).when(mockAudioManager).setSendingHandler(audioSendHandlerArgumentCaptor.capture());
  //
  //        ArgumentCaptor<VoiceChannel> voiceChannelArgumentCaptor =
  // ArgumentCaptor.forClass(VoiceChannel.class);
  //        doAnswer(invocation ->
  // null).when(mockAudioManager).openAudioConnection(voiceChannelArgumentCaptor.capture());
  //
  //        Member mockMember = mock(Member.class);
  //        when(mockMember.getVoiceState()).thenReturn(mockGuildVoiceState);
  //
  //        Guild mockGuild = mock(Guild.class);
  //        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);
  //
  //        ArgumentCaptor<AudioEventListener> audioEventListenerArgumentCaptor =
  //                ArgumentCaptor.forClass(AudioEventListener.class);
  //        AudioPlayer mockPlayer = mock(AudioPlayer.class);
  //        doAnswer(invocation ->
  // null).when(mockPlayer).addListener(audioEventListenerArgumentCaptor.capture());
  //
  //        AudioPlayerManager mockAudioPlayerManager = mock(AudioPlayerManager.class);
  //        when(mockAudioPlayerManager.createPlayer()).thenReturn(mockPlayer);
  //
  //        VoiceChannelUtils.joinVoiceChannel(mockMember, mockGuild, mockAudioPlayerManager);
  //
  //        assertTrue(audioSendHandlerArgumentCaptor.getValue() instanceof AudioPlayerSendHandler);
  //        assertTrue(audioEventListenerArgumentCaptor.getValue() instanceof TrackScheduler);
  //        assertEquals(mockVoiceChannel, voiceChannelArgumentCaptor.getValue());
  //    }
  //
  //    @Test(expected = IllegalArgumentException.class)
  //    public void canJoinVoiceChannelFailsGracefullyWhenVoiceStateIsNull()
  //    {
  //        Member mockMember = mock(Member.class);
  //        when(mockMember.getVoiceState()).thenReturn(null);
  //
  //        VoiceChannelUtils.joinVoiceChannel(mockMember, null, null);
  //    }
  //
  //    @Test(expected = IllegalArgumentException.class)
  //    public void canJoinVoiceChannelFailsGracefullyWhenVoiceStateIsNotConnected()
  //    {
  //
  //        GuildVoiceState mockGuildVoiceState = mock(GuildVoiceState.class);
  //        when(mockGuildVoiceState.inVoiceChannel()).thenReturn(false);
  //
  //        Member mockMember = mock(Member.class);
  //        when(mockMember.getVoiceState()).thenReturn(mockGuildVoiceState);
  //
  //        VoiceChannelUtils.joinVoiceChannel(mockMember, null, null);
  //    }

  //  @Test
  //  public void canGetAudioPlayerSendHandlerSuccessfully() {
  //    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
  //    final String GUILD_ID = "mockGuildId";
  //
  //    AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);
  //
  //    AudioManager mockAudioManager = mock(AudioManager.class);
  //    when(mockAudioManager.isConnected()).thenReturn(true);
  //    when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);
  //
  //    Guild mockGuild = mock(Guild.class);
  //    when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);
  //
  //    JDA mockJda = mock(JDA.class);
  //    when(mockJda.getGuildById(stringArgumentCaptor.capture())).thenReturn(mockGuild);
  //
  //    AudioPlayerSendHandler returnedAudioPlayerSendHandler =
  // VoiceChannelService.getAudioPlayerSendHandler(
  //        mockJda,
  //        GUILD_ID);
  //    assertEquals(mockAudioPlayerSendHandler, returnedAudioPlayerSendHandler);
  //
  //    assertEquals(GUILD_ID, stringArgumentCaptor.getValue());
  //  }
}
