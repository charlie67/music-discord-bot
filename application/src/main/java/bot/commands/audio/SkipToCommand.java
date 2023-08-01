package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SkipToCommand extends Command {

	private final VoiceChannelService voiceChannelService;

	@Autowired
	public SkipToCommand(VoiceChannelService voiceChannelService) {
		this.name = "skipto";
		this.help = "Skips to a certain position in the queue.";
		this.guildOnly = true;

		this.options = List.of(Option.createOption(OptionName.SKIP_TO_POSITION, true, 0));

		this.voiceChannelService = voiceChannelService;
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioPlayerSendHandler audioPlayerSendHandler;
		try {
			audioPlayerSendHandler =
							voiceChannelService.getAudioPlayerSendHandler(event.getJDA(), event.getGuild().getId());
		} catch (IllegalArgumentException e) {
			event.getChannel().sendMessage("**Not currently connected to the voice channel**").queue();
			return;
		}

		int elementSkipTo;

		try {
			elementSkipTo = event.getOption(OptionName.SKIP_TO_POSITION).getAsInt();
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage("**You need to provide a number to skip to.**").queue();
			return;
		}

		TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

		List<AudioTrack> queue = trackScheduler.getQueue();
		List<AudioTrack> sublistQueue = queue.subList(elementSkipTo - 1, queue.size());

		trackScheduler.setQueue(sublistQueue);
		audioPlayerSendHandler.getAudioPlayer().stopTrack();
		event.reactSuccess();
	}

	@Override
	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
		final String message = event.getMessage().getContentRaw();
		String[] parts = message.split("\\s+", 2);

		OptionValue optionValue = TextOptionValue.builder().optionName(OptionName.SKIP_TO_POSITION).optionValue(parts[1]).jda(event.getJDA()).build();
		return Map.of(OptionName.SKIP_TO_POSITION, optionValue);
	}
}
