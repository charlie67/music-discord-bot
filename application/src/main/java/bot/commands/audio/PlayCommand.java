package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PlayCommand extends Command {

  private static final Logger LOGGER = LogManager.getLogger(PlayCommand.class);
  private final AudioPlayerManager playerManager;
  private final VoiceChannelService voiceChannelService;

  public PlayCommand(AudioPlayerManager playerManager, VoiceChannelService voiceChannelService) {
    this.playerManager = playerManager;
    this.name = "play";
    this.help = "Plays a song with the given name or url.";

    CommandData commandData = new CommandData(this.name, this.help);
    commandData.addOptions(
            new OptionData(OptionType.STRING, "song", "song or url to play", true));
    this.commandData = commandData;

    this.allowedInSlash = false;

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    LOGGER.info("Play command triggered with message {}", event.getArgs());
    voiceChannelService.searchAndPlaySong(event, false, playerManager);
  }

  @Override
  public void executeSlashCommand(SlashCommandEvent event) {
    event.deferReply().queue();

    event.getOptions().get(0).getAsString();
  }
}
