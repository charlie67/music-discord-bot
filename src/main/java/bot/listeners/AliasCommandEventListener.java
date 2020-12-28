package bot.listeners;

import bot.Entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.service.BotService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class AliasCommandEventListener extends ListenerAdapter
{
    private final Logger LOGGER = LogManager.getLogger(AliasCommandEventListener.class);

    private BotService botService;

    private final AliasEntityRepository aliasEntityRepository;

    @Autowired
    public AliasCommandEventListener(AliasEntityRepository aliasEntityRepository)
    {
        this.aliasEntityRepository = aliasEntityRepository;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event)
    {
        // Return if it's a bot
        if (event.getAuthor().isBot())
        {
            return;
        }

        String guildID = event.getGuild().getId();

        String rawContent = event.getMessage().getContentRaw();

        if (rawContent.startsWith(BotService.COMMAND_PREFIX))
        {
            String query = rawContent.replace(BotService.COMMAND_PREFIX, "");

            String[] queryParts = query.split("\\s+");
            String commandName = queryParts[0];

            AliasEntity aliasEntity = aliasEntityRepository.findByServerIdAndName(guildID, commandName);

            if (aliasEntity == null)
            {
                // not an alias
                return;
            }

            Command commandToExecute = botService.getCommandFromName(aliasEntity.getCommand());
            commandToExecute.run(new CommandEvent(event, aliasEntity.getArgs(), botService.getClient()));
        }
    }

    // break a circular dependency
    public void setBotService(BotService botService)
    {
        this.botService = botService;
    }
}
