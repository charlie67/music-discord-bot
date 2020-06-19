package bot.utils;

public class TextChannelResponses
{
    public static final String NOT_CONNECTED_TO_VOICE_MESSAGE = "**You need to be in a voice channel.**";
    public static final String NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND = "**Need to provide something to play**";
    public static final String ERROR_LOADING_VIDEO = "**There was a problem loading that video.**";
    public static final String DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL = "**Do not have permission to join your voice " +
            "channel**";

    public static final String REMOVE_COMMAND_NO_ARGUMENT = "**No song was provided to remove**";
    public static final String REMOVE_COMMAND_NOT_A_NUMBER = "**%s is not a number**";
    public static final String REMOVE_COMMAND_NO_TRACK_TO_REMOVE = "**There is not a track at position %d on the queue**";

    public static final String NOTHING_CURRENTLY_PLAYING = "**Nothing is currently playing**";

    public static final String LOOP_ENABLED = "**Loop enabled!**";
    public static final String LOOP_DISABLED = "**Loop disabled!**";

    public static final String BOT_NOT_CONNECTED_TO_VOICE = "**Not currently connected to the voice channel**";

    public static final String SEEK_POINT_LONGER_THAN_SONG = "**Cannot seek to a position longer than the song**";
    public static final String SEEK_COMMAND_FORMAT = "**Invalid format**, example formats: \n `0:01`, `1:45`, '2:23:55'";
    public static final String SEEKING_TO_INFORMATION = "**Seeking to** `%s`";

    public static final String TRYING_TO_RESUME_PLAYING_SONG = "**The song is not paused.**";
    public static final String TRYING_TO_PAUSE_PAUSED_SONG = "**Song is already paused.**";

    public static final String NO_SUBREDDIT_PROVIDED = "**You need to provide a subreddit to search for.**";
    public static final String UNABLE_TO_SEARCH_FOR_SUBREDDIT = "**Unable to search for that subreddit**";
    public static final String UNABLE_TO_GET_POSTS_FOR_SUBREDDIT = "**Unable to get posts for that subreddit**";
}
