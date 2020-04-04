package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class YouTubeUtilsTest
{
    @Test
    public void searchForVideo() throws IllegalAccessException
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez", 0);

        assert audioTrack instanceof YoutubeAudioTrack;

        assertNotNull(audioTrack.getIdentifier());
        assertNotEquals(0, audioTrack.getDuration());
        assertNotNull(audioTrack.getInfo());
    }

    @Test
    public void getYoutubeThumbnail() throws IllegalAccessException
    {
        AudioTrack audioTrack = YouTubeUtils.searchForVideo("Fallen kingdom captain sparklez", 0);
        assertNotNull(audioTrack);

        String thumbnail = YouTubeUtils.getYoutubeThumbnail(audioTrack);
        assertFalse(thumbnail.isEmpty());
    }
}