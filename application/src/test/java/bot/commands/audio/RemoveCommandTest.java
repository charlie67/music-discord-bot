// package bot.commands.audio;
//
// import bot.commands.audio.utils.AudioPlayerSendHandler;
// import bot.commands.audio.utils.TrackScheduler;
// import bot.utils.UnicodeEmote;
// import bot.utils.command.CommandEvent;
// import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
// import net.dv8tion.jda.api.entities.Guild;
// import net.dv8tion.jda.api.entities.Message;
// import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
// import net.dv8tion.jda.api.managers.AudioManager;
// import net.dv8tion.jda.api.requests.RestAction;
// import net.dv8tion.jda.api.requests.restaction.MessageAction;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NOT_A_NUMBER;
// import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NO_ARGUMENT;
// import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NO_TRACK_TO_REMOVE;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doAnswer;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import static org.mockito.internal.verification.VerificationModeFactory.times;
//
// @ExtendWith(MockitoExtension.class)
// public class RemoveCommandTest
// {
//    TrackScheduler mockTrackScheduler = mock(TrackScheduler.class);
//
//    AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
//
//    CommandEvent mockCommandEvent = mock(CommandEvent.class);
//
//    @BeforeEach
//    public void init()
//    {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testRemovesTracksSuccessfully()
//    {
//        RestAction mockRestAction = mock(RestAction.class);
//        doAnswer(invocation -> null).when(mockRestAction).queue();
//
//
////        when(mockTrackScheduler.remove(2)).thenReturn(mockRestAction);
//
//        AudioPlayerSendHandler audioPlayerSendHandler = new
// AudioPlayerSendHandler(mockAudioPlayer, mockTrackScheduler);
//
//
//        when(mockCommandEvent.getArgs()).thenReturn("3");
//        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
//        when(mockCommandEvent.getGuild().getAudioManager()).thenReturn(mock(AudioManager.class));
//
// when(mockCommandEvent.getGuild().getAudioManager().getSendingHandler()).thenReturn(audioPlayerSendHandler);
//
//        Message mockMessage = mock(Message.class);
//        when(mockCommandEvent.getMessage()).thenReturn(mockMessage);
//
// when(mockCommandEvent.getMessage().addReaction(any(String.class))).thenReturn(mockRestAction);
//
//        RemoveCommand removeCommand = new RemoveCommand();
//        removeCommand.execute(mockCommandEvent);
//
//        verify(mockTrackScheduler, times(1)).remove(2);
//        verify(mockMessage, times(1)).addReaction(UnicodeEmote.THUMBS_UP);
//    }
//
//    @Test
//    public void testSendsMessageWhenIndexOutOfBoundsIsThrown()
//    {
//        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
//
//        ArgumentCaptor<Integer> intArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
//
//        MessageAction mockMessageAction = mock(MessageAction.class);
//        doAnswer(invocation -> null).when(mockMessageAction).queue();
//
//        TrackScheduler mockTrackScheduler = mock(TrackScheduler.class);
//        doThrow(new
// IndexOutOfBoundsException()).when(mockTrackScheduler).remove(intArgumentCaptor.capture());
//
//        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
//        AudioPlayerSendHandler audioPlayerSendHandler = new
// AudioPlayerSendHandler(mockAudioPlayer, mockTrackScheduler);
//
//        TextChannel mockTextChannel = mock(TextChannel.class);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//        when(mockCommandEvent.getArgs()).thenReturn("0");
//        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
//        when(mockCommandEvent.getGuild().getAudioManager()).thenReturn(mock(AudioManager.class));
//
// when(mockCommandEvent.getGuild().getAudioManager().getSendingHandler()).thenReturn(audioPlayerSendHandler);
//
//
// when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
//
//        RemoveCommand removeCommand = new RemoveCommand();
//        removeCommand.execute(mockCommandEvent);
//
//        assertEquals(Integer.valueOf(-1), intArgumentCaptor.getValue());
//        assertEquals(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, 0),
// stringArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testSendsMessageWhenAudioSendHandlerIsNull()
//    {
//        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
//
//        MessageAction mockMessageAction = mock(MessageAction.class);
//        doAnswer(invocation -> null).when(mockMessageAction).queue();
//
//        TextChannel mockTextChannel = mock(TextChannel.class);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//        when(mockCommandEvent.getArgs()).thenReturn("4");
//        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
//        when(mockCommandEvent.getGuild().getAudioManager()).thenReturn(mock(AudioManager.class));
//        when(mockCommandEvent.getGuild().getAudioManager().getSendingHandler()).thenReturn(null);
//
//
// when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
//
//        RemoveCommand removeCommand = new RemoveCommand();
//        removeCommand.execute(mockCommandEvent);
//
//        assertEquals(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, 4),
// stringArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testSendsMessageWhenNoArgumentIsSent()
//    {
//        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
//
//        MessageAction mockMessageAction = mock(MessageAction.class);
//        doAnswer(invocation -> null).when(mockMessageAction).queue();
//
//        TextChannel mockTextChannel = mock(TextChannel.class);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//        when(mockCommandEvent.getArgs()).thenReturn("");
//
//
// when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
//
//        RemoveCommand removeCommand = new RemoveCommand();
//        removeCommand.execute(mockCommandEvent);
//
//        assertEquals(REMOVE_COMMAND_NO_ARGUMENT, stringArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testSendsMessageWhenArgumentThatIsNotANumberIsSent()
//    {
//        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
//
//        MessageAction mockMessageAction = mock(MessageAction.class);
//        doAnswer(invocation -> null).when(mockMessageAction).queue();
//
//        TextChannel mockTextChannel = mock(TextChannel.class);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//        when(mockCommandEvent.getArgs()).thenReturn("doo doo");
//
//
// when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
//
//        RemoveCommand removeCommand = new RemoveCommand();
//        removeCommand.execute(mockCommandEvent);
//
//        assertEquals(String.format(REMOVE_COMMAND_NOT_A_NUMBER, "doo doo"),
// stringArgumentCaptor.getValue());
//    }
// }
