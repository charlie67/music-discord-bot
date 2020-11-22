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
        LOGGER.info("Track {} ended {}", track.getIdentifier(), endReason.toString());
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

        if (queue.size() > 0)
        {
            player.playTrack(nextTrack());
            return;
        }

        if (track instanceof YoutubeAudioTrack)
        {
            try
            {
                String oldTrackId = track.getInfo().identifier;
                AudioTrack nextTrack = YouTubeUtils.getRelatedVideo(oldTrackId, new ArrayList<>(historyQueue));
                durationInMilliSeconds += nextTrack.getDuration();
                queue.add(nextTrack);
                player.playTrack(nextTrack());

            }
            catch(IOException | IllegalArgumentException | FriendlyException e)
            {
                LOGGER.error("Encountered error when trying to find a related video", e);
            }
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
        if (queue.size() > 0)
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
