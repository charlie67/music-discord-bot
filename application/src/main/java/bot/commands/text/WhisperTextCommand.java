package bot.commands.text;

import static bot.utils.TextChannelResponses.ECHO_COMMAND_NO_ARGS;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.List;
import net.dv8tion.jda.api.Permission;

public class WhisperTextCommand extends Command {
  public WhisperTextCommand() {
    this.name = "whisper";
    this.aliases = new String[] {"w"};
    this.help = "sshhhh";
    this.botPermissions = new Permission[] {Permission.MESSAGE_MANAGE};
    // todo disable this in slash commands
    this.options = List.of(Option.createOption(OptionName.WHISPER_TEXT, true, 0));
  }

  @Override
  protected void execute(CommandEvent event) {
    event.getMessage().delete().queue();

    String textToReturn = event.getOption(OptionName.WHISPER_TEXT).getAsString();

    if (textToReturn.isEmpty()) {
      event.getChannel().sendMessage(ECHO_COMMAND_NO_ARGS).queue();
      return;
    }

    event.reply(textToReturn);
  }
}
