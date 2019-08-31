package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.commands.audio.utils.TrackScheduler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command
{
    //The audio player manager that the audio player will be created from
    private final AudioPlayerManager playerManager;

    public JoinCommand(AudioPlayerManager playerManager) {
        this.playerManager = playerManager;
        this.name = "join";
        this.help = "Joins the voice channel that the user is currently connected to";
    }

    @Override
    protected void execute(CommandEvent event)
    {
        joinVoiceChannel(event, playerManager);
    }

    static void joinVoiceChannel(CommandEvent event, AudioPlayerManager playerManager)
    {
        GuildVoiceState voiceState = event.getMember().getVoiceState();

        if (voiceState == null || !voiceState.inVoiceChannel())
        {
            event.getChannel().sendMessage("You need to be in a voice channel.").queue();
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);

        audioManager.setSendingHandler(new AudioPlayerSendHandler(player, trackScheduler));
        audioManager.openAudioConnection(voiceState.getChannel());
    }
}