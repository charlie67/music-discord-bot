package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.TimeUtils;
import bot.utils.YoutubeUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class PlayCommand extends Command
{
    private final AudioPlayerManager playerManager;

    public PlayCommand(AudioPlayerManager playerManager)
    {
        this.playerManager = playerManager;
        this.name = "play";
        this.help = "Play the requested song";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        System.out.println("Play command is being executed");
        String argument = event.getArgs();

        if (argument.isEmpty())
        {
            event.getChannel().sendMessage("Need to provide something to play").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected())
        {
            JoinCommand.joinVoiceChannel(event, playerManager);
        }
        event.getChannel().sendMessage("Searching for `").append(argument).append("`").queue();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

        playerManager.loadItem(argument, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler);

                event.getChannel().sendMessage(eb.build()).queue();
                if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null)
                {
                    audioPlayerSendHandler.getAudioPlayer().playTrack(track);
                    return;
                }

                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                for (AudioTrack track : playlist.getTracks())
                {
                    trackScheduler.queue(track);
                }

                event.getChannel().sendMessage(String.format("Queued %d tracks", playlist.getTracks().size())).queue();

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
                YoutubeAudioSourceManager ytAudioSourceManager = new YoutubeAudioSourceManager();
                YoutubeSearchProvider yt = new YoutubeSearchProvider(ytAudioSourceManager);
                BasicAudioPlaylist playlist = (BasicAudioPlaylist) yt.loadSearchResult(argument);
                List<AudioTrack> tracks = playlist.getTracks();

                if (tracks.size() > 0)
                {
                    AudioTrack track = tracks.get(0);
                    trackScheduler.queue(track);

                    EmbedBuilder eb = getAudioTrackMessage(track, trackScheduler);
                    event.getChannel().sendMessage(eb.build()).queue();

                    if (audioPlayerSendHandler.getAudioPlayer().getPlayingTrack() == null)
                    {
                        audioPlayerSendHandler.getAudioPlayer().playTrack(track);
                    }
                }
                else
                {
                    event.getChannel().sendMessage(String.format("%s didn't match a video", argument)).queue();
                }
            }

            @Override
            public void loadFailed(FriendlyException throwable)
            {
                event.getChannel().sendMessage("Loading failed for that video").queue();
            }
        });
    }

    @NotNull
    private EmbedBuilder getAudioTrackMessage(AudioTrack track, TrackScheduler trackScheduler)
    {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Added to queue");
        eb.setTitle(track.getInfo().title, track.getInfo().uri);
        if (track instanceof YoutubeAudioTrack)
        {
            String url = YoutubeUtils.getYoutubeThumbnail(track);
            eb.setThumbnail(url);
            eb.setColor(Color.RED);
        }
        eb.setDescription(String.format("Queue position: %d\nSong Duration: %s", trackScheduler.getQueueSize(), TimeUtils.timeString(track.getDuration() / 1000)));
        return eb;
    }
}
