package bot;

import bot.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@Slf4j
public class ApplicationBootstrap implements CommandLineRunner {

  private final BotService botService;

  public ApplicationBootstrap(BotService botService) {
    this.botService = botService;
  }

  public static void main(String[] args) {
    log.info("Starting Discord Bot");
    SpringApplication.run(ApplicationBootstrap.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    this.botService.startBot();
  }
}
