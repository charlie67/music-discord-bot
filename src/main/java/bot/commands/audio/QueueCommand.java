package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.TimeUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        AtomicInteger ordinal = new AtomicInteger(1);
        Paginator.Builder pb = new Paginator.Builder();
        queue.forEach(audioTrack -> {
            pb.addItems(String.format("`%d.` [%s](%s) | %s\n\n", ordinal.getAndIncrement(), audioTrack.getInfo().title, audioTrack.getInfo().uri, TimeUtils.timeString(audioTrack.getDuration() / 1000)));
        });
        pb.setItemsPerPage(10);
        pb.setEventWaiter(new EventWaiter());

        pb.build().display(channel);
    }
}
