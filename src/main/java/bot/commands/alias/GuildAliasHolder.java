package bot.commands.alias;

import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

/**
 * A class that holds the alias's for a single guild there is one of these classes per guild
 */
public class GuildAliasHolder
{
    /**
     * This converts a alias name to a command object that can be executed
     */
    private final HashMap<String, Alias> aliasNameToAliasObject = new HashMap<>();

    public boolean doesAliasExistForCommand(String command)
    {
        return aliasNameToAliasObject.containsKey(command);
    }

    public void executeAlias(String command, MessageReceivedEvent event, CommandClient commandClient)
    {
        Alias alias = aliasNameToAliasObject.get(command);
        alias.execute(event, commandClient);
    }

    public void addCommandWithAlias(String command, Alias alias)
    {
        aliasNameToAliasObject.put(command, alias);
    }

    public Alias getCommandWithAlias(String command)
    {
        return aliasNameToAliasObject.get(command);
    }

    public HashMap<String, Alias> getAliasNameToAliasObject()
    {
        return aliasNameToAliasObject;
    }
}
