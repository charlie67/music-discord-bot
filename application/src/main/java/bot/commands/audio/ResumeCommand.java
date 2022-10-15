package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.UnicodeEmote;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
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
      LOGGER.debug("Trying to resume a currently playing song", e);
      event.getChannel().sendMessage(TextChannelResponses.TRYING_TO_RESUME_PLAYING_SONG).queue();
      return;
    } catch (IllegalAccessException e) {
      LOGGER.debug("Error while running resume command", e);
      return;
    }
    event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
  }
}
