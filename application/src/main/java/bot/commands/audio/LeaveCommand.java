package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveCommand extends Command {

	private final VoiceChannelService voiceChannelService;

	@Autowired
	public LeaveCommand(VoiceChannelService voiceChannelService) {
		this.name = "leave";
		this.aliases = new String[]{"die", "stop"};
		this.help = "Leave the currently connected voice channel";

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioManager audioManager = event.getGuild().getAudioManager();
		AudioPlayerSendHandler audioPlayerSendHandler;
		try {
			audioPlayerSendHandler =
							voiceChannelService.getAudioPlayerSendHandler(event.getJDA(), event.getGuild().getId());
		} catch (IllegalArgumentException e) {
			event.reply(TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE);
			event.reactError();
			return;
		}

		if (!voiceChannelService.isUserConnectedToAudioChannel(event.getGuild(), event.getMember())) {
			event.reply(TextChannelResponses.USER_NOT_CONNECTED_TO_VOICE_CHANNEL);
			return;
		}

		// tell the track scheduler that the bot is leaving the vc and not to find related videos.
		audioPlayerSendHandler.getTrackScheduler().setLeaveFlag(true);
		audioPlayerSendHandler.getAudioPlayer().stopTrack();
		audioManager.closeAudioConnection();
		audioPlayerSendHandler.getTrackScheduler().clearQueue();
		event.reactSuccessOrReply(TextChannelResponses.BOT_LEFT_VOICE);
	}
}
