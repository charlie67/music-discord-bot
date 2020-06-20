package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static bot.utils.TextChannelResponses.NO_ALIASES_SET;

public class AliasListCommand extends Command
{
    private final AliasCommandEventListener aliasCommandEventListener;

    public AliasListCommand(AliasCommandEventListener aliasCommandEventListener)
    {
        this.name = "aliaslist";
        this.aliases = new String[]{"al"};
        this.help = "List all the aliases for this server";

        this.aliasCommandEventListener = aliasCommandEventListener;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String guildId = event.getGuild().getId();
        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildId);

        if (guildAliasHolder == null || guildAliasHolder.getAliasEntityList().size() == 0)
        {
            event.getChannel().sendMessage(NO_ALIASES_SET).queue();
            return;
        }

        HashMap<String, Alias> aliasHashMap = guildAliasHolder.getAliasNameToAliasObject();
        List<Alias> aliases = new ArrayList<>();

        aliasHashMap.keySet().forEach(key ->
        {
            Alias alias = aliasHashMap.get(key);
            aliases.add(alias);
        });

        StringBuilder aliasListString = new StringBuilder();
        int i = 1;

        for (Alias alias : aliases)
        {
            aliasListString.append("`").append(i).append(":` `");
            aliasListString.append(alias.getAliasName());
            aliasListString.append("` executes command `");
            aliasListString.append(alias.getCommand().getName());
            aliasListString.append("` with arguments `");
            aliasListString.append(alias.getAliasCommandArguments());
            aliasListString.append("`");
            aliasListString.append("\n");
            i++;
        }

        event.getChannel().sendMessage(aliasListString.toString()).queue();
    }
}
