package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

import static bot.utils.EmoteHelper.THUMBS_UP_STRING;
import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NO_ARGUMENT;
import static bot.utils.TextChannelResponses.REMOVE_COMMAND_NO_TRACK_TO_REMOVE;

@Component
public class RemoveCommand extends Command {
	private final Logger LOGGER = LogManager.getLogger(RemoveCommand.class);

	public RemoveCommand() {
		this.name = "remove";
		this.help = "Remove the requested song from the queue.";

		this.options = List.of(Option.createOption(Response.REMOVE_POSITION, true, 0));
	}

	@Override
	protected void execute(CommandEvent event) {
		int trackToRemove;

		try {
			trackToRemove = event.getOption(Response.REMOVE_POSITION).getAsInt();
		} catch (NumberFormatException e) {
			event.reply(REMOVE_COMMAND_NO_ARGUMENT);
			return;
		}

		LOGGER.info("Removing track {} from the queue", trackToRemove);

		AudioManager audioManager = event.getGuild().getAudioManager();

		AudioPlayerSendHandler audioPlayerSendHandler =
						(AudioPlayerSendHandler) audioManager.getSendingHandler();

		if (audioPlayerSendHandler == null) {
			event.reply(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, trackToRemove));
			return;
		}

		TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

		try {
			trackScheduler.remove(trackToRemove - 1);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.info("Track {} is not a track on the queue", trackToRemove);
			event.reply(String.format(REMOVE_COMMAND_NO_TRACK_TO_REMOVE, trackToRemove));
			return;
		}

		event.reactSuccessOrReply(THUMBS_UP_STRING);
	}
}
