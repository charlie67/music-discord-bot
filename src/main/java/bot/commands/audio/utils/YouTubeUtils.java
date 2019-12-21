package bot.commands.audio.utils;

import bot.utils.GetSystemEnvironmentOrDefaultValue;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class YouTubeUtils
{
    private static Logger LOGGER = LogManager.getLogger(YouTubeUtils.class);

    static AudioTrack searchForVideo(String argument)
    {
        LOGGER.info("Searching for {}", argument);

        YoutubeSearchProvider yt = new YoutubeSearchProvider();
        BasicAudioPlaylist playlist = (BasicAudioPlaylist) yt.loadSearchResult(argument, audioTrackInfo -> new YoutubeAudioTrack(audioTrackInfo, new YoutubeAudioSourceManager()));
        List<AudioTrack> tracks = playlist.getTracks();

        LOGGER.info("Received {} results", tracks.size());

        if (tracks.size() > 0)
        {
            return tracks.get(0);
        }
        else
        {
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

    static AudioTrack getRelatedVideo(String videoID) throws IOException
    {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request ->
        {
        }).setApplicationName("bot").build();

        // Define the API request for retrieving search results.
        YouTube.Search.List search = youtube.search().list("id,snippet");

        //set the API key
        search.setKey(GetSystemEnvironmentOrDefaultValue.get("YOUTUBE_API_KEY"));
        search.setRelatedToVideoId(videoID);
        search.setMaxResults(4L);

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        SearchResult video = searchResultList.get(new Random().nextInt(searchResultList.size()));

        String id = (String) ((ResourceId) video.get("id")).get("videoId");

        return searchForVideo(id);
    }
}
