package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class RemoveCommand extends Command
{
    public RemoveCommand()
    {
        this.name = "remove";
        this.help = "Remove the requested song from the queue";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String argument = event.getArgs();

        if (argument.isEmpty())
        {
            event.getChannel().sendMessage("Need to provide something to remove").queue();
            return;
        }

        int trackToRemove;
        try
        {
            trackToRemove = Integer.parseInt(argument);
        }
        catch (NumberFormatException e)
        {
            event.getChannel().sendMessage("That's not a number").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();

        if (audioPlayerSendHandler==null)
        {
            return;
        }

        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

        List<AudioTrack> queue = trackScheduler.getQueue();

        if (trackToRemove > queue.size())
        {
            event.getChannel().sendMessage(String.format("There is not a track at position %d on the queue", trackToRemove)).queue();
            return;
        }

        queue.remove(trackToRemove);
        event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
    }
}
