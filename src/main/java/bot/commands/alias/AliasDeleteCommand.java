package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import bot.repositories.GuildAliasHolderEntityRepository;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static bot.utils.TextChannelResponses.ALIAS_DELETE_ALIAS_DOES_NOT_EXIST;
import static bot.utils.TextChannelResponses.ALIAS_DELETE_NONE_PROVIDED;
import static bot.utils.TextChannelResponses.ALIAS_REMOVED;
import static bot.utils.TextChannelResponses.ERROR_OCCURRED_WHEN_REMOVING_ALIAS;

public class AliasDeleteCommand extends Command
{
    private final Logger LOGGER = LogManager.getLogger(AliasDeleteCommand.class);

    private final AliasCommandEventListener aliasCommandEventListener;

    private final GuildAliasHolderEntityRepository guildAliasHolderEntityRepository;

    public AliasDeleteCommand(AliasCommandEventListener aliasCommandEventListener,
                              GuildAliasHolderEntityRepository guildAliasHolderEntityRepository)
    {
        this.name = "aliasdelete";
        this.help = "Delete an alias with a specific name";
        this.aliases = new String[]{"ad"};

        this.aliasCommandEventListener = aliasCommandEventListener;
        this.guildAliasHolderEntityRepository = guildAliasHolderEntityRepository;
    }


    @Override
    protected void execute(CommandEvent event)
    {
        String aliasToDelete = event.getArgs();

        if (aliasToDelete.isEmpty())
        {
            event.getChannel().sendMessage(ALIAS_DELETE_NONE_PROVIDED).queue();
            return;
        }

        String guildId = event.getGuild().getId();

        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildId);

        if (!guildAliasHolder.doesAliasExistForCommand(aliasToDelete))
        {
            event.getChannel().sendMessage(String.format(ALIAS_DELETE_ALIAS_DOES_NOT_EXIST, aliasToDelete)).queue();
            return;
        }

        try
        {
            guildAliasHolder.removeCommandWithAlias(aliasToDelete);
        }
        catch(IllegalArgumentException e)
        {
            LOGGER.error("Error occurred when deleting alias for guild {}", guildId, e);
            event.getChannel().sendMessage(ERROR_OCCURRED_WHEN_REMOVING_ALIAS).queue();
            return;
        }

        guildAliasHolderEntityRepository.save(guildAliasHolder);

        event.getChannel().sendMessage(String.format(ALIAS_REMOVED, aliasToDelete)).queue();
    }
}
