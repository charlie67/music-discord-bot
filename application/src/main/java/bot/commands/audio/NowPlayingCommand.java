package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.YouTubeUtils;
import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.TimeUtils;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.*;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NowPlayingCommand extends Command {

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public NowPlayingCommand(VoiceChannelService voiceChannelService) {
    this.name = "nowplaying";
    this.aliases = new String[] {"np", "now playing"};
    this.help = "Get the currently playing song";

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

    AudioTrack np = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();

    if (np == null) {
      event.getChannel().sendMessage(TextChannelResponses.NOTHING_CURRENTLY_PLAYING).queue();
      return;
    }

    EmbedBuilder eb = new EmbedBuilder();
    if (np instanceof YoutubeAudioTrack) {
      String url = YouTubeUtils.getYoutubeThumbnail(np);
      eb.setThumbnail(url);
      eb.setColor(Color.RED);
    }
    eb.setAuthor("Now playing");
    eb.setTitle(np.getInfo().title, np.getInfo().uri);
    eb.setDescription(
        String.format(
            "%s / %s",
            TimeUtils.timeString(np.getPosition() / 1000),
            TimeUtils.timeString(np.getDuration() / 1000)));
    event.reply(eb.build());
  }
}
