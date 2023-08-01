package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static bot.utils.EmbedUtils.createEmbedBuilder;

@Slf4j
public class QueueCommand extends Command {
	private final EventWaiter eventWaiter;

	public QueueCommand(EventWaiter eventWaiter) {
		this.name = "queue";
		this.help = "Show the queue of audio tracks";
		this.guildOnly = true;

		this.eventWaiter = eventWaiter;
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioManager audioManager = event.getGuild().getAudioManager();

		AudioPlayerSendHandler audioPlayerSendHandler =
						(AudioPlayerSendHandler) audioManager.getSendingHandler();
		if (audioPlayerSendHandler == null) {
			event.reply(TextChannelResponses.NO_TRACKS_ON_QUEUE);
			return;
		}

		TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();
		List<AudioTrack> queue = trackScheduler.getQueue();

		if (queue.isEmpty()) {
			event.reply(TextChannelResponses.NO_TRACKS_ON_QUEUE);
			return;
		}

		event.reply(TextChannelResponses.QUEUE_SUCCESS_MESSAGE);
		EmbedPaginator buttonMenu =
						new EmbedPaginator.Builder()
										.setUsers(event.getUser())
										.setEventWaiter(eventWaiter)
										.setTimeout(10, TimeUnit.MINUTES)
										.addItems(createEmbedBuilder(event, trackScheduler, queue))
										.build();
		buttonMenu.display(event.getChannel());
	}
}
