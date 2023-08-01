package bot.commands.audio.play;

import bot.utils.command.Command;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;

public abstract class PlayBaseCommand extends Command {
	@Override
	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
		final String message = event.getMessage().getContentRaw();
		String[] parts = message.split("\\s+", 2);

		if (parts.length < 2) {
			return Map.of();
		}

		OptionValue optionValue = TextOptionValue.builder().optionName(OptionName.PLAY_AUDIO_SONG_NAME).optionValue(parts[1]).jda(event.getJDA()).build();
		return Map.of(OptionName.PLAY_AUDIO_SONG_NAME, optionValue);
	}
}
