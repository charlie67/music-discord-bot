package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.AudioSearchResultHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.commands.audio.utils.VoiceChannelUtils;
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
        VoiceChannelUtils.SearchAndPlaySong(event, false, playerManager);
    }
}
