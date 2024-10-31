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
public class ResumeCommand extends Command {

  private final Logger LOGGER = LogManager.getLogger(ResumeCommand.class);

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public ResumeCommand(VoiceChannelService voiceChannelService) {
    this.name = "resume";
    this.help = "Resume the song if it's been paused.";

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    try {
      voiceChannelService.setPauseStatusOnAudioPlayer(event, false);
    } catch (IllegalArgumentException e) {
      LOGGER.info("Trying to resume a currently playing song", e);
      event.reply(TextChannelResponses.TRYING_TO_RESUME_PLAYING_SONG);
      return;
    } catch (IllegalAccessException e) {
      LOGGER.info("Error while running resume command", e);
      return;
    }

    event.reactSuccessOrReply(TextChannelResponses.TRACK_RESUMED);
  }
}
