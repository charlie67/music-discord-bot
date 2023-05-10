package bot.commands.audio;

import bot.configuration.BotConfiguration;
import bot.dao.OptionEntityDao;
import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayCommand extends Command {

  private static final Logger LOGGER = LogManager.getLogger(PlayCommand.class);
  private final AudioPlayerManager playerManager;
  private final String youtubeApiKey;
  private final OptionEntityDao optionEntityDao;
  private final VoiceChannelService voiceChannelService;

  @Autowired
  public PlayCommand(
      AudioPlayerManager playerManager,
      BotConfiguration configuration,
      OptionEntityDao optionEntityDao,
      VoiceChannelService voiceChannelService) {
    this.playerManager = playerManager;
    this.name = "play";
    this.help = "Plays a song with the given name or url.";
    this.guildOnly = true;

    this.options = List.of(Option.createOption(OptionName.PLAY_AUDIO_SONG_NAME, true, 0));

    this.youtubeApiKey = configuration.getYoutubeApiKey();
    this.optionEntityDao = optionEntityDao;

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    voiceChannelService.searchAndPlaySong(
        event, false, playerManager, youtubeApiKey, optionEntityDao);
  }
}
