package bot.service.impl;

import bot.commands.audio.ClearQueueCommand;
import bot.commands.audio.JoinCommand;
import bot.commands.audio.LeaveCommand;
import bot.commands.audio.NowPlayingCommand;
import bot.commands.audio.PlayCommand;
import bot.commands.audio.PlayTopCommand;
import bot.commands.audio.QueueCommand;
import bot.commands.audio.RemoveCommand;
import bot.commands.audio.SeekCommand;
import bot.commands.audio.ShuffleCommand;
import bot.commands.audio.SkipSongCommand;
import bot.commands.audio.SkipToCommand;
import bot.commands.audio.utils.VoiceChannelEventListener;
import bot.commands.image.RedditSearchCommand;
import bot.commands.utilities.PingCommand;
import bot.service.BotService;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.security.auth.login.LoginException;

public class BotServiceImpl implements BotService
{
    @Value("${DISCORD_BOT_KEY}")
    private String DISCORD_BOT_KEY;

    @Value("${OWNER_ID}")
    private String OWNER_ID;

    private JDA jda;
    private AudioPlayerManager playerManager;

    @Override
    public void startBot() throws LoginException
    {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        CommandClientBuilder builder = new CommandClientBuilder();

        builder.setPrefix("-");
        builder.setActivity(null);
        builder.setOwnerId(OWNER_ID);
        builder.addCommands(new JoinCommand(playerManager), new PlayCommand(playerManager),
                new PlayTopCommand(playerManager), new QueueCommand(), new LeaveCommand(), new NowPlayingCommand(),
                new SkipSongCommand(), new ClearQueueCommand(), new RemoveCommand(), new SeekCommand(),
                new PingCommand(), new ShuffleCommand(), new SkipToCommand(), new RedditSearchCommand());

        CommandClient client = builder.build();

        this.jda = new JDABuilder(DISCORD_BOT_KEY).addEventListeners(client, new VoiceChannelEventListener()).build();
    }

    @Override
    public void shutdownBot()
    {
        this.jda.shutdown();
    }

    @Override
    public JDA getJda()
    {
        return jda;
    }

    public AudioPlayerManager getAudioPlayerManager()
    {
        return playerManager;
    }
}