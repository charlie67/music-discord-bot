package bot.commands.text;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.events.CommandEventType;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import org.springframework.stereotype.Component;

import java.util.List;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

@Component
public class EchoTextCommand extends Command {
	public EchoTextCommand() {
		this.name = "echotext";
		this.aliases = new String[]{"echo", "text"};
		this.help = "Sends a message with the text that was passed in as an argument";
		this.allowedCommandExecution = List.of(CommandEventType.TEXT);

		this.options = List.of(Option.createOption(Response.ECHO_TEXT, true, 0));
	}

	@Override
	protected void execute(CommandEvent event) {
		String textToReturn = event.getOption(Response.ECHO_TEXT).getAsString();

		if (textToReturn.isEmpty()) {
			event.reply(ECHO_COMMAND_NO_ARGS);
			return;
		}

		event.reply(textToReturn);
	}

//	@Override
//	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
//		final String message = event.getMessage().getContentRaw();
//		String[] parts = message.split("\\s+", 2);
//
//		OptionValue optionValue = TextOptionValue.builder().optionName(OptionName.ECHO_TEXT).optionValue(parts[1]).jda(event.getJDA()).build();
//		return Map.of(OptionName.ECHO_TEXT, optionValue);
//	}
}
