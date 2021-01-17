package bot.commands.text;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;

public class DmCommand extends Command
{
    public DmCommand()
    {
        this.name = "d";
        this.hidden = true;
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        // message format of -d <ID> message only in a dm
        if (!event.isFromType(ChannelType.PRIVATE))
        {
            return;
        }

        String[] queryParts = event.getArgs().split("\\s+");

        if (queryParts.length < 2)
        {
            fail(event);
            return;
        }

        String userID = queryParts[0];
        queryParts[0] = "";

        String message = String.join(" ", queryParts).trim();

        User userToDm = event.getJDA().getUserById(userID);

        if (userToDm == null)
        {
            fail(event);
            return;
        }

        userToDm.openPrivateChannel().queue((channel) -> channel.sendMessage(message).queue());
    }

    private void fail(CommandEvent event)
    {
        event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage("noob").queue());
    }
}
