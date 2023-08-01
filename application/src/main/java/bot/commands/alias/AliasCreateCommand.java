package bot.commands.alias;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static bot.utils.TextChannelResponses.*;

@Component
public class AliasCreateCommand extends Command {

	private final Logger LOGGER = LogManager.getLogger(AliasCreateCommand.class);
	private final AliasEntityRepository aliasEntityRepository;
	private Set<String> allCurrentCommandNames;
	private Map<String, Command> commandNameToCommandMap;

	@Autowired
	public AliasCreateCommand(AliasEntityRepository aliasEntityRepository) {
		this.name = "aliascreate";
		this.aliases = new String[]{"alias", "ac"};
		this.help = "Create a new alias for a command.";

		this.options =
						List.of(
										Option.createOption(OptionName.ALIAS_CREATE_NAME, true, 0),
										Option.createOption(OptionName.ALIAS_CREATE_COMMAND, true, 1),
										Option.createOption(OptionName.ALIAS_CREATE_ARGS, true, 1));
		this.aliasEntityRepository = aliasEntityRepository;
	}

	@Override
	protected void execute(CommandEvent event) {
		event.deferReply();

		// command is given as -alias NAME_OF_ALIAS <Command to run when alias is called>
		// e.g. -aliascreate song play http://youtube.com/somesong
		// get the arguments and extract them into the different parts

		String aliasName = event.getOption(OptionName.ALIAS_CREATE_NAME).getAsString();
		String aliasCommand = event.getOption(OptionName.ALIAS_CREATE_COMMAND).getAsString();
		String aliasCommandArguments = event.getOption(OptionName.ALIAS_CREATE_ARGS).getAsString();

		if (allCurrentCommandNames.contains(aliasName)) {
			event.reply(String.format(ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND, aliasName));
			return;
		}

		// This is the command that the alias will execute when it is called
		Command command = commandNameToCommandMap.get(aliasCommand);

		if (command == null) {
			event.reply(String.format(ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND, aliasCommand));
			return;
		}

		String guildId = event.getGuild().getId();

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
		AliasEntity existingAlias = aliasEntityRepository.findByServerIdAndName(guildId, aliasName);
		if (existingAlias != null) {
			aliasEntityRepository.delete(existingAlias);
		}

		AliasEntity aliasEntity =
						new AliasEntity()
										.setArgs(aliasCommandArguments)
										.setName(aliasName)
										.setCommand(aliasCommand)
										.setServerId(guildId);

		aliasEntityRepository.save(aliasEntity);

		event
						.getChannel()
						.sendMessage(String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments))
						.queue();

		LOGGER.info(
						"Created alias for server {} with name {} that executes command {} with arguments {}",
						guildId,
						aliasName,
						aliasCommand,
						aliasCommandArguments);
	}

	@Override
	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
		List<String> messageSplit = new ArrayList<>(Arrays.stream(event.getMessage().getContentRaw().split(" ")).toList());
		messageSplit.remove(0); // remove the command name from the list
		if (messageSplit.size() <= 2) {
			OptionValue nameOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_NAME).optionValue("").jda(event.getJDA()).build();
			OptionValue commandOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_COMMAND).optionValue("").jda(event.getJDA()).build();
			OptionValue argsOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_ARGS).optionValue("").jda(event.getJDA()).build();

			return Map.of(OptionName.ALIAS_CREATE_NAME, nameOption, OptionName.ALIAS_CREATE_COMMAND, commandOption, OptionName.ALIAS_CREATE_ARGS, argsOption);
		}

		OptionValue nameOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_NAME).optionValue(messageSplit.get(0)).jda(event.getJDA()).build();
		messageSplit.remove(0); // remove the name option from the list

		OptionValue commandOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_COMMAND).optionValue(messageSplit.get(0)).jda(event.getJDA()).build();
		messageSplit.remove(0); // remove the command option from the list

		OptionValue argsOption = TextOptionValue.builder().optionName(OptionName.ALIAS_CREATE_ARGS).optionValue(String.join(" ", messageSplit)).jda(event.getJDA()).build();

		return Map.of(OptionName.ALIAS_CREATE_NAME, nameOption, OptionName.ALIAS_CREATE_COMMAND, commandOption, OptionName.ALIAS_CREATE_ARGS, argsOption);
	}
}
