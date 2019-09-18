package bot.commands.audio.utils;

import bot.utils.TimeUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

public class AudioSearchResultHandler implements AudioLoadResultHandler
{
    private TrackScheduler trackScheduler;
    private CommandEvent event;
    private AudioPlayerSendHandler audioPlayerSendHandler;
    private String argument;
    private boolean playTop;

    AudioSearchResultHandler(TrackScheduler trackScheduler, CommandEvent event, AudioPlayerSendHandler audioPlayerSendHandler, String argument, boolean playTop)
    {
        this.trackScheduler = trackScheduler;
        this.event = event;
        this.audioPlayerSendHandler = audioPlayerSendHandler;
        this.argument = argument;
        this.playTop = playTop;
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

        event.getChannel().sendMessage(String.format("**Queued `%d` tracks**", playlist.getTracks().size())).queue();

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
        AudioTrack track = YouTubeUtils.searchForVideo(argument);

        if (track != null)
        {
            queueTrackAndStartNextSong(track);
        }
        else
        {
            event.getChannel().sendMessage(String.format("%s didn't match a video", argument)).queue();
        }
    }

    private void queueTrackAndStartNextSong(AudioTrack track)
    {
        long queueDurationInMilliSeconds = trackScheduler.getDurationInMilliSeconds();
        trackScheduler.queue(track, playTop);

        EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler.getQueueSize(), queueDurationInMilliSeconds);
        event.getChannel().sendMessage(eb.build()).queue();

        if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null)
        {
            audioPlayerSendHandler.getAudioPlayer().playTrack(trackScheduler.nextTrack());
        }
    }

    @Override
    public void loadFailed(FriendlyException throwable)
    {
        event.getChannel().sendMessage("Loading failed for that video").queue();
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
//        eb.setDescription(String.format("Queue position \t Estimated time until playing \n%d \t%s Song Duration: %s", queueSize, TimeUtils.timeString(timeUntilPlaying / 1000), TimeUtils.timeString(track.getDuration() / 1000)));
        eb.addField("Song duration", TimeUtils.timeString(track.getDuration() / 1000), true);
        eb.addField("Channel", track.getInfo().author, true);
        eb.addField("Queue position", playTop ? "1" : String.valueOf(queueSize), true);

        //the song will be played when the queue has finished and the currently playing song has stopped
        long timeUntilPlaying;
        AudioTrack nowPlayingTrack = audioPlayerSendHandler.getAudioPlayer().getPlayingTrack();
        timeUntilPlaying = nowPlayingTrack == null ? 0 : (queueDurationInMilliSeconds + (nowPlayingTrack.getDuration() - nowPlayingTrack.getPosition()));
        eb.addField("Estimated time until playing", TimeUtils.timeString(timeUntilPlaying / 1000), true);
        return eb;
    }
}