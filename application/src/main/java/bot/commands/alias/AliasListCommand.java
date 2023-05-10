package bot.commands.alias;

import static bot.utils.TextChannelResponses.NO_ALIASES_SET;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.EmbedUtils;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import java.util.ArrayList;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliasListCommand extends Command {
  private final AliasEntityRepository aliasEntityRepository;

  @Autowired
  public AliasListCommand(AliasEntityRepository aliasEntityRepository) {
    this.name = "aliaslist";
    this.aliases = new String[] {"al"};
    this.help = "List all the aliases for this server";

    this.aliasEntityRepository = aliasEntityRepository;
  }

  @Override
  protected void execute(CommandEvent event) {
    String guildId = event.getGuild().getId();
    Set<AliasEntity> aliasEntitySet = aliasEntityRepository.findAllByServerId(guildId);

    if (aliasEntitySet.size() == 0) {
      event.reply(NO_ALIASES_SET);
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
