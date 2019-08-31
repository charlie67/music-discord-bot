package bot.commands.audio.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler
{
    private final AudioPlayer audioPlayer;

    private final  TrackScheduler trackScheduler;

    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(@NotNull AudioPlayer audioPlayer, @NotNull TrackScheduler trackScheduler) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = trackScheduler;
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    public TrackScheduler getTrackScheduler()
    {
        return trackScheduler;
    }

    public AudioPlayer getAudioPlayer()
    {
        return audioPlayer;
    }
}
