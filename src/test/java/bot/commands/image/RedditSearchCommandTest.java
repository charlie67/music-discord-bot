package bot.commands.image;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedditSearchCommandTest
{
    @Test
    public void testRedditSearchWhenNoArgsSpecified()
    {
        RedditSearchCommand command = new RedditSearchCommand();

        AtomicBoolean messageQueued = new AtomicBoolean(false);
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation ->
        {
            messageQueued.set(true);
            return null;
        }).when(mockMessageAction).queue();

        MessageChannel mockMessageChannel = mock(MessageChannel.class);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(mockMessageChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn("");
        when(mockCommandEvent.getChannel()).thenReturn(mockMessageChannel);

        command.execute(mockCommandEvent);

        assertTrue(messageQueued.get());
        assertEquals(stringArgumentCaptor.getAllValues().get(0), "Provide a subreddit to search for");
    }

    @Test
    public void testRedditSearchDoesntReturnSameURL() throws InterruptedException
    {
        RedditSearchCommand command = new RedditSearchCommand();

        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        MessageChannel mockMessageChannel = mock(MessageChannel.class);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(mockMessageChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn("pics");
        when(mockCommandEvent.getChannel()).thenReturn(mockMessageChannel);

        int NUM_EXECUTIONS = 100;

        for (int i = 0; i < NUM_EXECUTIONS; i++)
        {
            command.execute(mockCommandEvent);
            TimeUnit.SECONDS.sleep(2);
        }

        List<String> values = stringArgumentCaptor.getAllValues();

        assertEquals(values.size(), NUM_EXECUTIONS);
        assertEquals(new HashSet<>(values).size(), NUM_EXECUTIONS);
    }
}