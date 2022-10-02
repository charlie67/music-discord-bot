package bot.utils.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class ApiMessageReceivedEvent extends MessageReceivedEvent
{
    public ApiMessageReceivedEvent(@NotNull JDA api, long responseNumber, @NotNull Message message)
    {
        super(api, responseNumber, message);
    }
}
