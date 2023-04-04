package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.managers.AudioManager;

public class ShuffleCommand extends Command {
  public ShuffleCommand() {
    this.name = "shuffle";
    this.help = "Shuffle the queue";
  }

  @Override
  protected void execute(CommandEvent event) {
    AudioManager audioManager = event.getGuild().getAudioManager();

    if (!audioManager.isConnected()) {
      event.getChannel().sendMessage("**Not currently connected to the voice channel**").queue();
      return;
    }

    AudioPlayerSendHandler audioPlayerSendHandler =
        (AudioPlayerSendHandler) audioManager.getSendingHandler();
    TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

    List<AudioTrack> queue = trackScheduler.getQueue();
    Collections.shuffle(queue);
    trackScheduler.setQueue(queue);

    event.reactSuccess();
  }
}
