package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

import static bot.utils.TextChannelResponses.ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND;
import static bot.utils.TextChannelResponses.ALIAS_CREATED;
import static bot.utils.TextChannelResponses.ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND;
import static bot.utils.TextChannelResponses.NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS;

public class AliasCreateCommand extends Command
{
    private final Logger LOGGER = LogManager.getLogger(AliasCreateCommand.class);


    private Set<String> allCurrentCommandNames;

    private HashMap<String, Command> commandNameToCommandMap;

    private final AliasCommandEventListener aliasCommandEventListener;

    public AliasCreateCommand(AliasCommandEventListener aliasCommandEventListener)
    {
        this.name = "aliascreate";
        this.aliases = new String[]{"alias", "ac"};
        this.help = "Create a command alias";

        this.aliasCommandEventListener = aliasCommandEventListener;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        //command is given as -alias NAME_OF_ALIAS <Command to run when alias is called>
        //e.g. -aliascreate song play http://youtube.com/somesong
        //get the arguments and exxtract them into the different parts
        String[] arguments = event.getArgs().split("\\s+");

        //check that at least 3 arguments are specified
        if (arguments.length < 3)
        {
            event.getChannel().sendMessage(NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS).queue();
            return;
        }

        String aliasName = arguments[0].toLowerCase();
        String aliasCommand = arguments[1].toLowerCase();
        String aliasCommandArguments = arguments[2];

        if (allCurrentCommandNames.contains(aliasName))
        {
            event.getChannel().sendMessage(String.format(ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND, aliasName)).queue();
            return;
        }

        // This is the command that the alias will execute when it is called
        Command command = commandNameToCommandMap.get(aliasCommand);

        if (command == null)
        {
            event.getChannel().sendMessage(String.format(ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND, aliasCommand)).queue();
            return;
        }

        String guildId = event.getGuild().getId();

        Alias alias = new Alias(aliasName, aliasCommandArguments, command);

        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildId);

        if (guildAliasHolder == null)
        {
            guildAliasHolder = new GuildAliasHolder();
            aliasCommandEventListener.putGuildAliasHolderForGuildWithId(guildId, guildAliasHolder);
        }

        guildAliasHolder.addCommandWithAlias(aliasName, alias);
        event.getChannel().sendMessage(String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments)).queue();

        LOGGER.info("Created alias for server {} with name {} that executes command {} with arguments {}", guildId,
                aliasName, aliasCommand, aliasCommandArguments);
    }

    public void setAllCurrentCommandNames(Set<String> allCurrentCommandNames)
    {
        this.allCurrentCommandNames = allCurrentCommandNames;
    }

    public void setCommandNameToCommandMap(HashMap<String, Command> commandNameToCommandMap)
    {
        this.commandNameToCommandMap = commandNameToCommandMap;
    }
}
