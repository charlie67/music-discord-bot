package bot.commands.alias;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.EmbedUtils;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static bot.utils.TextChannelResponses.NO_ALIASES_SET;

@Component
public class AliasSearchCommand extends Command {
	private final AliasEntityRepository aliasEntityRepository;

	@Autowired
	public AliasSearchCommand(AliasEntityRepository aliasEntityRepository) {
		this.name = "aliassearch";
		this.aliases = new String[]{"as"};
		this.help = "Search all the aliases in this server";
		this.options = List.of(Option.createOption(OptionName.ALIAS_SEARCH_NAME, true, 0));

		this.aliasEntityRepository = aliasEntityRepository;
	}

	@Override
	protected void execute(CommandEvent event) {
		String guildId = event.getGuild().getId();
		String nameContains = event.getOption(OptionName.ALIAS_SEARCH_NAME).getAsString();

		if (nameContains.isEmpty()) {
			event.reply(TextChannelResponses.ALIAS_SEARCH_NOT_PROVIDED);
			return;
		}

		Set<AliasEntity> aliasEntitySet =
						aliasEntityRepository.findAliasEntityByNameContainingAndServerId(guildId, nameContains);

		if (aliasEntitySet.isEmpty()) {
			event.getChannel().sendMessage(NO_ALIASES_SET).queue();
			return;
		}

		// can only send 2000 characters in a single message so put each alias description onto
		// eachAliasDescription and
		// later combine ones that are less than 2000 characters into their own messages
		ArrayList<String> eachAliasDescription = new ArrayList<>();

		int i = 1;

		for (AliasEntity alias : aliasEntitySet) {
			StringBuilder aliasListString = new StringBuilder();
			aliasListString.append("`").append(i).append(":` `");
			aliasListString.append(alias.getName());
			aliasListString.append("` executes command `");
			aliasListString.append(alias.getCommand());
			aliasListString.append("` with arguments `");
			aliasListString.append(alias.getArgs());
			aliasListString.append("`");
			aliasListString.append("\n");
			i++;
			eachAliasDescription.add(aliasListString.toString());
		}

		EmbedUtils.splitTextListsToSend(eachAliasDescription, event.getChannel());
	}
}
