package bot.utils;

public class TextChannelResponses
{
    public static final String NOT_CONNECTED_TO_VOICE_MESSAGE = "**You need to be in a voice channel.**";
    public static final String NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND = "**Need to provide something to play**";
    public static final String ERROR_LOADING_VIDEO = "**There was a problem loading that video.**";

    public static final String BOT_NOT_CONNECTED_TO_VOICE = "**Not currently connected to the voice channel**";

    public static final String SEEK_POINT_LONGER_THAN_SONG = "**Cannot seek to a position longer than the song**";
    public static final String SEEK_COMMAND_FORMAT = "**Invalid format**, example formats: \n `0:01`, `1:45`, 2:23:55";
    public static final String SEEKING_TO_INFORMATION = "**Seeking to** `%s`";

    public static final String NO_SUBREDDIT_PROVIDED = "**You need to provide a subreddit to search for.**";
    public static final String UNABLE_TO_SEARCH_FOR_SUBREDDIT = "**Unable to search for that subreddit**";
    public static final String UNABLE_TO_GET_POSTS_FOR_SUBREDDIT = "**Unable to get posts for that subreddit**";
}
