package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.service.VoiceChannelService;
import bot.utils.EmoteHelper;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bot.utils.EmoteHelper.THUMBS_UP;
import static bot.utils.EmoteHelper.WAVE_STRING;

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
			audioPlayerSendHandler = voiceChannelService.getAudioPlayerSendHandler(event.getJDA(),
							event.getGuild().getId());
		} catch (IllegalArgumentException e) {
			event.reply("**Not currently connected to the voice channel**");
			return;
		}

		audioPlayerSendHandler.getAudioPlayer().stopTrack();
		audioManager.closeAudioConnection();
		audioPlayerSendHandler.getTrackScheduler().clearQueue();
		event.reactSuccessOrReply(WAVE_STRING);
	}
}
