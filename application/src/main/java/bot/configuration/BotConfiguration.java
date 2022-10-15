package bot.configuration;

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
public class BotConfiguration {

  private String youtubeApiKey;
  private String discordKey;
  private String userId;
  private String redditClientId;
  private String redditClientSecret;
  private String ownerId;
  private String commandPrefix;
}
