package bot.listeners.messageListeners;

import bot.Entities.AliasEntity;
import bot.repositories.AliasEntityRepository;
import bot.service.BotService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliasCommandHandler
{
    private final Logger LOGGER = LogManager.getLogger(AliasCommandHandler.class);

    private BotService botService;

    private final AliasEntityRepository aliasEntityRepository;

    @Autowired
    public AliasCommandHandler(AliasEntityRepository aliasEntityRepository)
    {
        this.aliasEntityRepository = aliasEntityRepository;
    }

    public void handle(@NotNull MessageReceivedEvent event)
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
            queryParts[0] = "";

            String commandExtraArgs = String.join(" ", queryParts).trim();

            AliasEntity aliasEntity = aliasEntityRepository.findByServerIdAndName(guildID, commandName);

            if (aliasEntity == null)
            {
                // not an alias
                return;
            }

            String args = aliasEntity.getArgs().equals("") ? commandExtraArgs : aliasEntity.getArgs();

            Command commandToExecute = botService.getCommandFromName(aliasEntity.getCommand());
            commandToExecute.run(new CommandEvent(event, args, botService.getClient()));
        }
    }

    // break a circular dependency
    public void setBotService(BotService botService)
    {
        this.botService = botService;
    }
}
