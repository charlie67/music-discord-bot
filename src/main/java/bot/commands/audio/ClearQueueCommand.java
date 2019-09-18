package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.VoiceChannelUtils;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class ClearQueueCommand extends Command
{
    public ClearQueueCommand()
    {
        this.name = "clear";
        this.help = "Clear the queue for this server";
    }

    @Override
    protected void execute(CommandEvent event)
    {
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

        audioPlayerSendHandler.getTrackScheduler().clearQueue();
        event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
    }
}
