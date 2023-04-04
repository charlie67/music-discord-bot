package bot.commands.text;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

import bot.utils.command.Command;
import bot.utils.command.CommandEvent;

public class EchoTextCommand extends Command {
  public EchoTextCommand() {
    this.name = "echotext";
    this.aliases = new String[] {"echo", "text"};
    this.help = "Sends a message with the text that was passed in as an argument";
  }

  @Override
  protected void execute(CommandEvent event) {
    String textToReturn = event.getArgs();

    if (textToReturn.isEmpty()) {
      event.getChannel().sendMessage(ECHO_COMMAND_NO_ARGS).queue();
      return;
    }

    event.getChannel().sendMessage(textToReturn).queue();
  }
}
