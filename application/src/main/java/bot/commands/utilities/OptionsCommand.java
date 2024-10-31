package bot.commands.utilities;

import bot.entities.OptionEntity;
import bot.repositories.OptionEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.events.CommandEventType;
import bot.utils.command.option.Option;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static bot.utils.TextChannelResponses.NOT_VALID_OPTION;
import static bot.utils.command.option.Response.OPTION_NAME;
import static bot.utils.command.option.Response.OPTION_VALUE;

@Component
public class OptionsCommand extends Command {
	private final OptionEntityRepository optionEntityRepository;
	private final HashMap<Boolean, String> enabledDisabledMap = new HashMap<>();

	public OptionsCommand(OptionEntityRepository optionEntityRepository) {
		this.optionEntityRepository = optionEntityRepository;

		this.enabledDisabledMap.put(Boolean.FALSE, "disabled");
		this.enabledDisabledMap.put(Boolean.TRUE, "enabled");

		this.name = "option";
		this.aliases = new String[]{"setting", "settings"};
		this.help = "Set options for the bot. Current Options:\n";
		this.allowedCommandExecution = List.of(CommandEventType.SLASH);

		this.options = Arrays.asList(Option.createOption(OPTION_NAME, true,
						0), Option.createOption(OPTION_VALUE, true, 1));
	}

	@Override
	protected void execute(CommandEvent event) {
		event.getChannel().sendTyping().queue();

		//command is given as -options OPTIONS_NAME true/false
		//get the arguments and extract them into the different parts

		String guildId = event.getGuild().getId();
		String optionName = event.getOption(OPTION_NAME).getAsString().toLowerCase().trim();

		// todo change to a
		if (!Arrays.stream(OptionName.values()).map(option -> option.getDisplayName().toLowerCase()).collect(Collectors.toSet()).contains(optionName)) {
			event.reply(String.format(NOT_VALID_OPTION, optionName));
			return;
		}

		Boolean booleanValue = event.getOption(OPTION_VALUE).getAsBoolean();

		OptionEntity optionEntity = optionEntityRepository.findByServerIdAndName(guildId, optionName);

		// if an argument was not provided and there is an optionEntity then just invert whatever is currently set.
		if (optionEntity != null && booleanValue == null) {
			booleanValue = !optionEntity.getOption();
		} else if (optionEntity == null) {
			// there is no optionEntity so create one
			optionEntity = new OptionEntity();
			optionEntity.setServerId(guildId);
			optionEntity.setName(optionName);

			// if booleanValue is still null then set it to false
			if (booleanValue == null) {
				booleanValue = Boolean.FALSE;
			}
		}

		optionEntity.setOption(booleanValue);
		optionEntityRepository.save(optionEntity);
		String disabled_enabled_text = enabledDisabledMap.get(booleanValue);

		event.reply(String.format("**%s has been %s.**", optionName, disabled_enabled_text));
	}
}
