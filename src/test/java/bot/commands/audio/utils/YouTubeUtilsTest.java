package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.io.IOException;

import static org.junit.Assert.*;

public class YouTubeUtilsTest
{

    @org.junit.Test
    public void getRelatedVideo() throws IOException
    {
        AudioTrack audioTrack = YouTubeUtils.getRelatedVideo("cPJUBQd-PNM");
        assertNotNull(audioTrack);
    }
}