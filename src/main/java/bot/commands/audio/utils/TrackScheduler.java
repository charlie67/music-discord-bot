package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;

public class TrackScheduler extends AudioEventAdapter
{
    private ArrayList<AudioTrack> queue = new ArrayList<>();

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
    {
        System.out.println("Track ended");
        if (endReason == AudioTrackEndReason.LOAD_FAILED)
        {
            System.out.println("Loading failed on the track");
        }

        if (queue.size() > 0)
        {
            player.playTrack(nextTrack());
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

    public int getQueueSize()
    {
        return queue.size();
    }

    public AudioTrack nextTrack()
    {
        if (queue.size() > 0)
        {
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
