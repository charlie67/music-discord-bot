package bot.commands.audio;

import bot.commands.audio.utils.VoiceChannelUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

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
        VoiceChannelUtils.SearchAndPlaySong(event.getJDA(), event.getArgs(), event.getGuild().getId(), event.getChannel().getId(), event.getMember().getId(), false, playerManager);
    }
}
