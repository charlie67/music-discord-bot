package bot.configuration;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AudioPlayerConfiguration {

  @Bean
  public YoutubeAudioSourceManager youtubeAudioSourceManager(
      @Value("${bot.youtube.email}") String youtubeEmail,
      @Value("${bot.youtube.password}") String youtubePassword) {
    return new YoutubeAudioSourceManager(true, youtubeEmail, youtubePassword);
  }

  @Bean
  public AudioPlayerManager playerManager(YoutubeAudioSourceManager youtubeAudioSourceManager) {
    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    playerManager.registerSourceManager(youtubeAudioSourceManager);
    playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
    playerManager.registerSourceManager(new BandcampAudioSourceManager());
    playerManager.registerSourceManager(new VimeoAudioSourceManager());
    playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
    playerManager.registerSourceManager(new BeamAudioSourceManager());
    playerManager.registerSourceManager(new GetyarnAudioSourceManager());
    playerManager.registerSourceManager(
        new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));

    return playerManager;
  }
}
