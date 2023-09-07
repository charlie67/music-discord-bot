package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.TextChannelResponses;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import com.google.common.collect.EvictingQueue;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static bot.utils.EmbedUtils.createEmbedBuilder;

public class HistoryCommand extends Command {
	private final EventWaiter eventWaiter;

	public HistoryCommand(EventWaiter eventWaiter) {
		this.name = "playhistory";
		this.aliases = new String[]{"history"};
		this.help = "Show the play history";
		this.eventWaiter = eventWaiter;
	}

	@Override
	protected void execute(CommandEvent event) {
		AudioManager audioManager = event.getGuild().getAudioManager();

		AudioPlayerSendHandler audioPlayerSendHandler =
						(AudioPlayerSendHandler) audioManager.getSendingHandler();

		if (audioPlayerSendHandler == null) {
			event.getChannel().sendMessage(TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE).queue();
			return;
		}

		EvictingQueue<AudioTrack> history =
						audioPlayerSendHandler.getTrackScheduler().getHistory();

		if (history.isEmpty()) {
			event.getChannel().sendMessage(TextChannelResponses.NO_HISTORY_TO_SHOW).queue();
			return;
		}

		try {
			EmbedPaginator buttonMenu =
							new EmbedPaginator.Builder()
											.setUsers(event.getUser())
											.setEventWaiter(eventWaiter)
											.setTimeout(10, TimeUnit.MINUTES)
											.addItems(createEmbedBuilder(event, null, new ArrayList<>(history)))
											.build();
			buttonMenu.display(event.getChannel());
			event.reply("**History**");
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage(TextChannelResponses.CANT_DISPLAY_QUEUE_PAGE).queue();
		}
	}
}
