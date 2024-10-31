package bot.commands.text;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.events.CommandEventType;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import net.dv8tion.jda.api.Permission;
import org.springframework.stereotype.Component;

import java.util.List;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

@Component
public class WhisperTextCommand extends Command {
	public WhisperTextCommand() {
		this.name = "whisper";
		this.aliases = new String[]{"w"};
		this.help = "sshhhh";
		this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
		this.options = List.of(Option.createOption(Response.WHISPER_TEXT, true, 0));
		this.allowedCommandExecution = List.of(CommandEventType.TEXT);
	}

	@Override
	protected void execute(CommandEvent event) {
		event.getMessage().delete().queue();

		String textToReturn = event.getOption(Response.WHISPER_TEXT).getAsString();

		if (textToReturn.isEmpty()) {
			event.getChannel().sendMessage(ECHO_COMMAND_NO_ARGS).queue();
			return;
		}

		event.reply(textToReturn);
	}

//	@Override
//	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
//		final String message = event.getMessage().getContentRaw();
//		String[] parts = message.split("\\s+", 2);
//
//		OptionValue optionValue = TextOptionValue.builder().optionName(OptionName.WHISPER_TEXT).optionValue(parts[1]).jda(event.getJDA()).build();
//		return Map.of(OptionName.WHISPER_TEXT, optionValue);
//	}
}
