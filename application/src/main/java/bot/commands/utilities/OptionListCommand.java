package bot.commands.utilities;

import bot.entities.OptionEntity;
import bot.repositories.OptionEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class OptionListCommand extends Command {
	private final OptionEntityRepository optionEntityRepository;

	public OptionListCommand(OptionEntityRepository optionEntityRepository) {
		this.optionEntityRepository = optionEntityRepository;

		this.name = "optionlist";
		this.aliases = new String[]{"settingslist", "settinglist", "optionslist"};
		this.help = "Show a list of the current settings.";
	}

	@Override
	protected void execute(CommandEvent event) {
		HashMap<String, Boolean> nameToState = new HashMap<>();

		// get the settings and then
		for (OptionName optionName : OptionName.values()) {
			OptionEntity optionEntity =
							optionEntityRepository.findByServerIdAndName(event.getGuild().getId(), optionName.getDisplayName().toLowerCase());

			if (optionEntity == null ) {
				nameToState.put(optionName.getDisplayName(), optionName.isDefaultValue());
			} else {
				nameToState.put(optionName.getDisplayName(), optionEntity.getOption());
			}
		}

		EmbedBuilder eb = new EmbedBuilder();

		// get a random colour for the embed
		Random rand = new Random();
		eb.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));

		AtomicInteger ordinal = new AtomicInteger(1);
		StringBuilder sb = new StringBuilder();

		nameToState.forEach(
						(setting_name, value) -> {
							int itemPosition = ordinal.getAndIncrement();

							sb.append(String.format("`%d.` %s - %s\n\n", itemPosition, setting_name, value));
						});

		eb.setDescription(sb);
		event.reply(eb.build());
	}
}
