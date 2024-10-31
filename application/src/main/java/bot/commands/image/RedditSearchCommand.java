package bot.commands.image;

import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RedditSearchCommand extends Command {
	private final Map<MultiKey<String>, SubredditHolderComponent> guildIdToSubredditHolder =
					new HashMap<>();

	public RedditSearchCommand() {
		this.name = "redditsearch";
		this.help = "Search reddit for an image from a supplied subreddit";
		this.guildOnly = true;

		this.options = List.of(Option.createOption(Response.SEARCH_SUBREDDIT_OPTION, true, 0));
	}

	@Override
	public void execute(CommandEvent event) {
		if (!event.optionPresent(Response.SEARCH_SUBREDDIT_OPTION)) {
			event.reply(TextChannelResponses.NO_SUBREDDIT_PROVIDED);
			return;
		}

		final String guildId = event.getGuild().getId();
		final String subreddit = event.getOption(Response.SEARCH_SUBREDDIT_OPTION).getAsString();

		try {
			getNextUrl(event, subreddit, guildId);
		} catch (IllegalArgumentException e) {
			event.reply(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT);
		}
	}

	private void getNextUrl(CommandEvent event, String subreddit, String guildId) {
		if (guildIdToSubredditHolder.containsKey(new MultiKey<>(subreddit, guildId))) {
			SubredditHolderComponent subredditHolderComponent =
							guildIdToSubredditHolder.get(new MultiKey<>(subreddit, guildId));
			try {
				String url = subredditHolderComponent.getNewUrlItem();
				event.reply(url);
			} catch (IllegalAccessException e) {
				event.reply(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT);
			}
		} else {
			SubredditHolderComponent subredditHolderComponent = new SubredditHolderComponent(subreddit);
			guildIdToSubredditHolder.put(new MultiKey<>(subreddit, guildId), subredditHolderComponent);
			try {
				String url = subredditHolderComponent.getNewUrlItem();
				event.reply(url);
			} catch (IllegalAccessException e) {
				event.reply(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT);
			}
		}
	}

//	@Override
//	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
//		final String message = event.getMessage().getContentRaw();
//		String[] parts = message.split("\\s+", 2);
//
//		TextOptionValue.TextOptionValueBuilder textOptionValueBuilder = TextOptionValue.builder().optionName(OptionName.SEARCH_SUBREDDIT_OPTION).jda(event.getJDA());
//
//		if (parts.length == 1) {
//			OptionValue optionValue = textOptionValueBuilder.optionValue("").build();
//			return Map.of(OptionName.SEARCH_SUBREDDIT_OPTION, optionValue);
//		}
//
//		OptionValue optionValue = textOptionValueBuilder.optionValue(parts[1]).build();
//		return Map.of(OptionName.SEARCH_SUBREDDIT_OPTION, optionValue);
//	}

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
