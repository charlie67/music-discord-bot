package bot.commands.audio;

import bot.commands.audio.utils.VoiceChannelUtils;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayCommand extends Command
{
    private final AudioPlayerManager playerManager;

    private static final Logger LOGGER = LogManager.getLogger(PlayCommand.class);
    private final String youtubeApiKey;

    public PlayCommand(AudioPlayerManager playerManager, String youtubeApiKey)
    {
        this.playerManager = playerManager;
        this.name = "play";
        this.help = "Plays a song with the given name or url.";

        this.youtubeApiKey = youtubeApiKey;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        LOGGER.info("Play command triggered with message {}", event.getArgs());
        VoiceChannelUtils.searchAndPlaySong(event, false, playerManager, youtubeApiKey);
    }
}
