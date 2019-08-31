package bot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class YoutubeUtils
{
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
}
