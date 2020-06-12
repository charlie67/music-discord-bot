package bot.service.impl;

import bot.commands.alias.AliasCreateCommand;
import bot.commands.audio.ClearQueueCommand;
import bot.commands.audio.JoinCommand;
import bot.commands.audio.LeaveCommand;
import bot.commands.audio.LoopCommand;
import bot.commands.audio.NowPlayingCommand;
import bot.commands.audio.PauseCommand;
import bot.commands.audio.PlayCommand;
import bot.commands.audio.PlayTopCommand;
import bot.commands.audio.QueueCommand;
import bot.commands.audio.RemoveCommand;
import bot.commands.audio.ResumeCommand;
import bot.commands.audio.SeekCommand;
import bot.commands.audio.ShuffleCommand;
import bot.commands.audio.SkipSongCommand;
import bot.commands.audio.SkipToCommand;
import bot.commands.image.RedditSearchCommand;
import bot.commands.utilities.PingCommand;
import bot.listeners.AliasCommandEventListener;
import bot.listeners.VoiceChannelEventListener;
import bot.service.BotService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_EMOJIS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGE_REACTIONS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

public class BotServiceImpl implements BotService
{
    @Value("${DISCORD_BOT_KEY}")
    private String DISCORD_BOT_KEY;

    @Value("${OWNER_ID}")
    private String OWNER_ID;

    private JDA jda;
    private AudioPlayerManager playerManager;

    public static final String COMMAND_PREFIX = "-";

    @Override
    public void startBot() throws LoginException
    {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        CommandClientBuilder builder = new CommandClientBuilder();

        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener);

        builder.setPrefix(COMMAND_PREFIX);
        builder.setActivity(null);
        builder.setOwnerId(OWNER_ID);
        builder.addCommands(new JoinCommand(playerManager), new PlayCommand(playerManager),
                new PlayTopCommand(playerManager), new QueueCommand(), new LeaveCommand(), new NowPlayingCommand(),
                new SkipSongCommand(), new ClearQueueCommand(), new RemoveCommand(), new SeekCommand(),
                new PingCommand(), new ShuffleCommand(), new SkipToCommand(), new RedditSearchCommand(),
                new PauseCommand(), new ResumeCommand(), new LoopCommand(), aliasCreateCommand);

        CommandClient client = builder.build();
        aliasCommandEventListener.setCommandClient(client);

        HashMap<String, Command> commandNameToCommandMap = new HashMap<>();

        Set<String> commandNameSet = new HashSet<>();
        client.getCommands().forEach(command ->
        {
            commandNameToCommandMap.put(command.getName(), command);
            for (String commandAlias : command.getAliases())
            {
                commandNameToCommandMap.put(commandAlias, command);
            }

            commandNameSet.add(command.getName());
            Collections.addAll(commandNameSet, command.getAliases());
        });

        aliasCreateCommand.setAllCurrentCommandNames(commandNameSet);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        this.jda = JDABuilder.create(DISCORD_BOT_KEY,
                GUILD_MEMBERS, GUILD_VOICE_STATES, GUILD_MESSAGES,
                GUILD_MESSAGE_REACTIONS, GUILD_PRESENCES, GUILD_EMOJIS).addEventListeners(client,
                new VoiceChannelEventListener(), aliasCommandEventListener).build();
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
