package bot.commands.alias;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static bot.utils.TextChannelResponses.ALIAS_DELETE_ALIAS_DOES_NOT_EXIST;
import static bot.utils.TextChannelResponses.ALIAS_DELETE_NONE_PROVIDED;
import static bot.utils.TextChannelResponses.ALIAS_REMOVED;

@Component
public class AliasDeleteCommand extends Command {
	private final AliasEntityRepository aliasEntityRepository;

	@Autowired
	public AliasDeleteCommand(AliasEntityRepository aliasEntityRepository) {
		this.name = "aliasdelete";
		this.help = "Delete an alias with a specific name";
		this.aliases = new String[]{"ad"};
		this.options = List.of(Option.createOption(OptionName.ALIAS_DELETE_NAME, true, 0));

		this.aliasEntityRepository = aliasEntityRepository;
	}

	@Override
	protected void execute(CommandEvent event) {
		String aliasToDelete = event.getOption(OptionName.ALIAS_DELETE_NAME).getAsString();

		if (aliasToDelete.isEmpty()) {
			event.reply(ALIAS_DELETE_NONE_PROVIDED);
			return;
		}

		String guildId = event.getGuild().getId();

		AliasEntity aliasEntityToDelete =
						aliasEntityRepository.findByServerIdAndName(guildId, aliasToDelete);

		if (aliasEntityToDelete == null) {
			event.reply(String.format(ALIAS_DELETE_ALIAS_DOES_NOT_EXIST, aliasToDelete));
			return;
		}

		aliasEntityRepository.delete(aliasEntityToDelete);

		event.reply(String.format(ALIAS_REMOVED, aliasToDelete));
	}
}
