package bot.service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public interface BotService {

    void startBot() throws LoginException;

    void shutdownBot();

    AudioPlayerManager getAudioPlayerManager();

    JDA getJda();
}