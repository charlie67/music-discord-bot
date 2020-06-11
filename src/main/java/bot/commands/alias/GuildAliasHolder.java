package bot.commands.alias;

import java.util.HashMap;

/**
 * A class that holds the alias's for a single guild there is one of these classes per guild
 */
public class GuildAliasHolder
{
    /**
     * This converts a alias name to a command object that can be executed
     */
    HashMap<String, Alias> aliasNameToAliasObject = new HashMap<>();

    public boolean doesAliasExistForCommand(String command)
    {
        return aliasNameToAliasObject.containsKey(command);
    }

    public void executeAlias(String command)
    {
        Alias alias = aliasNameToAliasObject.get(command);
        alias.execute();
    }

    public void addCommandWithAlias(String command, Alias alias)
    {
        aliasNameToAliasObject.put(command, alias);
    }
}
