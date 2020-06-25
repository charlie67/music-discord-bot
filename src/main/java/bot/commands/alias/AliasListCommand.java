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

        // can only send 2000 characters in a single message so put each alias description onto eachAliasDescription and
        // later combine ones that are less than 2000 characters into their own messages
        ArrayList<String> eachAliasDescription = new ArrayList<>();

        int i = 1;

        for (Alias alias : aliases)
        {
            StringBuilder aliasListString = new StringBuilder();
            aliasListString.append("`").append(i).append(":` `");
            aliasListString.append(alias.getAliasName());
            aliasListString.append("` executes command `");
            aliasListString.append(alias.getCommand().getName());
            aliasListString.append("` with arguments `");
            aliasListString.append(alias.getAliasCommandArguments());
            aliasListString.append("`");
            aliasListString.append("\n");
            i++;
            eachAliasDescription.add(aliasListString.toString());
        }
        ArrayList<String> fullMessagesToSend = new ArrayList<>();

        int index = -1;
        for (String aliasDescription : eachAliasDescription)
        {
            if (fullMessagesToSend.isEmpty())
            {
                fullMessagesToSend.add(aliasDescription);
                index++;
            }
            else
            {
                String previousMessage = fullMessagesToSend.get(index);

                if (aliasDescription.length() + previousMessage.length() < 2000)
                {
                    fullMessagesToSend.remove(index);
                    aliasDescription = previousMessage + aliasDescription;
                    fullMessagesToSend.add(aliasDescription);
                }
                else
                {
                    fullMessagesToSend.add(aliasDescription);
                    index++;
                }
            }
        }


        fullMessagesToSend.forEach(s -> event.getChannel().sendMessage(s).queue());
    }
}
