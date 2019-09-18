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

import java.awt.Color;
import java.util.List;
import java.util.Random;
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
            channel.sendMessage("**Queue is empty**").queue();
            return;
        }

        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();
        List<AudioTrack> queue = trackScheduler.getQueue();

        if (queue.size() == 0)
        {
            channel.sendMessage("**Queue is empty**").queue();
            return;
        }

        double totalPages = Math.ceil((double) queue.size() / 10);
        int page = 1;

        if (!event.getArgs().equals(""))
        {
            try
            {
                page = Integer.parseInt(event.getArgs());

                if (page > totalPages)
                {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e)
            {
                event.getChannel().sendMessage("**Cannot display that page**").queue();
                return;
            }
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setFooter(String.format("Page %d/%.0f", page, totalPages), event.getAuthor().getAvatarUrl());

        //get a random colour for the embed
        Random rand = new Random();
        // Java 'Color' class takes 3 floats, from 0 to 1.
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        eb.setColor(randomColor);

        AtomicInteger ordinal = new AtomicInteger(1);
        StringBuilder sb = new StringBuilder();

        for (AudioTrack audioTrack : queue)
        {
            int itemPosition = ordinal.getAndIncrement();
            if (itemPosition < 10 * page + 1 - 10) continue;

            sb.append(String.format("`%d.` [%s](%s) | %s\n\n", itemPosition, audioTrack.getInfo().title, audioTrack.getInfo().uri, TimeUtils.timeString(audioTrack.getDuration() / 1000)));

            if (ordinal.get() > 10 * page) break;
        }

        sb.append(String.format("%d songs in queue | %s total duration", queue.size(), TimeUtils.timeString(trackScheduler.getDurationInMilliSeconds() / 1000)));

        eb.setDescription(sb);

        event.getChannel().sendMessage(eb.build()).queue();
    }
}
