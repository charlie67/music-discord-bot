package bot.commands.audio;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RemoveCommand extends Command
{
    public RemoveCommand()
    {
        this.name = "remove";
        this.help = "Remove the requested song from the queue";
    }

    @Override
    protected void execute(CommandEvent event)
    {

    }
}
