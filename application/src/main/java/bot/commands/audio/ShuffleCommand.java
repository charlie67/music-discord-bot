package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static bot.utils.EmoteHelper.THUMBS_UP_STRING;
import static bot.utils.EmoteHelper.WAVE_STRING;

@Component
public class ShuffleCommand extends Command {
	public ShuffleCommand() {
		this.name = "shuffle";
		this.help = "Shuffle the queue";
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioManager audioManager = event.getGuild().getAudioManager();

		if (!audioManager.isConnected()) {
			event.reply("**Not currently connected to the voice channel**");
			return;
		}

		AudioPlayerSendHandler audioPlayerSendHandler =
						(AudioPlayerSendHandler) audioManager.getSendingHandler();
		TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

		List<AudioTrack> queue = trackScheduler.getQueue();
		Collections.shuffle(queue);
		trackScheduler.setQueue(queue);

		event.reactSuccessOrReply(THUMBS_UP_STRING);
	}
}
