package bot.commands.audio;

import static bot.utils.EmbedUtils.createEmbedBuilder;
import static bot.utils.TextChannelResponses.CANT_DISPLAY_QUEUE_PAGE;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class QueueCommand extends Command {
  public QueueCommand() {
    this.name = "queue";
    this.help = "See the queue for the server";
  }

  @Override
  protected void execute(CommandEvent event) {
    AudioManager audioManager = event.getGuild().getAudioManager();
    MessageChannel channel = event.getChannel();

    AudioPlayerSendHandler audioPlayerSendHandler =
        (AudioPlayerSendHandler) audioManager.getSendingHandler();
    if (audioPlayerSendHandler == null) {
      channel.sendMessage("**Queue is empty**").queue();
      return;
    }

    TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();
    List<AudioTrack> queue = trackScheduler.getQueue();

    if (queue.isEmpty()) {
      channel.sendMessage("**Queue is empty**").queue();
      return;
    }

    try {
      EmbedBuilder eb = createEmbedBuilder(event, trackScheduler, queue, true);
      event.reply(eb.build());
    } catch (NumberFormatException e) {
      event.getChannel().sendMessage(CANT_DISPLAY_QUEUE_PAGE).queue();
    }
  }
}
