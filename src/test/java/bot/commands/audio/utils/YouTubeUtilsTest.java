package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class YouTubeUtilsTest
{
    @Test
    public void searchForVideo() throws IllegalAccessException
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez");

        assert audioTrack instanceof YoutubeAudioTrack;

        assertNotNull(audioTrack.getIdentifier());
    }

    @Test
    public void getYoutubeThumbnail() throws IllegalAccessException
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez");
        assertNotNull(audioTrack);

        String thumbnail = YouTubeUtils.getYoutubeThumbnail(audioTrack);
        assertFalse(thumbnail.isEmpty());
    }
}