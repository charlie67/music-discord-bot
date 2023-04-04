package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoopCommand extends Command {

  private final Logger LOGGER = LogManager.getLogger(LoopCommand.class);

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public LoopCommand(VoiceChannelService voiceChannelService) {
    this.name = "loop";
    this.help = "Enable or disable the current song from looping.";

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    AudioPlayerSendHandler audioPlayerSendHandler;
    try {
      audioPlayerSendHandler =
          voiceChannelService.getAudioPlayerSendHandler(event.getJDA(), event.getGuild().getId());
    } catch (IllegalArgumentException e) {
      event.getChannel().sendMessage(TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE).queue();
      return;
    }

    TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

    AudioTrack nowPlaying = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();
    AudioTrack loopTrack = trackScheduler.getLoopTrack();

    if (nowPlaying == null) {
      event.getChannel().sendMessage(TextChannelResponses.NOTHING_CURRENTLY_PLAYING).queue();
      return;
    }

    if (loopTrack == null) {
      trackScheduler.setLoopTrack(nowPlaying);
      event.getChannel().sendMessage(TextChannelResponses.LOOP_ENABLED).queue();
      LOGGER.info("Loop enabled");
    } else {
      trackScheduler.setLoopTrack(null);
      event.getChannel().sendMessage(TextChannelResponses.LOOP_DISABLED).queue();
      LOGGER.info("Loop disabled");
    }
  }
}
