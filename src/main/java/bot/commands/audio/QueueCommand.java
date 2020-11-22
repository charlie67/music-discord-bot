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
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static bot.utils.EmbedUtils.createEmbedBuilder;
import static bot.utils.TextChannelResponses.CANT_DISPLAY_QUEUE_PAGE;

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

        try{
            EmbedBuilder eb = createEmbedBuilder(event, trackScheduler, queue, true);
            event.getChannel().sendMessage(eb.build()).queue();
        } catch(NumberFormatException e) {
            event.getChannel().sendMessage(CANT_DISPLAY_QUEUE_PAGE).queue();
        }
    }
}
