package bot.commands.audio.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceChannelUtils
{
    public static void joinVoiceChannel(CommandEvent event, AudioPlayerManager playerManager) throws IllegalArgumentException
    {
        GuildVoiceState voiceState = event.getMember().getVoiceState();

        if (voiceState == null || !voiceState.inVoiceChannel())
        {
            throw new IllegalArgumentException("Unable to join the voice channel");
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);

        audioManager.setSendingHandler(new AudioPlayerSendHandler(player, trackScheduler));
        audioManager.openAudioConnection(voiceState.getChannel());
    }
    public static void SearchAndPlaySong(CommandEvent event, boolean playTop, AudioPlayerManager playerManager)
    {
        String argument = event.getArgs();

        if (argument.isEmpty())
        {
            event.getChannel().sendMessage("**Need to provide something to play**").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected())
        {
            try
            {
                VoiceChannelUtils.joinVoiceChannel(event, playerManager);
            } catch (IllegalArgumentException e)
            {
                event.getChannel().sendMessage("**You need to be in a voice channel.**").queue();
                return;
            }
        }
        event.getChannel().sendMessage("Searching for `").append(argument).append("`").queue();
        event.getChannel().sendTyping().queue();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();

        playerManager.loadItem(argument, new AudioSearchResultHandler(trackScheduler, event, audioPlayerSendHandler, argument, playTop));
    }

    public static AudioPlayerSendHandler getAudioPlayerSendHandler(Guild guild) throws IllegalArgumentException
    {
        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected())
        {
            throw new IllegalArgumentException("Not currently connected to the voice channel");
        }

        return (AudioPlayerSendHandler) audioManager.getSendingHandler();
    }
}
