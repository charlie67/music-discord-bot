package bot.commands.utilities;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;


public class PingCommand extends Command
{
    public PingCommand()
    {
        this.name = "ping";
        this.help = "Check the bot's response time to Discord";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        long ping = event.getJDA().getGatewayPing();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(String.format(":hourglass: %dms", ping));
        eb.setColor(Color.GREEN);
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
