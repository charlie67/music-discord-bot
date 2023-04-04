// package bot.commands.text;
//
// import bot.utils.command.CommandEvent;
// import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
// import static testUtils.MockTextChannelCreator.createMockTextChannelWhereTextIsSentNoTyping;
//
// @ExtendWith(MockitoExtension.class)
// public class TestEchoTextCommand
// {
//    @BeforeEach
//    public void init()
//    {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testFailsWhenNoArgsAreSent()
//    {
//        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
//        TextChannel mockTextChannel =
// createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getArgs()).thenReturn("");
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//
//        EchoTextCommand echoTextCommand = new EchoTextCommand();
//        echoTextCommand.execute(mockCommandEvent);
//
//        assertEquals(ECHO_COMMAND_NO_ARGS, textChannelArgumentCaptor.getValue());
//    }
//
//    @Test
//    public void testExecutesSuccessfully()
//    {
//        final String ARGS = "this is a message";
//        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
//        TextChannel mockTextChannel =
// createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);
//
//        CommandEvent mockCommandEvent = mock(CommandEvent.class);
//        when(mockCommandEvent.getArgs()).thenReturn(ARGS);
//        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
//
//        EchoTextCommand echoTextCommand = new EchoTextCommand();
//        echoTextCommand.execute(mockCommandEvent);
//
//        assertEquals(ARGS, textChannelArgumentCaptor.getValue());
//    }
// }
