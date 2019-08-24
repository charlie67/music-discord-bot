package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class SkipSongCommand extends Command
{
    public SkipSongCommand()
    {
        this.name = "skip";
        this.help = "Skip the currently playing song";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected())
        {
            event.getChannel().sendMessage("Not currently connected to the voice channel").queue();
            return;
        }

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        if (audioPlayerSendHandler != null)
        {
            audioPlayerSendHandler.getAudioPlayer().stopTrack();
            event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
        }
    }
}
