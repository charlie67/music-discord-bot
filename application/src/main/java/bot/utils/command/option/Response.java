package bot.utils.command.option;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Getter
public enum Response {
	// play command
	PLAY_ARGUMENT("link-or-query", "URL to play or search term", OptionType.STRING),
	PLAY_AUDIO_SONG_POSITION("position", "Position to play the song at", OptionType.INTEGER),

	// skipto command
	SKIP_TO_POSITION("position", "Position to skip to", OptionType.INTEGER),

	// reddit search command
	SEARCH_SUBREDDIT_OPTION("subreddit", "The subreddit to search for", OptionType.STRING),

	// seek command
	SEEK_POSITION(
					"position", "Position to seek to. e.g. 1:25 for 1 minute and 25 seconds", OptionType.STRING),

	// remove command
	REMOVE_POSITION("track-to-remove", "Track position to remove", OptionType.INTEGER),

	// DM command
	DM_USER("user", "User to send the message to", OptionType.STRING),
	DM_TEXT("text", "Text to send", OptionType.STRING),

	// create alias
	ALIAS_CREATE_NAME("name", "Name of the alias", OptionType.STRING),
	ALIAS_CREATE_COMMAND("command", "Command to create the alias for", OptionType.STRING),
	ALIAS_CREATE_ARGS("args", "Arguments to pass to the command", OptionType.STRING),

	// delete alias
	ALIAS_DELETE_NAME("name", "Name of the alias to delete", OptionType.STRING),

	// search alias
	ALIAS_SEARCH_NAME("query", "Name of the alias to search for", OptionType.STRING),

	// echo command
	ECHO_TEXT("text", "Text to send", OptionType.STRING),

	// option command
	OPTION_NAME("option-name", "Name of the option to update", OptionType.STRING),
	OPTION_VALUE("option-value", "The value to set. true/false", OptionType.BOOLEAN),

	// whisper command
	WHISPER_TEXT("whisper-text", "Text to whisper", OptionType.STRING);

	private final String displayName;
	private final String description;
	private final OptionType optionType;

	Response(String displayName, String description, OptionType optionType) {
		this.displayName = displayName;
		this.description = description;
		this.optionType = optionType;
	}
}
