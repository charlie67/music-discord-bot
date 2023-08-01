package bot.commands.text;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Map;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

public class EchoTextCommand extends Command {
	public EchoTextCommand() {
		this.name = "echotext";
		this.aliases = new String[]{"echo", "text"};
		this.help = "Sends a message with the text that was passed in as an argument";

		this.options = List.of(Option.createOption(OptionName.ECHO_TEXT, true, 0));
	}

	@Override
	protected void execute(CommandEvent event) {
		String textToReturn = event.getOption(OptionName.ECHO_TEXT).getAsString();

		if (textToReturn.isEmpty()) {
			event.reply(ECHO_COMMAND_NO_ARGS);
			return;
		}

		event.reply(textToReturn);
	}

	@Override
	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
		final String message = event.getMessage().getContentRaw();
		String[] parts = message.split("\\s+", 2);

		OptionValue optionValue = TextOptionValue.builder().optionName(OptionName.ECHO_TEXT).optionValue(parts[1]).jda(event.getJDA()).build();
		return Map.of(OptionName.ECHO_TEXT, optionValue);
	}
}
