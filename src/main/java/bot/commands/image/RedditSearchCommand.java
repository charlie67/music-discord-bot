package bot.commands.image;

import bot.utils.Injector;
import bot.utils.SystemEnv;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RedditSearchCommand extends Command
{
    @SystemEnv("REDDIT_CLIENT_ID")
    private String REDDIT_CLIENT_ID;

    @SystemEnv("REDDIT_CLIENT_SECRET")
    private String REDDIT_CLIENT_SECRET;

    private RedditClient reddit;

    //maps subreddit onto links for that subreddit
    private HashMap<String, SubredditHashComponent> subredditHashMap = new HashMap<>();

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
            event.getChannel().sendMessage("Provide a subreddit to search for").queue();
            return;
        }

        SubredditHashComponent subredditHashComponent;

        if (subredditHashMap.containsKey(subreddit))
        {
            subredditHashComponent = subredditHashMap.get(subreddit);

            if (subredditHashComponent.timeStored - System.currentTimeMillis() > 604800000)
            {
                subredditHashComponent = new SubredditHashComponent(System.currentTimeMillis(), subreddit);

                if (!subreddit.equals("randnsfw") && !subreddit.equals("rand"))
                {
                    subredditHashMap.put(subreddit, subredditHashComponent);
                }
            }
        }
        else
        {
            subredditHashComponent = new SubredditHashComponent(System.currentTimeMillis(), subreddit);
            subredditHashMap.put(subreddit, subredditHashComponent);
        }

        event.getChannel().sendMessage(subredditHashComponent.getNewUrlItem()).queue();
    }

    class SubredditHashComponent
    {
        long timeStored;
        private List<Submission> subredditItems;
        private DefaultPaginator<Submission> paginator;
        private int itemCounter = 0;

        SubredditHashComponent(long timeStored, String subreddit)
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

        String getNewUrlItem()
        {
            if (itemCounter >= subredditItems.size())
            {
                extendList();
            }
            return subredditItems.get(itemCounter++).getUrl();
        }

    }
}