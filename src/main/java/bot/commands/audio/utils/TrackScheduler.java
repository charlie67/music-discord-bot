package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackScheduler extends AudioEventAdapter
{
    private ArrayList<AudioTrack> queue = new ArrayList<>();
    private Logger LOGGER = LogManager.getLogger(TrackScheduler.class);

    private long durationInMilliSeconds = 0;

    private Map<String, List<AudioTrack>> mapVideoIdToMix = new HashMap<>();

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        LOGGER.info(String.format("Track ended %s", endReason.toString()));

        if (!endReason.mayStartNext && !endReason.equals(AudioTrackEndReason.STOPPED))
        {
        return;
        }

        if (queue.size() > 0)
        {
            player.playTrack(nextTrack());
            return;
        }


        try
        {
            String oldTrackId = track.getInfo().identifier;
            AudioTrack nextTrack = YouTubeUtils.getRelatedVideo(oldTrackId);
            queue.add(nextTrack);
            player.playTrack(nextTrack());

        } catch (IOException e)
        {
            LOGGER.error("Encountered error when trying to find a related video", e);
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs)
    {
        super.onTrackStuck(player, track, thresholdMs);
    }

    public void queue(AudioTrack track)
    {
        durationInMilliSeconds += track.getDuration();
        queue.add(track);
    }

    public void clearQueue()
    {
        durationInMilliSeconds = 0;
        queue.clear();
    }

    int getQueueSize()
    {
        return queue.size();
    }

    AudioTrack nextTrack()
    {
        if (queue.size() > 0)
        {
            AudioTrack audioTrack = queue.get(0);
            durationInMilliSeconds -= audioTrack.getDuration();
            queue.remove(0);
            return audioTrack;
        }

        return null;
    }

    public void setQueue(ArrayList<AudioTrack> queue)
    {
        this.queue = queue;
    }

    public ArrayList<AudioTrack> getQueue()
    {
        return queue;
    }

    public long getDurationInMilliSeconds()
    {
        return durationInMilliSeconds;
    }
}
