package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCommand extends Command
{
    public LeaveCommand()
    {
        this.name = "leave";
        this.aliases = new String[]{"die", "stop"};
        this.help = "Leave the currently connected voice channel";
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
        audioPlayerSendHandler.getAudioPlayer().stopTrack();
        audioManager.closeAudioConnection();
        event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
    }
}
