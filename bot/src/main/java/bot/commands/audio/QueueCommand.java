package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.TimeUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class QueueCommand extends Command
{
    public QueueCommand()
    {
        this.name = "queue";
        this.help = "See the queue for the server";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        AudioManager audioManager = event.getGuild().getAudioManager();
        MessageChannel channel = event.getChannel();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        if (audioPlayerSendHandler == null)
        {
            channel.sendMessage("Queue is empty").queue();
            return;
        }

        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();
        List<AudioTrack> queue = trackScheduler.getQueue();

        if (queue.size() == 0)
        {
            channel.sendMessage("Queue is empty").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(String.format("Queue for %s", event.getGuild().getName()));
        double pages = Math.ceil((double) queue.size() / 10);
        eb.setFooter(String.format("%d songs in queue | tab 1/%.0f", queue.size(), pages));

        StringBuilder stringBuilder = new StringBuilder();
        int trackCount = 1;

        for (AudioTrack audioTrack : queue)
        {
            if (trackCount == 11) break;
            stringBuilder.append(String.format("`%d.` [%s](%s) | %s\n\n", trackCount, audioTrack.getInfo().title, audioTrack.getInfo().uri, TimeUtils.timeString(audioTrack.getDuration() / 1000)));
            trackCount++;
        }

        eb.setDescription(stringBuilder.toString());

        channel.sendMessage(eb.build()).queue();
    }
}
