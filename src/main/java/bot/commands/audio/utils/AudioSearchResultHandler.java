package bot.commands.audio.utils;

import bot.utils.TimeUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AudioSearchResultHandler implements AudioLoadResultHandler
{
    private TrackScheduler trackScheduler;
    private CommandEvent event;
    private AudioPlayerSendHandler audioPlayerSendHandler;
    private String argument;

    public AudioSearchResultHandler(TrackScheduler trackScheduler, CommandEvent event, AudioPlayerSendHandler audioPlayerSendHandler, String argument)
    {
        this.trackScheduler = trackScheduler;
        this.event = event;
        this.audioPlayerSendHandler = audioPlayerSendHandler;
        this.argument = argument;
    }

    @Override
    public void trackLoaded(AudioTrack track)
    {
        trackScheduler.queue(track);
        EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler.getQueueSize());

        event.getChannel().sendMessage(eb.build()).queue();
        if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null) {
            audioPlayerSendHandler.getAudioPlayer().playTrack(trackScheduler.nextTrack());
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist)
    {
        for (AudioTrack track : playlist.getTracks()) {
            trackScheduler.queue(track);
        }

        event.getChannel().sendMessage(String.format("Queued %d tracks", playlist.getTracks().size())).queue();

        if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null) {
            AudioTrack track = trackScheduler.nextTrack();
            audioPlayerSendHandler.getAudioPlayer().playTrack(track);
        }
    }

    @Override
    public void noMatches()
    {
        //This means that the argument didn't match a particular source so search for it on youtube instead
        AudioTrack track = YouTubeUtils.searchForVideo(argument);

        if (track != null) {
            trackScheduler.queue(track);

            EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler.getQueueSize());
            event.getChannel().sendMessage(eb.build()).queue();

            if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null) {
                audioPlayerSendHandler.getAudioPlayer().playTrack(trackScheduler.nextTrack());
            }
        } else {
            event.getChannel().sendMessage(String.format("%s didn't match a video", argument)).queue();
        }
    }

    @Override
    public void loadFailed(FriendlyException throwable)
    {
        event.getChannel().sendMessage("Loading failed for that video").queue();
    }

    private EmbedBuilder getAudioTrackMessage(AudioTrack track, int queueSize)
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
        eb.setDescription(String.format("Queue position: %d\nSong Duration: %s", queueSize, TimeUtils.timeString(track.getDuration() / 1000)));
        return eb;
    }
}