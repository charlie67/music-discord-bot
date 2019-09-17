package bot.main;

import bot.commands.audio.ClearQueueCommand;
import bot.commands.audio.JoinCommand;
import bot.commands.audio.LeaveCommand;
import bot.commands.audio.NowPlayingCommand;
import bot.commands.audio.PlayCommand;
import bot.commands.audio.QueueCommand;
import bot.commands.audio.RemoveCommand;
import bot.commands.audio.SeekCommand;
import bot.commands.audio.ShuffleCommand;
import bot.commands.audio.SkipSongCommand;
import bot.commands.audio.utils.VoiceChannelEventListener;
import bot.commands.utilities.PingCommand;
import bot.utils.GetSystemEnvironmentOrDefaultValue;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class BotController
{

    public static void main(String[] args) throws LoginException
    {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setPrefix("-");
        builder.setActivity(null);
        builder.setOwnerId("106139573561626624");
        builder.addCommands(new JoinCommand(playerManager), new PlayCommand(playerManager), new QueueCommand(),
                new LeaveCommand(), new NowPlayingCommand(), new SkipSongCommand(), new ClearQueueCommand(),
                new RemoveCommand(), new SeekCommand(), new PingCommand(), new ShuffleCommand());

        CommandClient client = builder.build();

        JDA jda = new JDABuilder(GetSystemEnvironmentOrDefaultValue.get("DISCORD_BOT_KEY")).addEventListeners(client, new VoiceChannelEventListener()).build();
    }
}
