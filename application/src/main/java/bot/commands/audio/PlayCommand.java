package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PlayCommand extends Command {

	private final AudioPlayerManager playerManager;
	private final VoiceChannelService voiceChannelService;

	public PlayCommand(AudioPlayerManager playerManager, VoiceChannelService voiceChannelService) {
		this.playerManager = playerManager;
		this.name = "play";
		this.help = "Plays a song with the given name or url.";

		Option options = Option.createOption(Response.PLAY_ARGUMENT, true, 0);
		this.options = List.of(options);

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		voiceChannelService.searchAndPlaySong(event, false, playerManager);
	}
}
