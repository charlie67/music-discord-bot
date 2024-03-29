package bot.commands.alias;

import static bot.utils.TextChannelResponses.ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND;
import static bot.utils.TextChannelResponses.ALIAS_CREATED;
import static bot.utils.TextChannelResponses.ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND;
import static bot.utils.TextChannelResponses.ALIAS_TOO_LONG;
import static bot.utils.TextChannelResponses.HOW_TO_MAKE_ALIAS;
import static bot.utils.TextChannelResponses.NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS;

import bot.entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliasCreateCommand extends Command {

  private final Logger LOGGER = LogManager.getLogger(AliasCreateCommand.class);
  private final AliasEntityRepository aliasEntityRepository;
  private Set<String> allCurrentCommandNames;
  private Map<String, Command> commandNameToCommandMap;

  @Autowired
  public AliasCreateCommand(AliasEntityRepository aliasEntityRepository) {
    this.name = "aliascreate";
    this.aliases = new String[]{"alias", "ac"};
    this.help = "Create a new alias for a command. Created using " + HOW_TO_MAKE_ALIAS;

    CommandData commandData = new CommandData(this.name, "Create a new alias for a command.");
    commandData.addOptions(new OptionData(OptionType.STRING, "command",
        "The command that this alias will " +
            "execute when" +
            " called", true), new OptionData(OptionType.STRING, "options",
        "The arguments that will be passed to" +
            " the alias command when this is executed", false));
    this.commandData = commandData;
    this.allowedInSlash = false;

    this.aliasEntityRepository = aliasEntityRepository;
  }

  @Override
  public void executeSlashCommand(SlashCommandEvent event) {
    event.deferReply().queue();
    event.getHook().sendMessage("ummm this is awkward").queue();
  }

  @Override
  protected void execute(CommandEvent event) {
    event.getChannel().sendTyping().queue();

    //command is given as -alias NAME_OF_ALIAS <Command to run when alias is called>
    //e.g. -aliascreate song play http://youtube.com/somesong
    //get the arguments and extract them into the different parts
    String[] arguments = event.getArgs().split("\\s+");

    //check that at least 3 arguments are specified
    if (arguments.length < 2) {
      event.getChannel().sendMessage(NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS).queue();
      return;
    }

    String aliasName = arguments[0].toLowerCase();
    String aliasCommand = arguments[1].toLowerCase();
    String aliasCommandArguments =
        arguments.length < 3 ? " " : sliceArgumentsToString(arguments, 2, arguments.length);

    if (allCurrentCommandNames.contains(aliasName)) {
      event.getChannel()
          .sendMessage(String.format(ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND, aliasName))
          .queue();
      return;
    }

    // This is the command that the alias will execute when it is called
    Command command = commandNameToCommandMap.get(aliasCommand);

    if (command == null) {
      event.getChannel()
          .sendMessage(String.format(ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND, aliasCommand))
          .queue();
      return;
    }

    String guildId = event.getGuild().getId();

    // a Discord message can't be longer than 2000 characters
    if (String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments).length()
        > 1999) {
      LOGGER.error("Tried to create alias that was too long");
      event.getChannel().sendMessage(ALIAS_TOO_LONG).queue();
      return;
    }

    // can't store anything longer than 255 characters in the database
    if (aliasName.length() > 255 || aliasCommand.length() > 255
        || aliasCommandArguments.length() > 255) {
      event.getChannel().sendMessage(ALIAS_TOO_LONG).queue();
      return;
    }

    // if the alias already exists delete it
    AliasEntity existingAlias = aliasEntityRepository.findByServerIdAndName(guildId, aliasName);
    if (existingAlias != null) {
      aliasEntityRepository.delete(existingAlias);
    }

    AliasEntity aliasEntity = new AliasEntity()
        .setArgs(aliasCommandArguments)
        .setName(aliasName)
        .setCommand(aliasCommand)
        .setServerId(guildId);

    aliasEntityRepository.save(aliasEntity);

    event.getChannel()
        .sendMessage(
            String.format(ALIAS_CREATED, aliasName, aliasCommand, aliasCommandArguments))
        .queue();

    LOGGER.info(
        "Created alias for server {} with name {} that executes command {} with arguments {}",
        guildId,
        aliasName, aliasCommand, aliasCommandArguments);
  }

  String sliceArgumentsToString(String[] arr, int start, int end) {
    // Get the slice of the Array
    String[] slice = new String[end - start];

    // Copy elements of arr to slice
    if (slice.length >= 0) {
      System.arraycopy(arr, start, slice, 0, slice.length);
    }

    // return the slice
    return Arrays.toString(slice).replace(",", "").replace("[", "").replace("]", "");
  }

  public void setAllCurrentCommandNames(Set<String> allCurrentCommandNames) {
    this.allCurrentCommandNames = allCurrentCommandNames;
  }

  public void setCommandNameToCommandMap(Map<String, Command> commandNameToCommandMap) {
    this.commandNameToCommandMap = commandNameToCommandMap;
  }
}
