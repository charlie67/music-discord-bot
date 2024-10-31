package bot.commands.audio;

import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bot.utils.EmoteHelper.WAVE_STRING;
import static bot.utils.TextChannelResponses.DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL;
import static bot.utils.TextChannelResponses.USER_NOT_CONNECTED_TO_VOICE_CHANNEL;

@Component
public class JoinCommand extends Command {

	//The audio player manager that the audio player will be created from
	private final AudioPlayerManager playerManager;
	private final VoiceChannelService voiceChannelService;

	@Autowired
	public JoinCommand(AudioPlayerManager playerManager, VoiceChannelService voiceChannelService) {
		this.playerManager = playerManager;
		this.name = "join";
		this.help = "Joins the voice channel that the user is currently connected to";

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		try {
			voiceChannelService.joinVoiceChannel(event.getMember(), event.getGuild(),
							playerManager);
			event.reactSuccessOrReply(WAVE_STRING);
		} catch (IllegalArgumentException e) {
			event.reply(USER_NOT_CONNECTED_TO_VOICE_CHANNEL);
		} catch (InsufficientPermissionException e) {
			event.reply(DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL);
		}
	}
}