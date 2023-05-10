package bot.commands.text;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.List;

public class EchoTextCommand extends Command {
  public EchoTextCommand() {
    this.name = "echotext";
    this.aliases = new String[] {"echo", "text"};
    this.help = "Sends a message with the text that was passed in as an argument";

    this.options = List.of(Option.createOption(OptionName.ECHO_TEXT, true, 0));
  }

  @Override
  protected void execute(CommandEvent event) {
    String textToReturn = event.getOption(OptionName.ECHO_TEXT).getAsString();

    if (textToReturn.isEmpty()) {
      event.reply(ECHO_COMMAND_NO_ARGS);
      return;
    }

    event.reply(textToReturn);
  }
}
