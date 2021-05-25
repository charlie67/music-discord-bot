package bot.commands.audio.utils;

import com.google.common.collect.EvictingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackScheduler extends AudioEventAdapter
{
    private List<AudioTrack> queue = new ArrayList<>();
    private final Logger LOGGER = LogManager.getLogger(TrackScheduler.class);

    private AudioTrack loopTrack = null;

    private long durationInMilliSeconds = 0;

    private final EvictingQueue<AudioTrack> historyQueue = EvictingQueue.create(20);

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        LOGGER.info("Track {} ended {}", track.getIdentifier(), endReason);
        historyQueue.add(track);

        if (!endReason.mayStartNext && !endReason.equals(AudioTrackEndReason.STOPPED))
        {
            return;
        }

        if (loopTrack != null)
        {
            player.playTrack(loopTrack.makeClone());
            return;
        }

        if (!queue.isEmpty())
        {
            player.playTrack(nextTrack());
            return;
        }

        if (track instanceof YoutubeAudioTrack)
        {
            String oldTrackId = track.getInfo().identifier;
            AudioTrack nextTrack = getRelatedVideoRetry(oldTrackId, 0);
            if (nextTrack != null)
            {
                player.playTrack(nextTrack);
            }
        }
    }

    public AudioTrack getRelatedVideoRetry(String trackId, int retryAmount)
    {
        if (retryAmount >= 10)
        {
            return null;
        }

        try
        {
            return YouTubeUtils.getRelatedVideo(trackId, new ArrayList<>(historyQueue));

        }
        catch(IOException | IllegalArgumentException | FriendlyException e)
        {
            LOGGER.error("Encountered error when trying to find a related video", e);
        }

        return getRelatedVideoRetry(trackId, retryAmount + 1);
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception)
    {
        LOGGER.error("error when playing track", exception);
    }

    public void queue(AudioTrack track, boolean queueFirst)
    {
        durationInMilliSeconds += track.getDuration();
        if (queueFirst)
        {
            queue.add(0, track);
            return;
        }
        queue.add(track);
    }

    public void clearQueue()
    {
        durationInMilliSeconds = 0;
        queue.clear();
    }

    public int getQueueSize()
    {
        return queue.size();
    }

    AudioTrack nextTrack()
    {
        if (!queue.isEmpty())
        {
            AudioTrack audioTrack = queue.get(0);
            durationInMilliSeconds -= audioTrack.getDuration();
            queue.remove(0);
            return audioTrack;
        }

        return null;
    }

    public void setQueue(List<AudioTrack> queue)
    {
        this.queue = queue;
    }

    public List<AudioTrack> getQueue()
    {
        return queue;
    }

    public long getDurationInMilliSeconds()
    {
        return durationInMilliSeconds;
    }

    public AudioTrack getLoopTrack()
    {
        return loopTrack;
    }

    public void setLoopTrack(AudioTrack loopTrack)
    {
        this.loopTrack = loopTrack;
    }

    public void remove(int trackToRemove)
    {
        queue.remove(trackToRemove);
    }

    public EvictingQueue<AudioTrack> getHistory()
    {
        return historyQueue;
    }
}
