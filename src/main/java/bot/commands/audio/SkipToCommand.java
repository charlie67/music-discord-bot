package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.commands.audio.utils.VoiceChannelUtils;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class SkipToCommand extends Command
{
    public SkipToCommand()
    {
        this.name = "skipto";
        this.help = "Skips to a certain position in the queue.";
    }
    @Override
    protected void execute(CommandEvent event)
    {
        AudioPlayerSendHandler audioPlayerSendHandler;
        try
        {
            audioPlayerSendHandler = VoiceChannelUtils.getAudioPlayerSendHandler(event.getGuild());
        }
        catch (IllegalArgumentException e)
        {
            event.getChannel().sendMessage("**Not currently connected to the voice channel**").queue();
            return;
        }

        int elementSkipTo;

        try
        {
            elementSkipTo = Integer.parseInt(event.getArgs());
        }
        catch (NumberFormatException e)
        {
            event.getChannel().sendMessage("**You need to provide a number to skip to.**").queue();
            return;
        }

        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

        List<AudioTrack> queue = trackScheduler.getQueue();
        List<AudioTrack> sublistQueue = queue.subList(elementSkipTo-1, queue.size());

        trackScheduler.setQueue(sublistQueue);
        audioPlayerSendHandler.getAudioPlayer().stopTrack();
        event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
    }
}