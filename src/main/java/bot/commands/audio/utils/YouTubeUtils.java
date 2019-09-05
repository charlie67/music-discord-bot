package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;

import java.util.List;

public class YouTubeUtils
{
    static AudioTrack searchForVideo(String argument)
    {
        YoutubeAudioSourceManager ytAudioSourceManager = new YoutubeAudioSourceManager();
        YoutubeSearchProvider yt = new YoutubeSearchProvider(ytAudioSourceManager);
        BasicAudioPlaylist playlist = (BasicAudioPlaylist) yt.loadSearchResult(argument);
        List<AudioTrack> tracks = playlist.getTracks();

        if (tracks.size() > 0)
        {
            return tracks.get(0);
        }
        else
        {
            return null;
        }
    }

    public static AudioTrack getRelatedVideo(YoutubeAudioTrack track)
    {


        return null;
    }
}
