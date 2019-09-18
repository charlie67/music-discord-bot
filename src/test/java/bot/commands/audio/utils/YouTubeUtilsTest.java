package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.Test;

import static org.junit.Assert.*;

public class YouTubeUtilsTest
{
    @Test
    public void searchForVideo()
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez");

        assert audioTrack instanceof YoutubeAudioTrack;

        assertEquals(audioTrack.getIdentifier(), "I-sH53vXP2A");
    }

    @Test
    public void getYoutubeThumbnail()
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez");
        assertNotNull(audioTrack);

        String thumbnail = YouTubeUtils.getYoutubeThumbnail(audioTrack);
        assertEquals(thumbnail, "http://img.youtube.com/vi/I-sH53vXP2A/0.jpg");
    }
}