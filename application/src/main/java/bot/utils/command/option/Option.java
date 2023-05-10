package bot.utils.command.option;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public record Option(OptionData optionData, int position, OptionName optionName) {

  public static Option createOption(OptionName optionName, boolean required, int position) {
    return new Option(
        new OptionData(
            optionName.getOptionType(),
            optionName.getDisplayName(),
            optionName.getDescription(),
            required),
        position,
        optionName);
  }
}
