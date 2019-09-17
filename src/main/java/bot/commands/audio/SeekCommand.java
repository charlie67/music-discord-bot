package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.TimeUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;

public class SeekCommand extends Command
{
    public SeekCommand()
    {
        this.name = "seek";
        this.help = "\tSeeks to a certain point in the current track.";
    }
        @Override
    protected void execute(CommandEvent event)
    {
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected())
        {
            event.getChannel().sendMessage("**Not currently connected to the voice channel**").queue();
            return;
        }

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        AudioTrack np = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();

        String seekPoint = event.getArgs();

        int seekTime;

        if (seekPoint.contains(":"))
        {
            //it is in the format mins:seconds
            String[] parts = seekPoint.split(":");

            //parts[0] is mins and parts[1[ is seconds

            seekTime = Integer.parseInt(parts[0]) * 60;
            seekTime += Integer.parseInt(parts[1]);
        }
        else
        {
            try
            {
                seekTime = Integer.parseInt(seekPoint);
            }
            catch (NumberFormatException e)
            {
                event.getChannel().sendMessage("**Invalid format**, example formats: \n `0:01` `1:45`").queue();
                return;
            }
        }

        if (seekTime*1000 > np.getDuration() || !np.isSeekable())
        {
            event.getChannel().sendMessage("**Cannot seek to a position longer than the song**").queue();
            return;
        }

        event.getChannel().sendMessage(String.format("**Seeking to** `%s`", TimeUtils.timeString(seekTime))).queue();

        np.setPosition(seekTime * 1000);
    }
}
