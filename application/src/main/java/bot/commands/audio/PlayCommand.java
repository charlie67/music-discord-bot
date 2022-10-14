package bot.commands.audio;

import bot.commands.audio.utils.VoiceChannelUtils;
import bot.dao.OptionEntityDao;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayCommand extends Command {

    private static final Logger LOGGER = LogManager.getLogger(PlayCommand.class);
    private final AudioPlayerManager playerManager;
    private final String youtubeApiKey;
    private final OptionEntityDao optionEntityDao;

    public PlayCommand(AudioPlayerManager playerManager, String youtubeApiKey,
        OptionEntityDao optionEntityDao) {
        this.playerManager = playerManager;
        this.name = "play";
        this.help = "Plays a song with the given name or url.";

        CommandData commandData = new CommandData(this.name, this.help);
        commandData.addOptions(
            new OptionData(OptionType.STRING, "song", "song or url to play", true));
        this.commandData = commandData;

        this.allowedInSlash = false;

        this.youtubeApiKey = youtubeApiKey;
        this.optionEntityDao = optionEntityDao;
    }

    @Override
    protected void execute(CommandEvent event) {
        LOGGER.info("Play command triggered with message {}", event.getArgs());
        VoiceChannelUtils.searchAndPlaySong(event, false, playerManager, youtubeApiKey,
            optionEntityDao);
    }

    @Override
    public void executeSlashCommand(SlashCommandEvent event) {
        event.deferReply().queue();

        event.getOptions().get(0).getAsString();
    }
}
