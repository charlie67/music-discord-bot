package bot.commands.audio;

import bot.configuration.BotConfiguration;
import bot.dao.OptionEntityDao;
import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JoinCommand extends Command {

  // The audio player manager that the audio player will be created from
  private final AudioPlayerManager playerManager;
  private final String youtubeApiKey;
  private final OptionEntityDao optionEntityDao;

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public JoinCommand(
      AudioPlayerManager playerManager,
      BotConfiguration botConfiguration,
      OptionEntityDao optionEntityDao,
      VoiceChannelService voiceChannelService) {
    this.playerManager = playerManager;
    this.name = "join";
    this.help = "Joins the voice channel that the user is currently connected to";

    this.youtubeApiKey = botConfiguration.getYoutubeApiKey();
    this.optionEntityDao = optionEntityDao;

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    try {
      voiceChannelService.joinVoiceChannel(
          event.getMember(), event.getGuild(), youtubeApiKey, playerManager, optionEntityDao);
      event.reactSuccess();
    } catch (IllegalArgumentException e) {
      event.reply(TextChannelResponses.USER_NOT_CONNECTED_TO_VOICE_CHANNEL);
    } catch (InsufficientPermissionException e) {
      event.reply(TextChannelResponses.DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL);
    }
  }
}
