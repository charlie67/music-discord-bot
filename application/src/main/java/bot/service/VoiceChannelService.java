package bot.service;

import bot.commands.audio.UserInfo;
import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.AudioSearchResultHandler;
import bot.commands.audio.utils.TrackScheduler;
import bot.dao.OptionEntityDao;
import bot.utils.TextChannelResponses;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class VoiceChannelService {

	private static final Logger LOGGER = LogManager.getLogger(VoiceChannelService.class);

	private static boolean isValidURL(String urlString) {
		try {
			URL url = new URL(urlString);
			url.toURI();
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public AudioPlayerSendHandler getAudioPlayerSendHandler(JDA jda, String guildId)
					throws IllegalArgumentException {
		if (guildId == null || guildId.equals("")) {
			throw new IllegalArgumentException("Guild ID is NULL");
		}

		Guild guild = jda.getGuildById(guildId);
		if (guild == null) {
			throw new IllegalArgumentException("Guild is NULL is the ID correct?");
		}

		AudioManager audioManager = guild.getAudioManager();

		if (!audioManager.isConnected()) {
			throw new IllegalArgumentException("Not currently connected to the voice channel");
		}

		return (AudioPlayerSendHandler) audioManager.getSendingHandler();
	}

	public void setPauseStatusOnAudioPlayer(CommandEvent event, boolean pauseStatus)
					throws IllegalArgumentException,
					IllegalAccessException {
		Guild guild = event.getGuild();
		MessageChannel channel = event.getChannel();
		Member member = event.getMember();

		AudioManager audioManager = guild.getAudioManager();

		if (!audioManager.isConnected()) {
			channel.sendMessage(TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE).queue();
			throw new IllegalAccessException("Bot not connected to the voice channel");
		}

		if (audioManager.getConnectedChannel() != null && !audioManager.getConnectedChannel()
						.getMembers().contains(member)) {
			channel.sendMessage(TextChannelResponses.NOT_CONNECTED_TO_VOICE_MESSAGE).queue();
			throw new IllegalAccessException("Member not in the voice channel");
		}

		AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
		AudioPlayer audioPlayer = audioPlayerSendHandler.getAudioPlayer();
		if (audioPlayer.isPaused() == pauseStatus) {
			throw new IllegalArgumentException("Setting the same pause status");
		}
		audioPlayer.setPaused(pauseStatus);
	}

	/**
	 * Search and play a song, joining the required voice channel if necessary
	 *
	 * @param event         The event that cased this to happen
	 * @param playTop       If this should be placed at the top of the queue
	 * @param playerManager Used for creating audio players and loading tracks and playlists.
	 * @throws IllegalArgumentException If an error occurred during play
	 */
	public void searchAndPlaySong(CommandEvent event, boolean playTop,
																AudioPlayerManager playerManager,
																String youtubeApiKey, OptionEntityDao optionEntityDao) throws IllegalArgumentException {
		String argument = event.getArgs();
		MessageChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		Member member = event.getMember();

		if (argument.isEmpty()) {
			channel.sendMessage(TextChannelResponses.NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND).queue();
			return;
		}

		AudioManager audioManager = guild.getAudioManager();

		if (!audioManager.isConnected()) {
			try {
				joinVoiceChannel(member, guild, playerManager);
			} catch (InsufficientPermissionException e) {
				channel.sendMessage(TextChannelResponses.DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL)
								.queue();
				return;
			} catch (IllegalArgumentException e) {
				channel.sendMessage(TextChannelResponses.NOT_CONNECTED_TO_VOICE_MESSAGE).queue();
				return;
			}
		}

		if (audioManager.getConnectedChannel() != null && !audioManager.getConnectedChannel()
						.getMembers().contains(member)) {
			channel.sendMessage(TextChannelResponses.NOT_CONNECTED_TO_VOICE_MESSAGE).queue();
			return;
		}

		channel.sendMessage("Searching for `").append(argument).append("`").queue();
		channel.sendTyping().queue();

		AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
		TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

		// create the userInfo object to attach to the track
		UserInfo userInfo = new UserInfo(event.getChannel().getIdLong(), argument, event.getJDA());

		if (!isValidURL(argument)) {
			LOGGER.info("{} is not a url so prepending ytsearch", argument);
			argument = "ytsearch: ".concat(argument);
		}
		playerManager.loadItem(argument,
						new AudioSearchResultHandler(trackScheduler, audioPlayerSendHandler, channel, argument,
										playTop, userInfo));
	}

	/**
	 * Connect to the voice channel that member is in
	 *
	 * @param member        The member to join
	 * @param guild         The server that the member is in
	 * @param playerManager The player manager for this guild
	 * @throws IllegalArgumentException        If the voice channel can't be joined
	 * @throws InsufficientPermissionException If the bot lacks the permission to join the voice
	 *                                         channel
	 */
	public void joinVoiceChannel(Member member, Guild guild,
															 AudioPlayerManager playerManager)
					throws IllegalArgumentException,
					InsufficientPermissionException {
		GuildVoiceState memberVoiceState = member.getVoiceState();

		if (memberVoiceState == null || !memberVoiceState.inVoiceChannel()) {
			throw new IllegalArgumentException("Unable to join the voice channel");
		}

		AudioManager audioManager = guild.getAudioManager();

		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackScheduler = new TrackScheduler();
		player.addListener(trackScheduler);

		audioManager.setSendingHandler(new AudioPlayerSendHandler(player, trackScheduler));
		audioManager.openAudioConnection(memberVoiceState.getChannel());
	}
}
