package bot.commands.alias;

import static bot.utils.TextChannelResponses.NO_ALIASES_SET;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.EmbedUtils;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliasSearchCommand extends Command {
  private final AliasEntityRepository aliasEntityRepository;

  @Autowired
  public AliasSearchCommand(AliasEntityRepository aliasEntityRepository) {
    this.name = "aliassearch";
    this.aliases = new String[] {"as"};
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
      String aliasListString =
          "`"
              + i
              + ":` `"
              + alias.getName()
              + "` executes command `"
              + alias.getCommand()
              + "` with arguments `"
              + alias.getArgs()
              + "`"
              + "\n";
      i++;
      eachAliasDescription.add(aliasListString);
    }

    EmbedUtils.splitTextListsToSend(eachAliasDescription, event.getChannel());
  }
}
