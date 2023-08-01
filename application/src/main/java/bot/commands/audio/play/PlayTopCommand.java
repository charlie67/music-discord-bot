package bot.commands.audio.play;

import bot.configuration.BotConfiguration;
import bot.dao.OptionEntityDao;
import bot.service.VoiceChannelService;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayTopCommand extends PlayBaseCommand {

	private final AudioPlayerManager playerManager;
	private final String youtubeApiKey;
	private final OptionEntityDao optionEntityDao;
	private final VoiceChannelService voiceChannelService;

	@Autowired
	public PlayTopCommand(
					AudioPlayerManager playerManager,
					BotConfiguration configuration,
					OptionEntityDao optionEntityDao,
					VoiceChannelService voiceChannelService) {
		this.playerManager = playerManager;
		this.name = "playtop";
		this.aliases = new String[]{"pt"};
		this.help = "Plays a song with the given name or url and places it at the top of the queue.";
		this.guildOnly = true;
		this.botPermissions = new Permission[]{Permission.VOICE_CONNECT, Permission.VOICE_SPEAK};

		this.options = List.of(Option.createOption(OptionName.PLAY_AUDIO_SONG_NAME, true, 0));

		this.youtubeApiKey = configuration.getYoutubeApiKey();
		this.optionEntityDao = optionEntityDao;

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		voiceChannelService.searchAndPlaySong(
						event, true, playerManager, youtubeApiKey, optionEntityDao);
	}
}
