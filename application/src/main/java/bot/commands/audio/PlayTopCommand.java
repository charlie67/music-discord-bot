package bot.commands.audio;

import bot.configuration.BotConfiguration;
import bot.dao.OptionEntityDao;
import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTopCommand extends Command {

  private final AudioPlayerManager playerManager;
  private final String youtubeApiKey;
  private final OptionEntityDao optionEntityDao;
  private final VoiceChannelService voiceChannelService;

  @Autowired
  public PlayTopCommand(AudioPlayerManager playerManager, BotConfiguration configuration,
      OptionEntityDao optionEntityDao, VoiceChannelService voiceChannelService) {
    this.playerManager = playerManager;
    this.name = "playtop";
    this.aliases = new String[]{"pt"};
    this.help = "Plays a song with the given name or url by placing it at the top of the queue.";

    this.youtubeApiKey = configuration.getYoutubeApiKey();
    this.optionEntityDao = optionEntityDao;

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    voiceChannelService.searchAndPlaySong(event, true, playerManager, youtubeApiKey,
        optionEntityDao);
  }
}
