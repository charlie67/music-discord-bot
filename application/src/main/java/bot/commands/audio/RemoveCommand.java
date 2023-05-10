package bot.commands.audio;

import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NO_TRACK_TO_REMOVE;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.List;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoveCommand extends Command {
  private final Logger LOGGER = LogManager.getLogger(RemoveCommand.class);

  public RemoveCommand() {
    this.name = "remove";
    this.help = "Remove the requested song from the queue.";

    this.options = List.of(Option.createOption(OptionName.REMOVE_POSITION, true, 0));
  }

  @Override
  protected void execute(CommandEvent event) {
    int trackToRemove = event.getOption(OptionName.REMOVE_POSITION).getAsInt();

    LOGGER.info("Removing track {} from the queue", trackToRemove);

    AudioManager audioManager = event.getGuild().getAudioManager();

    AudioPlayerSendHandler audioPlayerSendHandler =
        (AudioPlayerSendHandler) audioManager.getSendingHandler();

    if (audioPlayerSendHandler == null) {
      event
          .getChannel()
          .sendMessage(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, trackToRemove))
          .queue();
      return;
    }

    TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

    try {
      trackScheduler.remove(trackToRemove - 1);
    } catch (IndexOutOfBoundsException e) {
      LOGGER.info("Track {} is not a track on the queue", trackToRemove);
      event
          .getChannel()
          .sendMessage(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, trackToRemove))
          .queue();
      return;
    }

    event.reactSuccess();
  }
}
