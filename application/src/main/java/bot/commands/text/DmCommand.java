package bot.commands.text;

import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.List;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DmCommand extends Command {
  private static final Logger LOGGER = LogManager.getLogger(DmCommand.class);

  public DmCommand() {
    this.name = "dm";
    this.hidden = true;
    // todo before upgrade make this dm only
    this.allowedInChannels = List.of(ChannelType.PRIVATE);

    this.options =
        List.of(
            Option.createOption(OptionName.DM_USER, true, 0),
            Option.createOption(OptionName.DM_TEXT, true, 1));
  }

  @Override
  protected void execute(CommandEvent event) {
    String rawContent = event.getMessage().getContentRaw();
    String[] queryParts = rawContent.split("\\s+");

    if (queryParts.length < 3) {
      fail(event);
      return;
    }

    String userID = queryParts[1];
    queryParts[0] = "";
    queryParts[1] = "";

    String message = String.join(" ", queryParts).trim();

    User userToDm = event.getJDA().getUserById(userID);

    if (userToDm == null) {
      LOGGER.debug("User ID {} not found", userID);
      fail(event);
      return;
    }

    userToDm.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
  }

  private void fail(CommandEvent event) {
    event.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage("noob").queue());
  }
}
