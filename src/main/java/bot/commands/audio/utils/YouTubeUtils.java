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
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
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

public class YouTubeUtils
{
    private static final Logger LOGGER = LogManager.getLogger(YouTubeUtils.class);

    @Deprecated
    static AudioTrack searchForVideo(String argument) throws IllegalAccessException
    {
        if (argument.startsWith("ytsearch:"))
        {
            argument = argument.replace("ytsearch:", "");
        }
        LOGGER.info("Searching for {}", argument);

        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true);
        YoutubeSearchProvider yt = new YoutubeSearchProvider();

        AudioItem result = yt.loadSearchResult(argument, audioTrackInfo -> new YoutubeAudioTrack(audioTrackInfo,
                youtube));

        if (result instanceof BasicAudioPlaylist)
        {
            BasicAudioPlaylist playlist = (BasicAudioPlaylist) result;
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
        else
        {
            if (result instanceof AudioReference)
            {
                LOGGER.error("Trying to search for a video but the result is an AudioReference, uri is {}, identifier is " +
                        "{}", ((AudioReference) result).getUri(), ((AudioReference) result).getIdentifier());
            }

            throw new IllegalAccessException("YouTube Search Result is not instance of BasicAudioPlaylist " + result.getClass().toString());
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
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
            return "";
        }
    }

    static AudioTrack getRelatedVideo(String videoID, List<AudioTrack> history) throws IOException,
            FriendlyException
    {
        LOGGER.info("finding related video for videoID {}", videoID);
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request ->
        {
        }).setApplicationName("bot").build();

        // Define the API request for retrieving search results.
        YouTube.Search.List search = youtube.search().list("id");

        //set the API key
        search.setKey(GetSystemEnvironmentOrDefaultValue.get("YOUTUBE_API_KEY"));
        search.setRelatedToVideoId(videoID);
        search.setEventType("none");
        search.setSafeSearch("none");
        search.setMaxResults(5L);

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();
        LOGGER.info("Found {} related videos", searchResultList.size());

        int searchResultListPosition = 0;
        SearchResult video;
        String id;

        // check if this video is in the history
        do
        {
            if (searchResultListPosition >= searchResultList.size())
            {
                throw new IllegalArgumentException("Unable to find a related video - list was exhausted");
            }

            searchResultListPosition++;
            video = searchResultList.get(searchResultListPosition);
            id = (String) ((ResourceId) video.get("id")).get("videoId");
        } while (isAudioTrackOnHistory(id, history));


        LOGGER.info("Found videoID {} as the related video", id);

        YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager(true);
        return (AudioTrack) youtubeAudioSourceManager.loadTrackWithVideoId(id, true);
    }

    private static boolean isAudioTrackOnHistory(String id, List<AudioTrack> history)
    {
        for (AudioTrack historyAudioTrack : history)
        {
            if (historyAudioTrack instanceof YoutubeAudioTrack && historyAudioTrack.getIdentifier().equals(id))
            {
                return true;
            }
        }
        return false;
    }
}
