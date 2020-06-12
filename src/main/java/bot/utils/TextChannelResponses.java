package bot.utils;

public class TextChannelResponses
{
    public static final String NOT_CONNECTED_TO_VOICE_MESSAGE = "**You need to be in a voice channel.**";
    public static final String NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND = "**Need to provide something to play**";
    public static final String ERROR_LOADING_VIDEO = "**There was a problem loading that video.**";
    public static final String DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL = "**Do not have permission to join your voice " +
            "channel**";

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

    public static final String NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS = "**Need more arguments to create an alias" +
            ".**\n**Create an alias with** `-aliascreate ALIAS_NAME <Command to run when ALIAS_NAME is called>`";
    public static final String ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND = "**%s is already used as the name of a command**";
    public static final String ALIAS_CREATED = "**Alias created with name %s that executes command %s with arguments %s**";
    public static final String ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND = "**%s is not a command**";
}
