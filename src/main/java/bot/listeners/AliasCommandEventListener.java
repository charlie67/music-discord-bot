package bot.listeners;

import bot.commands.alias.GuildAliasHolder;
import bot.service.impl.BotServiceImpl;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class AliasCommandEventListener extends ListenerAdapter
{
    private final HashMap<String, GuildAliasHolder> guildIdToGuildAliasHolderMap = new HashMap<>();

    private CommandClient commandClient;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        // Return if it's a bot
        if (event.getAuthor().isBot())
        {
            return;
        }

        String guildID = event.getGuild().getId();
        GuildAliasHolder guildAliasHolder = guildIdToGuildAliasHolderMap.get(guildID);

        if (guildAliasHolder == null)
        {
            return;
        }

        String rawContent = event.getMessage().getContentRaw();

        if (rawContent.startsWith(BotServiceImpl.COMMAND_PREFIX))
        {
            rawContent = rawContent.replace(BotServiceImpl.COMMAND_PREFIX, "");
            String[] parts = rawContent.split("\\s+");

            String commandAsString = parts[0];

            if (guildAliasHolder.doesAliasExistForCommand(commandAsString))
            {
                guildAliasHolder.executeAlias(commandAsString, event, commandClient);
            }
        }
    }

    /**
     * return the GuildAliasHolder that this class is holding will return NULL if a GuildAliasHolder doesn't exist for
     * that guildID
     *
     * @param guildId The guild ID you want the GuildAliasHolder for
     * @return The GuildAliasHolder if it exists or NULL if one doesn't exist for that Guild
     */
    public GuildAliasHolder getGuildAliasHolderForGuildWithId(String guildId)
    {
        return guildIdToGuildAliasHolderMap.get(guildId);
    }

    public void putGuildAliasHolderForGuildWithId(String guildId, GuildAliasHolder guildAliasHolder)
    {
        guildIdToGuildAliasHolderMap.put(guildId, guildAliasHolder);
    }

    public void setCommandClient(CommandClient commandClient)
    {
        this.commandClient = commandClient;
    }
}
