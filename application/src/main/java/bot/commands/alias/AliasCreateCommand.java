package bot.commands.alias;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static bot.utils.TextChannelResponses.ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND;
import static bot.utils.TextChannelResponses.ALIAS_CREATED;
import static bot.utils.TextChannelResponses.ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND;
import static bot.utils.TextChannelResponses.ALIAS_TOO_LONG;

@Component
public class AliasCreateCommand extends Command {

	private final Logger LOGGER = LogManager.getLogger(AliasCreateCommand.class);
	private final AliasEntityRepository aliasEntityRepository;
	private final AliasHelperService aliasHelperService;

	@Autowired
	public AliasCreateCommand(final AliasEntityRepository aliasEntityRepository, final AliasHelperService aliasHelperService) {
		this.name = "aliascreate";
		this.aliases = new String[]{"alias", "ac"};
		this.help = "Create a new alias for a command.";

		this.options =
						List.of(
										Option.createOption(Response.ALIAS_CREATE_NAME, true, 0),
										Option.createOption(Response.ALIAS_CREATE_COMMAND, true, 1),
										Option.createOption(Response.ALIAS_CREATE_ARGS, false, 1));
		this.aliasEntityRepository = aliasEntityRepository;
		this.aliasHelperService = aliasHelperService;
	}

	@Override
	protected void execute(final CommandEvent event) {
		// command is given as -alias NAME_OF_ALIAS <Command to run when alias is called>
		// e.g. -aliascreate song play http://youtube.com/somesong
		// get the arguments and extract them into the different parts

		final String aliasName = event.getOption(Response.ALIAS_CREATE_NAME).getAsString();
		final String aliasCommand = event.getOption(Response.ALIAS_CREATE_COMMAND).getAsString();
		final String aliasCommandArguments = event.getOption(Response.ALIAS_CREATE_ARGS).getAsString();

		if (aliasHelperService.isCommandNamePresent(aliasName)) {
			event.reply(String.format(ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND, aliasName));
			return;
		}

		// This is the command that the alias will execute when it is called
		final Optional<Command> command = aliasHelperService.getCommandWithName(aliasCommand);

		if (command.isEmpty()) {
			event.reply(String.format(ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND, aliasCommand));
			return;
		}

		final String guildId = event.getGuild().getId();

		// a Discord message can't be longer than 2000 characters
		if (String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments).length()
						> 1999) {
			LOGGER.error("Tried to create alias that was too long");
			event.getChannel().sendMessage(ALIAS_TOO_LONG).queue();
			return;
		}

		// can't store anything longer than 255 characters in the database
		if (aliasName.length() > 255
						|| aliasCommand.length() > 255
						|| aliasCommandArguments.length() > 255) {
			event.getChannel().sendMessage(ALIAS_TOO_LONG).queue();
			return;
		}

		// if the alias already exists delete it
		final AliasEntity existingAlias = aliasEntityRepository.findByServerIdAndName(guildId, aliasName);
		if (existingAlias != null) {
			aliasEntityRepository.delete(existingAlias);
		}

		final AliasEntity aliasEntity = AliasEntity.builder()
						.args(aliasCommandArguments)
						.name(aliasName)
						.command(aliasCommand)
						.serverId(guildId)
						.build();

		aliasEntityRepository.save(aliasEntity);

		event.reply(String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments));

		LOGGER.info(
						"Created alias for server {} with name {} that executes command {} with arguments {}",
						guildId,
						aliasName,
						aliasCommand,
						aliasCommandArguments);
	}

	public Map<Response, OptionValue> createOptionMap(final MessageReceivedEvent event) {
		final List<String> messageSplit = new ArrayList<>(Arrays.stream(event.getMessage().getContentRaw().split(" ")).toList());
		messageSplit.remove(0); // remove the command name from the list
		if (messageSplit.size() <= 2) {
			final OptionValue nameOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_NAME).optionValue("").jda(event.getJDA()).build();
			final OptionValue commandOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_COMMAND).optionValue("").jda(event.getJDA()).build();
			final OptionValue argsOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_ARGS).optionValue("").jda(event.getJDA()).build();

			return Map.of(Response.ALIAS_CREATE_NAME, nameOption, Response.ALIAS_CREATE_COMMAND, commandOption, Response.ALIAS_CREATE_ARGS, argsOption);
		}

		final OptionValue nameOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_NAME).optionValue(messageSplit.get(0)).jda(event.getJDA()).build();
		messageSplit.remove(0); // remove the name option from the list

		final OptionValue commandOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_COMMAND).optionValue(messageSplit.get(0)).jda(event.getJDA()).build();
		messageSplit.remove(0); // remove the command option from the list

		final OptionValue argsOption = TextOptionValue.builder().optionName(Response.ALIAS_CREATE_ARGS).optionValue(String.join(" ", messageSplit)).jda(event.getJDA()).build();

		return Map.of(Response.ALIAS_CREATE_NAME, nameOption, Response.ALIAS_CREATE_COMMAND, commandOption, Response.ALIAS_CREATE_ARGS, argsOption);
	}
}
