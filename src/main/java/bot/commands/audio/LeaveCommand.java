package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.VoiceChannelUtils;
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

        audioPlayerSendHandler.getAudioPlayer().stopTrack();
        audioManager.closeAudioConnection();
        event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
    }
}
