package bot.commands.image;

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
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;
import java.util.UUID;

public class RedditSearchCommand extends Command
{
    @Value("${REDDIT_CLIENT_ID}")
    private static String REDDIT_CLIENT_ID;

    @Value("${REDDIT_CLIENT_SECRET}")
    private static String REDDIT_CLIENT_SECRET;

    public RedditSearchCommand()
    {
        this.name = "redditsearch";
        this.help = "Search reddit for an image from a supplied subreddit";
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

        // Assuming we have a 'script' reddit app
        Credentials oauthCreds = Credentials.userless(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET, new UUID(1, 99999));

        // Create a unique User-Agent for our bot
        UserAgent userAgent = new UserAgent("bot", "hireddit", "0", "me");

        // Authenticate our client
        RedditClient reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);

        SubredditReference subredditReference = reddit.subreddit(subreddit);
        DefaultPaginator<Submission> paginator = subredditReference.posts().sorting(SubredditSort.TOP) .timePeriod(TimePeriod.ALL) .limit(50).build();

        Listing<Submission> top50MostPopular = paginator.next();

        Submission submission = top50MostPopular.get(new Random().nextInt(49));
        event.getChannel().sendMessage(submission.getUrl()).queue();
    }

}
