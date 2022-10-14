package bot.commands.audio;

import bot.commands.audio.utils.VoiceChannelUtils;
import bot.dao.OptionEntityDao;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class PlayTopCommand extends Command
{
    private final AudioPlayerManager playerManager;
    private final String youtubeApiKey;
    private final OptionEntityDao optionEntityDao;

    public PlayTopCommand(AudioPlayerManager playerManager, String youtubeApiKey,
        OptionEntityDao optionEntityDao)
    {
        this.playerManager = playerManager;
        this.name = "playtop";
        this.aliases = new String[]{"pt"};
        this.help = "Plays a song with the given name or url by placing it at the top of the queue.";

        this.youtubeApiKey = youtubeApiKey;
        this.optionEntityDao = optionEntityDao;
    }

    @Override
    protected void execute(CommandEvent event) {
        VoiceChannelUtils.searchAndPlaySong(event, true, playerManager, youtubeApiKey,
            optionEntityDao);
    }
}
