package bot.commands.image;

import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
public class RedditSearchCommand extends Command {

  Map<MultiKey<String>, SubredditHolderComponent> guildIdToSubredditHolder = new HashMap<>();

  public RedditSearchCommand() {
    this.name = "redditsearch";
    this.help = "Search reddit for an image from a supplied subreddit";
  }

  @Override
  protected void execute(CommandEvent event) {
    final String subreddit = event.getArgs();

    if (subreddit == null || subreddit.isEmpty()) {
      event.getChannel().sendMessage(TextChannelResponses.NO_SUBREDDIT_PROVIDED).queue();
      return;
    }
    final String guildId = event.getGuild().getId();

    if (guildIdToSubredditHolder.containsKey(new MultiKey<>(subreddit, guildId))) {
      SubredditHolderComponent subredditHolderComponent =
          guildIdToSubredditHolder.get(new MultiKey<>(subreddit, guildId));
      try {
        String url = subredditHolderComponent.getNewUrlItem();
        event.getChannel().sendMessage(url).queue();
      } catch (IllegalAccessException e) {
        event
            .getChannel()
            .sendMessage(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT)
            .queue();
      }
    } else {
      SubredditHolderComponent subredditHolderComponent = new SubredditHolderComponent(subreddit);
      guildIdToSubredditHolder.put(new MultiKey<>(subreddit, guildId), subredditHolderComponent);
      try {
        String url = subredditHolderComponent.getNewUrlItem();
        event.getChannel().sendMessage(url).queue();
      } catch (IllegalAccessException e) {
        event
            .getChannel()
            .sendMessage(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT)
            .queue();
      }
    }
  }

  private static class SubredditHolderComponent {
    private final String subredditName;
    private final List<JSONObject> subredditItems = new ArrayList<>();
    private int itemCounter = 0;

    SubredditHolderComponent(String subredditName) {
      this.subredditName = subredditName;
    }

    String getNewUrlItem() throws IllegalAccessException {

      if (itemCounter >= subredditItems.size()) {
        extendList();
      }
      return subredditItems.get(itemCounter++).getString("url");
    }

    private void extendList() {
      int upperLimitRequest = itemCounter + 50;
      String url =
          "https://www.reddit.com/r/"
              + subredditName
              + "/top/.json?t=all&limit=50&after="
              + upperLimitRequest;
      // create an HTTP connection to the URL
      try {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        if (json.has("error")) {
          log.error(
              "Error while trying to get subreddit items, error: {} message: {} reason: {}",
              json.getInt("error"),
              json.getString("message"),
              json.getString("reason"));
          throw new IllegalArgumentException("Error getting subreddit items");
        }
        JSONArray posts = json.getJSONObject("data").getJSONArray("children");
        posts.forEach(
            post -> {
              JSONObject postObject = (JSONObject) post;
              JSONObject postObjectData = postObject.getJSONObject("data");
              subredditItems.add(postObjectData);
            });

      } catch (IOException e) {
        log.error("Error while trying to get subreddit items", e);
        throw new IllegalArgumentException("Error getting subreddit items");
      }
    }
  }
}
