package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class TrackScheduler extends AudioEventAdapter
{
    private ArrayList<AudioTrack> queue = new ArrayList<>();
    private Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        LOGGER.debug(String.format("Track ended %s", endReason.toString()));

        if (queue.size() > 0) {
            player.playTrack(nextTrack());
        } else {
            try {
                String oldTrackId = track.getInfo().identifier;
                AudioTrack nextTrack = YouTubeUtils.getRelatedVideo(oldTrackId);
                queue.add(nextTrack);
                player.playTrack(nextTrack());

            } catch (IOException e) {
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

    public void queue(AudioTrack track)
    {
        queue.add(track);
    }

    int getQueueSize()
    {
        return queue.size();
    }

    AudioTrack nextTrack()
    {
        if (queue.size() > 0) {
            AudioTrack audioTrack = queue.get(0);
            queue.remove(0);
            return audioTrack;
        }

        return null;
    }

    public ArrayList<AudioTrack> getQueue()
    {
        return queue;
    }
}
