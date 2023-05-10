package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PauseCommand extends Command {

  private final Logger LOGGER = LogManager.getLogger(PauseCommand.class);
  private final VoiceChannelService voiceChannelService;

  @Autowired
  public PauseCommand(VoiceChannelService voiceChannelService) {
    this.name = "pause";
    this.help = "Pause the currently playing song.";

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    try {
      voiceChannelService.setPauseStatusOnAudioPlayer(event, true);
    } catch (IllegalArgumentException e) {
      LOGGER.debug("Trying to pause a paused song", e);
      event.getChannel().sendMessage(TextChannelResponses.TRYING_TO_PAUSE_PAUSED_SONG).queue();
      return;
    } catch (IllegalAccessException e) {
      LOGGER.info("Error while running pause command", e);
      return;
    }

    event.reactSuccessOrReply(TextChannelResponses.TRACK_PAUSED);
  }
}
