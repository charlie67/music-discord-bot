package bot.commands.image;

import bot.utils.Injector;
import bot.utils.SystemEnv;
import bot.utils.TextChannelResponses;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RedditSearchCommand extends Command
{
    private final Logger LOGGER = LogManager.getLogger(RedditSearchCommand.class);

    @SystemEnv("REDDIT_CLIENT_ID")
    private String REDDIT_CLIENT_ID;

    @SystemEnv("REDDIT_CLIENT_SECRET")
    private String REDDIT_CLIENT_SECRET;

    private final RedditClient reddit;

    //maps subreddit onto links for that subreddit
    private final HashMap<String, SubredditHashComponent> subredditHashMap = new HashMap<>();

    public RedditSearchCommand()
    {
        this.name = "redditsearch";
        this.help = "Search reddit for an image from a supplied subreddit";

        Injector.injectSystemEnvValue(this);

        // Assuming we have a 'script' reddit app
        Credentials oauthCreds = Credentials.userless(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET, new UUID(1, 99999));

        // Create a unique User-Agent for our bot
        UserAgent userAgent = new UserAgent("bot", "hireddit", "0", "me");

        // Authenticate our client
        reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String subreddit = event.getArgs();

        if (subreddit == null || subreddit.isEmpty())
        {
            event.getChannel().sendMessage(TextChannelResponses.NO_SUBREDDIT_PROVIDED).queue();
            return;
        }

        SubredditHashComponent subredditHashComponent;

        if (subredditHashMap.containsKey(subreddit))
        {
            subredditHashComponent = subredditHashMap.get(subreddit);

            if (subredditHashComponent.timeStored - System.currentTimeMillis() > 604800000)
            {
                try
                {
                    subredditHashComponent = new SubredditHashComponent(System.currentTimeMillis(), subreddit);
                }
                catch(ApiException e)
                {
                    event.getChannel().sendMessage(TextChannelResponses.UNABLE_TO_SEARCH_FOR_SUBREDDIT).queue();
                    LOGGER.error("Encountered error when search for subreddit", e);
                    return;
                }

                if (!subreddit.contains("rand"))
                {
                    subredditHashMap.put(subreddit, subredditHashComponent);
                }
            }
        }
        else
        {
            try
            {
                subredditHashComponent = new SubredditHashComponent(System.currentTimeMillis(), subreddit);
            }
            catch(ApiException e)
            {
                event.getChannel().sendMessage(TextChannelResponses.UNABLE_TO_SEARCH_FOR_SUBREDDIT).queue();
                LOGGER.error("Encountered error when searching for subreddit", e);
                return;
            }
            if (!subreddit.contains("rand"))
            {
                subredditHashMap.put(subreddit, subredditHashComponent);
            }
        }

        try
        {
            event.getChannel().sendMessage(subredditHashComponent.getNewUrlItem()).queue();
        }
        catch(IllegalAccessException e)
        {
            event.getChannel().sendMessage(TextChannelResponses.UNABLE_TO_GET_POSTS_FOR_SUBREDDIT).queue();
            LOGGER.error("Encountered error when trying to get posts from subreddit", e);
        }
    }

    class SubredditHashComponent
    {
        long timeStored;
        private List<Submission> subredditItems;
        private DefaultPaginator<Submission> paginator;
        private int itemCounter = 0;

        SubredditHashComponent(long timeStored, String subreddit) throws ApiException
        {
            this.timeStored = timeStored;
            this.subredditItems = new ArrayList<>();

            SubredditReference subredditReference = reddit.subreddit(subreddit);
            paginator = subredditReference.posts().sorting(SubredditSort.TOP).timePeriod(TimePeriod.ALL).build();

            Listing<Submission> top50MostPopular = paginator.next();
            subredditItems.addAll(top50MostPopular.getChildren());
        }

        void extendList()
        {
            subredditItems.addAll(paginator.next());
        }

        String getNewUrlItem() throws IllegalAccessException
        {
            if (subredditItems.size() == 0)
            {
                throw new IllegalAccessException("No items in the subreddit to display");
            }

            if (itemCounter >= subredditItems.size())
            {
                extendList();
            }
            return subredditItems.get(itemCounter++).getUrl();
        }

    }
}