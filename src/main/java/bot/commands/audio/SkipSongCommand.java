package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.VoiceChannelUtils;
import bot.utils.UnicodeEmote;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

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

        if (audioPlayerSendHandler != null)
        {
            audioPlayerSendHandler.getAudioPlayer().stopTrack();
            event.getMessage().addReaction(UnicodeEmote.THUMBS_UP).queue();
        }
    }
}

