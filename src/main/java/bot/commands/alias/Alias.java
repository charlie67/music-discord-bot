package bot.commands.alias;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Alias
{
    private final Logger LOGGER = LogManager.getLogger(Alias.class);

    private final String aliasCommandArguments;

    private final String aliasName;

    private final Command command;

    public Alias(String aliasName, String aliasCommandArguments, Command command)
    {
        this.aliasName = aliasName;
        this.aliasCommandArguments = aliasCommandArguments;

        this.command = command;
    }

    public void execute(MessageReceivedEvent event, CommandClient commandClient)
    {
        LOGGER.info("Executing alias for command {}", aliasName);
        command.run(new CommandEvent(event, aliasCommandArguments, commandClient));
    }

    public String getAliasCommandArguments()
    {
        return aliasCommandArguments;
    }

    public String getAliasName()
    {
        return aliasName;
    }

    public Command getCommand()
    {
        return command;
    }
}
