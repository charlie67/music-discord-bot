package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.service.VoiceChannelService;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClearQueueCommand extends Command {

	private final VoiceChannelService voiceChannelService;

	@Autowired
	public ClearQueueCommand(VoiceChannelService voiceChannelService) {
		this.name = "clearqueue";
		this.aliases = new String[]{"clear"};
		this.help = "Clear the queue for this server";
		this.guildOnly = true;

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioPlayerSendHandler audioPlayerSendHandler;

		GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

		if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
			event.reply(TextChannelResponses.USER_NOT_CONNECTED_TO_VOICE_CHANNEL);
		}

		try {
			audioPlayerSendHandler =
							voiceChannelService.getAudioPlayerSendHandler(event.getJDA(), event.getGuild().getId());
		} catch (IllegalArgumentException e) {
			event.reply(TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE);
			return;
		}

		audioPlayerSendHandler.getTrackScheduler().clearQueue();
		event.reply(TextChannelResponses.QUEUE_CLEARED);
	}
}
