package bot.commands.audio.utils;

import bot.utils.TextChannelResponses;
import bot.utils.TimeUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;

public class AudioSearchResultHandler implements AudioLoadResultHandler
{
    private final Logger LOGGER = LogManager.getLogger(AudioSearchResultHandler.class);

    private final TrackScheduler trackScheduler;
    private final AudioPlayerSendHandler audioPlayerSendHandler;
    private final String argument;
    private final boolean playTop;
    private final MessageChannel channel;

    AudioSearchResultHandler(TrackScheduler trackScheduler, AudioPlayerSendHandler audioPlayerSendHandler,
                             MessageChannel channel, String argument, boolean playTop)
    {
        this.trackScheduler = trackScheduler;
        this.audioPlayerSendHandler = audioPlayerSendHandler;
        this.argument = argument;
        this.playTop = playTop;
        this.channel = channel;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        queueTrackAndStartNextSong(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        for (AudioTrack track : playlist.getTracks())
        {
            trackScheduler.queue(track, playTop);
        }

        channel.sendMessage(String.format("**Queued `%d` tracks**", playlist.getTracks().size())).queue();

        if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null)
        {
            AudioTrack track = trackScheduler.nextTrack();
            audioPlayerSendHandler.getAudioPlayer().playTrack(track);
        }
    }

    @Override
    public void noMatches()
    {
        //This means that the argument didn't match a particular source so search for it on youtube instead
        try
        {
            AudioTrack track = YouTubeUtils.searchForVideo(argument, 0);

            if (track != null)
            {
                queueTrackAndStartNextSong(track);
            }
            else
            {
                channel.sendMessage(String.format("%s didn't match a video", argument)).queue();
            }
        }
        catch(IllegalAccessException e)
        {
            channel.sendMessage(TextChannelResponses.ERROR_LOADING_VIDEO).queue();
            LOGGER.error("Error when searching for YouTube video encountered the wrong result type returned", e);
        }


    }

    private void queueTrackAndStartNextSong(AudioTrack track)
    {
        long queueDurationInMilliSeconds = trackScheduler.getDurationInMilliSeconds();
        trackScheduler.queue(track, playTop);

        EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler.getQueueSize(), queueDurationInMilliSeconds);
        channel.sendMessage(eb.build()).queue();

        if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null)
        {
            audioPlayerSendHandler.getAudioPlayer().playTrack(trackScheduler.nextTrack());
        }
    }

    @Override
    public void loadFailed(FriendlyException throwable)
    {
        channel.sendMessage("Loading failed for that video").queue();
    }

    private EmbedBuilder getAudioTrackMessage(AudioTrack track, int queueSize, long queueDurationInMilliSeconds)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Added to queue");
        eb.setTitle(track.getInfo().title, track.getInfo().uri);
        if (track instanceof YoutubeAudioTrack)
        {
            String url = YouTubeUtils.getYoutubeThumbnail(track);
            eb.setThumbnail(url);
            eb.setColor(Color.RED);
        }
        eb.addField("Song duration", TimeUtils.timeString(track.getDuration() / 1000), true);
        eb.addField("Channel", track.getInfo().author, true);
        eb.addField("Queue position", playTop ? "1" : String.valueOf(queueSize), true);

        //the song will be played when the queue has finished and the currently playing song has stopped
        long timeUntilPlaying;
        AudioTrack nowPlayingTrack = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();
        timeUntilPlaying = nowPlayingTrack == null ? 0 :
                (queueDurationInMilliSeconds + (nowPlayingTrack.getDuration() - nowPlayingTrack.getPosition()));
        eb.addField("Estimated time until playing", TimeUtils.timeString(timeUntilPlaying / 1000), true);
        return eb;
    }
}