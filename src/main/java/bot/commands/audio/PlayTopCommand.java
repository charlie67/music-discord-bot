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

public class PlayTopCommand extends Command
{
    private final AudioPlayerManager playerManager;

    public PlayTopCommand(AudioPlayerManager playerManager)
    {
        this.playerManager = playerManager;
        this.name = "playtop";
        this.aliases = new String[]{"pt"};
        this.help = "Plays a song with the given name or url by placing it at the top of the queue.";
    }
    @Override
    protected void execute(CommandEvent event)
    {
        VoiceChannelUtils.SearchAndPlaySong(event, true, playerManager);
    }
}
