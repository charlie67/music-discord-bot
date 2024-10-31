package bot.commands.audio.utils;

import com.google.common.collect.EvictingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TrackScheduler extends AudioEventAdapter {

	private static final Logger LOGGER = LogManager.getLogger(TrackScheduler.class);
	private final EvictingQueue<AudioTrack> historyQueue = EvictingQueue.create(20);

	private List<AudioTrack> queue = new ArrayList<>();

	private AudioTrack loopTrack = null;

	/**
	 * The duration of the queue
	 */
	private long queueDurationInMilliSeconds = 0;

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		LOGGER.info("Track {} ended {}", track.getIdentifier(), endReason);

		if (endReason == AudioTrackEndReason.LOAD_FAILED) {
			loopTrack = null;
			// send message to the text channel saying that the loading failed
			UserInfo userInfo = (UserInfo) track.getUserData();
			TextChannel textChannel = userInfo.getJda().getTextChannelById(userInfo.getChannelId());
			if (textChannel != null) {
				textChannel.sendMessage(String.format("Loading failed for %s", track.getInfo().uri))
								.queue();
			}

			LOGGER.error("The text channel inside track userInfo is null.");
		} else {
			historyQueue.add(track);
		}

		if (!endReason.mayStartNext && !endReason.equals(AudioTrackEndReason.STOPPED)) {
			return;
		}

		if (loopTrack != null) {
			LOGGER.info("Current track {}", track.getInfo().length);

			AudioTrack cloneTrack = loopTrack.makeClone();
			LOGGER.info("cloneTrack track {}", cloneTrack.getInfo().length);

			this.queue(track.makeClone(), true);
		}

		if (!queue.isEmpty()) {
			player.playTrack(nextTrack());
		}
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		LOGGER.error("Error when playing track", exception);
	}

	public void queue(AudioTrack track, boolean queueFirst) {
		queueDurationInMilliSeconds += track.getDuration();
		if (queueFirst) {
			queue.add(0, track);
			return;
		}
		queue.add(track);
	}

	public void clearQueue() {
		queueDurationInMilliSeconds = 0;
		queue.clear();
	}

	public int getQueueSize() {
		return queue.size();
	}

	AudioTrack nextTrack() {
		if (!queue.isEmpty()) {
			AudioTrack audioTrack = queue.get(0);
			queueDurationInMilliSeconds -= audioTrack.getDuration();
			queue.remove(0);
			return audioTrack;
		}

		return null;
	}

	public List<AudioTrack> getQueue() {
		return queue;
	}

	public void setQueue(List<AudioTrack> queue) {
		this.queue = queue;
	}

	public long getQueueDurationInMilliSeconds() {
		return queueDurationInMilliSeconds;
	}

	public AudioTrack getLoopTrack() {
		return loopTrack;
	}

	public void setLoopTrack(AudioTrack loopTrack) {
		this.loopTrack = loopTrack;
	}

	public void remove(int trackToRemove) {
		queue.remove(trackToRemove);
	}

	public EvictingQueue<AudioTrack> getHistory() {
		return historyQueue;
	}
}
