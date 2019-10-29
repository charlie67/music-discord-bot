package bot.configuration;

import bot.service.BotService;
import bot.service.impl.BotServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringApplicationConfiguration
{
    @Bean
    public BotService botService()
    {
        return new BotServiceImpl();
    }
}
