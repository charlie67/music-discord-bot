package bot.commands.text;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.events.SlashCommandEvent;
import bot.utils.command.events.TextCommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class DmCommand extends Command {
	private static final Logger LOGGER = LogManager.getLogger(DmCommand.class);

	public DmCommand() {
		this.name = "dm";
		this.hidden = true;
		// todo before upgrade make this dm only
		this.dmOnly = true;

		this.options = List.of(Option.createOption(OptionName.DM_USER, true, 0), Option.createOption(OptionName.DM_TEXT, true, 1));
	}

	@Override
	protected void execute(CommandEvent event) {
		String rawContent = event.getMessage().getContentRaw();
		String[] queryParts = rawContent.split("\\s+");

		if (queryParts.length < 3) {
			fail(event);
			return;
		}

		String userID = queryParts[1];
		queryParts[0] = "";
		queryParts[1] = "";

		String message = String.join(" ", queryParts).trim();

		User userToDm = event.getJDA().getUserById(userID);

		if (userToDm == null) {
			LOGGER.debug("User ID {} not found", userID);
			fail(event);
			return;
		}

		userToDm.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
	}

	@Override
	public Map<OptionName, OptionMapping> extractOptions(TextCommandEvent event) {
		Map<OptionName, OptionMapping> map = new HashedMap<>();

		String[] argsSplit = event.getArgs().split(" ");

		map.put(OptionName.DM_USER, new OptionMapping(DataObject.empty()), event.getJDA(), event.getGuild());
		return map;
	}

	@Override
	public Map<OptionName, OptionMapping> extractOptions(SlashCommandEvent event) {
		Map<OptionName, OptionMapping> map = new HashedMap<>();

		for (Option option : this.options) {
			OptionName optionName = option.getOptionName();
			map.put(optionName, event.getOption(optionName));
		}
		return map;
	}

	private void fail(CommandEvent event) {
		event.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage("noob").queue());
	}
}
