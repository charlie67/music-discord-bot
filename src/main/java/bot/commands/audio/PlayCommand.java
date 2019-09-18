package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.AudioSearchResultHandler;
import bot.commands.audio.utils.TrackScheduler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.ConnectException;

public class PlayCommand extends Command
{
    private final AudioPlayerManager playerManager;

    public PlayCommand(AudioPlayerManager playerManager)
    {
        this.playerManager = playerManager;
        this.name = "play";
        this.help = "Plays a song with the given name or url.";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String argument = event.getArgs();

        if (argument.isEmpty())
        {
            event.getChannel().sendMessage("Need to provide something to play").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected())
        {
            try
            {
                JoinCommand.joinVoiceChannel(event, playerManager);
            } catch (ConnectException e)
            {
                event.getChannel().sendMessage("You need to be in a voice channel.").queue();
                return;
            }
        }
        event.getChannel().sendMessage("Searching for `").append(argument).append("`").queue();
        event.getChannel().sendTyping().queue();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

        playerManager.loadItem(argument, new AudioSearchResultHandler(trackScheduler, event, audioPlayerSendHandler, argument));
    }
}
