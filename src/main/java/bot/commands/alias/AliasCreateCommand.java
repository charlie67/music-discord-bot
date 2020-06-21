package bot.commands.alias;

import bot.Entities.GuildAliasHolderEntity;
import bot.listeners.AliasCommandEventListener;
import bot.repositories.GuildAliasHolderEntityRepository;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static bot.utils.TextChannelResponses.ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND;
import static bot.utils.TextChannelResponses.ALIAS_CREATED;
import static bot.utils.TextChannelResponses.ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND;
import static bot.utils.TextChannelResponses.ERROR_OCCURRED_CREATING_ALIAS;
import static bot.utils.TextChannelResponses.HOW_TO_MAKE_ALIAS;
import static bot.utils.TextChannelResponses.NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS;

public class AliasCreateCommand extends Command
{
    private final Logger LOGGER = LogManager.getLogger(AliasCreateCommand.class);

    private Set<String> allCurrentCommandNames;

    private final GuildAliasHolderEntityRepository guildAliasHolderEntityRepository;

    private HashMap<String, Command> commandNameToCommandMap;

    private final AliasCommandEventListener aliasCommandEventListener;

    public AliasCreateCommand(AliasCommandEventListener aliasCommandEventListener,
                              GuildAliasHolderEntityRepository guildAliasHolderEntityRepository)
    {
        this.name = "aliascreate";
        this.aliases = new String[]{"alias", "ac"};
        this.help = "Create a new alias for a command. Created using " + HOW_TO_MAKE_ALIAS;

        this.aliasCommandEventListener = aliasCommandEventListener;
        this.guildAliasHolderEntityRepository = guildAliasHolderEntityRepository;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        //command is given as -alias NAME_OF_ALIAS <Command to run when alias is called>
        //e.g. -aliascreate song play http://youtube.com/somesong
        //get the arguments and exxtract them into the different parts
        String[] arguments = event.getArgs().split("\\s+");

        //check that at least 3 arguments are specified
        if (arguments.length < 2)
        {
            event.getChannel().sendMessage(NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS).queue();
            return;
        }

        String aliasName = arguments[0].toLowerCase();
        String aliasCommand = arguments[1].toLowerCase();
        String aliasCommandArguments = arguments.length < 3 ? " " : sliceArgumentsToString(arguments, 2, arguments.length);

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
            guildAliasHolder = new GuildAliasHolder(guildId);
            aliasCommandEventListener.putGuildAliasHolderForGuildWithId(guildId, guildAliasHolder);
        }

        if (guildAliasHolder.doesAliasExistForCommand(aliasName))
        {
            try
            {
                guildAliasHolder.removeCommandWithAlias(aliasName);
            }
            catch(IllegalArgumentException e)
            {
                LOGGER.error("Error when removing alias for server {}", guildId, e);
                event.getChannel().sendMessage(String.format(ERROR_OCCURRED_CREATING_ALIAS, aliasName)).queue();
                return;
            }
        }

        guildAliasHolder.addCommandWithAlias(aliasName, alias);
        guildAliasHolderEntityRepository.save((GuildAliasHolderEntity) guildAliasHolder);

        event.getChannel().sendMessage(String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments)).queue();

        LOGGER.info("Created alias for server {} with name {} that executes command {} with arguments {}", guildId,
                aliasName, aliasCommand, aliasCommandArguments);
    }

    String sliceArgumentsToString(String[] arr, int start, int end)
    {
        // Get the slice of the Array
        String[] slice = new String[end - start];

        // Copy elements of arr to slice
        if (slice.length >= 0) System.arraycopy(arr, start, slice, 0, slice.length);

        // return the slice
        return Arrays.toString(slice).replace(",", "").replace("[", "").replace("]", "");
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
