package bot.commands.utilities;

import static bot.commands.utilities.OptionsCommand.OPTION_NAMES;
import static bot.utils.EmbedUtils.setRandomColour;

import bot.entities.OptionEntity;
import bot.repositories.OptionEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

@Component
public class OptionListCommand extends Command
{
    private final OptionEntityRepository optionEntityRepository;

    public OptionListCommand(OptionEntityRepository optionEntityRepository)
    {
        this.optionEntityRepository = optionEntityRepository;

        this.name = "optionlist";
        this.aliases = new String[]{"settingslist", "settinglist", "optionslist"};
        this.help = "Show a list of the current settings.";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.getChannel().sendTyping().queue();

        HashMap<String, Boolean> nameToState = new HashMap<>();

        // get the settings and then
        for (String option : OPTION_NAMES)
        {
            OptionEntity optionEntity = optionEntityRepository.findByServerIdAndName(event.getGuild().getId(), option);

            if (optionEntity == null || optionEntity.getOption())
            {
                nameToState.put(option, true);

            }
            else
            {
                nameToState.put(option, false);
            }
        }

        EmbedBuilder eb = new EmbedBuilder();

        //get a random colour for the embed
        setRandomColour(eb);

        AtomicInteger ordinal = new AtomicInteger(1);
        StringBuilder sb = new StringBuilder();

        nameToState.forEach((setting_name, value) ->
        {
            int itemPosition = ordinal.getAndIncrement();

            sb.append(String.format("`%d.` %s - %s\n\n", itemPosition, setting_name, value));
        });

        eb.setDescription(sb);
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
