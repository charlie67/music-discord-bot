package bot.commands.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import bot.configuration.BotConfiguration;
import bot.utils.TextChannelResponses;
import bot.utils.command.CommandEvent;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;

@Disabled
public class RedditSearchCommandTest {

  @MockBean
  private BotConfiguration botConfiguration;

  public RedditSearchCommandTest() {
    when(botConfiguration.getRedditClientId()).thenReturn("");
    when(botConfiguration.getRedditClientSecret()).thenReturn("");

  }

  CommandEvent getMockRedditSearchMessageAction(ArgumentCaptor<String> stringArgumentCaptor,
      AtomicBoolean messageQueued
      , String subreddit) {
    MessageAction mockMessageAction = mock(MessageAction.class);
    doAnswer(invocation ->
    {
      messageQueued.set(true);
      return null;
    }).when(mockMessageAction).queue();

    MessageChannel mockMessageChannel = mock(MessageChannel.class);

    when(mockMessageChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(
        mockMessageAction);

    CommandEvent mockCommandEvent = mock(CommandEvent.class);
    when(mockCommandEvent.getArgs()).thenReturn(subreddit);
    when(mockCommandEvent.getChannel()).thenReturn(mockMessageChannel);

    return mockCommandEvent;
  }

  @Test
  public void testRedditSearchWhenNoArgsSpecified() {
    RedditSearchCommand command = new RedditSearchCommand(botConfiguration);

    AtomicBoolean messageQueued = new AtomicBoolean(false);
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    CommandEvent mockCommandEvent = getMockRedditSearchMessageAction(stringArgumentCaptor,
        messageQueued, "");

    command.execute(mockCommandEvent);

    assertTrue(messageQueued.get());
    assertEquals(TextChannelResponses.NO_SUBREDDIT_PROVIDED, stringArgumentCaptor.getValue());
  }

  @Test
  public void testRedditSearchWhenSubredditCantBeAccessed() {
    RedditSearchCommand command = new RedditSearchCommand(botConfiguration);

    AtomicBoolean messageQueued = new AtomicBoolean(false);
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    CommandEvent mockCommandEvent = getMockRedditSearchMessageAction(stringArgumentCaptor,
        messageQueued,
        "the_donald");

    command.execute(mockCommandEvent);

    assertTrue(messageQueued.get());
    assertEquals(TextChannelResponses.UNABLE_TO_SEARCH_FOR_SUBREDDIT,
        stringArgumentCaptor.getValue());
  }

  @Test
  public void testRedditSearchWhenSubHasNoPosts() {
    RedditSearchCommand command = new RedditSearchCommand(botConfiguration);

    AtomicBoolean messageQueued = new AtomicBoolean(false);
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    CommandEvent mockCommandEvent = getMockRedditSearchMessageAction(stringArgumentCaptor,
        messageQueued,
        "emptytestsub");

    command.execute(mockCommandEvent);

    assertTrue(messageQueued.get());
    assertEquals(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT,
        stringArgumentCaptor.getValue());
  }

  @Test
  public void testRedditSearchDoesntReturnSameURL() throws InterruptedException {
    RedditSearchCommand command = new RedditSearchCommand(botConfiguration);

    AtomicBoolean messageQueued = new AtomicBoolean(false);
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
    CommandEvent mockCommandEvent = getMockRedditSearchMessageAction(stringArgumentCaptor,
        messageQueued, "pics");

    int NUM_EXECUTIONS = 10;

    for (int i = 0; i < NUM_EXECUTIONS; i++) {
      command.execute(mockCommandEvent);
      TimeUnit.MILLISECONDS.sleep(500);
    }

    List<String> values = stringArgumentCaptor.getAllValues();

    assertEquals(NUM_EXECUTIONS, values.size());
    assertEquals(NUM_EXECUTIONS, new HashSet<>(values).size());
  }
}