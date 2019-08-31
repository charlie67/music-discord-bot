package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.TimeUtils;
import bot.utils.YoutubeUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class NowPlayingCommand extends Command
{
    public NowPlayingCommand()
    {
        this.name = "nowplaying";
        this.aliases = new String[]{"np", "now playing"};
        this.help = "Get the currently playing song";
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
        AudioTrack np = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();

        if (np == null)
        {
            event.getChannel().sendMessage("Not currently playing anything").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        if (np instanceof YoutubeAudioTrack)
        {
            String url = YoutubeUtils.getYoutubeThumbnail(np);
            eb.setThumbnail(url);
            eb.setColor(Color.RED);
        }
        eb.setAuthor("Now playing");
        eb.setTitle(np.getInfo().title, np.getInfo().uri);
        eb.setDescription(String.format("%s / %s", TimeUtils.timeString(np.getPosition() / 1000), TimeUtils.timeString(np.getDuration() / 1000)));
        event.getChannel().sendMessage(eb.build()).queue();
    }

}
