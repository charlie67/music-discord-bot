package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTopCommand extends Command {

  private final AudioPlayerManager playerManager;
  private final VoiceChannelService voiceChannelService;

  @Autowired
  public PlayTopCommand(AudioPlayerManager playerManager, VoiceChannelService voiceChannelService) {
    this.playerManager = playerManager;
    this.name = "playtop";
    this.aliases = new String[]{"pt"};
    this.help = "Plays a song with the given name or url by placing it at the top of the queue.";

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    voiceChannelService.searchAndPlaySong(event, true, playerManager);
  }
}
