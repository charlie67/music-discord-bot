package bot.commands.alias;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Alias
{
    private final Logger LOGGER = LogManager.getLogger(Alias.class);

    private final String aliasCommand;

    private final String aliasCommandArguments;

    private final String aliasName;

    private final String guildId;

    public Alias(String aliasName, String aliasCommand, String aliasCommandArguments, String guildId)
    {
        this.aliasName = aliasName;
        this.aliasCommand = aliasCommand;
        this.aliasCommandArguments = aliasCommandArguments;
        this.guildId = guildId;
    }

    public void execute()
    {
        LOGGER.info("Executing alias for command {}", aliasName);
    }
}
