package bot.commands.utilities;

import static bot.utils.SettingsCommands.AUTOPLAY_NAME;
import static bot.utils.TextChannelResponses.NOT_VALID_OPTION;

import bot.entities.OptionEntity;
import bot.repositories.OptionEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class OptionsCommand extends Command {

  static final String[] OPTION_NAMES = new String[] {AUTOPLAY_NAME};
  private final OptionEntityRepository optionEntityRepository;
  private final HashMap<Boolean, String> enabledDisabledMap = new HashMap<>();

  public OptionsCommand(OptionEntityRepository optionEntityRepository) {
    this.optionEntityRepository = optionEntityRepository;

    this.enabledDisabledMap.put(Boolean.FALSE, "disabled");
    this.enabledDisabledMap.put(Boolean.TRUE, "enabled");

    this.name = "option";
    this.aliases = new String[] {"setting", "settings"};
    this.help = "Set options for the bot. Use -optionlist to see current options.";

    this.options =
        List.of(
            Option.createOption(OptionName.OPTION_NAME, true, 0),
            Option.createOption(OptionName.OPTION_VALUE, false, 1));
  }

  @Override
  protected void execute(CommandEvent event) {
    event.deferReply();

    String guildId = event.getGuild().getId();
    String optionName =
        event.getOption(OptionName.OPTION_NAME).getAsString().toLowerCase(Locale.ROOT);

    if (!Arrays.asList(OPTION_NAMES).contains(optionName)) {
      event.getChannel().sendMessage(String.format(NOT_VALID_OPTION, optionName)).queue();
      return;
    }

    Boolean booleanValue = null;
    // check if an argument was provided
    if (event.optionPresent(OptionName.OPTION_VALUE)) {
      booleanValue = event.getOption(OptionName.OPTION_VALUE).getAsBoolean();
    }

    OptionEntity optionEntity = optionEntityRepository.findByServerIdAndName(guildId, optionName);

    // if an argument was not provided and there is an optionEntity then just invert whatever is
    // currently set.
    if (optionEntity != null && booleanValue == null) {
      booleanValue = !optionEntity.getOption();
    } else if (optionEntity == null) {
      // there is no optionEntity so create one
      optionEntity = new OptionEntity();
      optionEntity.setServerId(guildId);
      optionEntity.setName(optionName);

      // if booleanValue is still null then set it to false (all options default to true when
      // created)
      if (booleanValue == null) {
        booleanValue = Boolean.FALSE;
      }
    }

    optionEntity.setOption(booleanValue);
    optionEntityRepository.save(optionEntity);
    String disabled_enabled_text = enabledDisabledMap.get(booleanValue);

    event
        .getChannel()
        .sendMessage(String.format("**%s has been %s.**", optionName, disabled_enabled_text))
        .queue();
  }
}
