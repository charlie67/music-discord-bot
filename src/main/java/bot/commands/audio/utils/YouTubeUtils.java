package bot.commands.audio.utils;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class YouTubeUtils
{
    static AudioTrack searchForVideo(String argument)
    {
        YoutubeAudioSourceManager ytAudioSourceManager = new YoutubeAudioSourceManager();
        YoutubeSearchProvider yt = new YoutubeSearchProvider(ytAudioSourceManager);
        BasicAudioPlaylist playlist = (BasicAudioPlaylist) yt.loadSearchResult(argument);
        List<AudioTrack> tracks = playlist.getTracks();

        if (tracks.size() > 0) {
            return tracks.get(0);
        } else {
            return null;
        }
    }

    public static String getYoutubeThumbnail(AudioTrack np)
    {
        try
        {
            String youtubeUrl = np.getInfo().uri;

            List<NameValuePair> params = URLEncodedUtils.parse(new URI(youtubeUrl), StandardCharsets.UTF_8);
            String videoID = params.get(0).getValue();
            return "http://img.youtube.com/vi/" + videoID + "/0.jpg";
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
            return "";
        }
    }

        return null;
    }
}
