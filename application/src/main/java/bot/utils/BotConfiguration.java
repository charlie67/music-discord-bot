package bot.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "bot")
@Configuration("BotConfiguration")
@Getter
@Setter
@Data
public class BotConfiguration
{
    String youtubeApiKey;
    String discordKey;
    String userId;
    String redditClientId;
    String redditClientSecret;
    String ownerId;
}
