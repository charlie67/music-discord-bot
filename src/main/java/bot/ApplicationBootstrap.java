package bot;

import bot.repositories.AliasEntityRepository;
import bot.repositories.GuildAliasHolderEntityRepository;
import bot.service.BotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties
public class ApplicationBootstrap implements CommandLineRunner
{
    private final AliasEntityRepository aliasEntityRepository;

    private final GuildAliasHolderEntityRepository guildAliasHolderEntityRepository;

    private final BotService botService;

    public ApplicationBootstrap(BotService botService, AliasEntityRepository aliasEntityRepository,
                                GuildAliasHolderEntityRepository guildAliasHolderEntityRepository)
    {
        this.botService = botService;
        this.aliasEntityRepository = aliasEntityRepository;
        this.guildAliasHolderEntityRepository = guildAliasHolderEntityRepository;
    }

    public static void main(String[] args)
    {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        this.botService.startBot(guildAliasHolderEntityRepository);
    }
}
