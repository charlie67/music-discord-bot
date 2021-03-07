package bot.service;

import bot.api.dto.TriggerCommandDto;
import bot.commands.alias.AliasCreateCommand;
import bot.commands.alias.AliasDeleteCommand;
import bot.commands.alias.AliasListCommand;
import bot.commands.audio.ClearQueueCommand;
import bot.commands.audio.HistoryCommand;
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
import bot.commands.text.DmCommand;
import bot.commands.text.EchoTextCommand;
import bot.commands.text.WhisperTextCommand;
import bot.commands.utilities.PingCommand;
import bot.listeners.VoiceChannelEventListener;
import bot.repositories.AliasEntityRepository;
import bot.utils.BotConfiguration;
import bot.utils.command.Command;
import bot.utils.command.CommandClient;
import bot.utils.command.CommandClientBuilder;
import bot.utils.command.CommandEvent;
import bot.utils.command.impl.ApiCommandEvent;
import bot.utils.command.impl.ApiMessage;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_EMOJIS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGE_REACTIONS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;

@Service
public class BotService
{
    public static final String COMMAND_PREFIX = "-";
    private final String discordBotKey;
    private final String ownerId;
    private final BotConfiguration botConfiguration;
    private final RedditSearchCommand redditSearchCommand;
    private final AliasEntityRepository aliasEntityRepository;
    private final AliasCreateCommand aliasCreateCommand;
    private final AliasDeleteCommand aliasDeleteCommand;
    private final AliasListCommand aliasListCommand;
    private JDA jda;
    private AudioPlayerManager playerManager;
    private CommandClient client;

    @Autowired
    public BotService(AliasCreateCommand aliasCreateCommand, AliasDeleteCommand aliasDeleteCommand,
                      AliasListCommand aliasListCommand, AliasEntityRepository aliasEntityRepository,
                      RedditSearchCommand redditSearchCommand, BotConfiguration botConfiguration)
    {
        this.aliasCreateCommand = aliasCreateCommand;
        this.aliasDeleteCommand = aliasDeleteCommand;
        this.aliasListCommand = aliasListCommand;
        this.aliasEntityRepository = aliasEntityRepository;

        this.redditSearchCommand = redditSearchCommand;

        this.discordBotKey = botConfiguration.getDiscordKey();
        this.ownerId = botConfiguration.getOwnerId();
        this.botConfiguration = botConfiguration;
    }

    public void startBot() throws LoginException
    {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix(COMMAND_PREFIX)
                .setActivity(null)
                .setOwnerId(ownerId)
                .setAliasEntityRepository(aliasEntityRepository)
                .setAliasCreateCommand(aliasCreateCommand)
                .addCommands(new JoinCommand(playerManager, botConfiguration.getYoutubeApiKey()),
                        new PlayCommand(playerManager, botConfiguration.getYoutubeApiKey()),
                        new PlayTopCommand(playerManager, botConfiguration.getYoutubeApiKey()), new QueueCommand(),
                        new LeaveCommand(), new NowPlayingCommand(), new SkipSongCommand(), new ClearQueueCommand(),
                        new RemoveCommand(), new SeekCommand(), new PingCommand(), new ShuffleCommand(), new SkipToCommand(),
                        redditSearchCommand, new PauseCommand(), new ResumeCommand(), new LoopCommand(),
                        aliasListCommand, aliasDeleteCommand, new EchoTextCommand(), new WhisperTextCommand(),
                        new HistoryCommand(), new DmCommand());

        this.client = builder.build();

        this.jda = JDABuilder.create(discordBotKey,
                GUILD_MEMBERS, GUILD_VOICE_STATES, GUILD_MESSAGES,
                GUILD_MESSAGE_REACTIONS, GUILD_PRESENCES, GUILD_EMOJIS, DIRECT_MESSAGES).addEventListeners(client,
                new VoiceChannelEventListener(botConfiguration)).build();
    }

    public void shutdownBot()
    {
        this.jda.shutdown();
    }

    public JDA getJda()
    {
        return jda;
    }

    public AudioPlayerManager getAudioPlayerManager()
    {
        return playerManager;
    }

    public CommandClient getClient()
    {
        return client;
    }

    public Command getCommandWithName(String name)
    {
        return client.getCommandWithName(name);
    }

    public CommandEvent createCommandEvent(TriggerCommandDto triggerCommandDto) throws IllegalArgumentException
    {
        User user = jda.getUserById(triggerCommandDto.getAuthorId());
        if (user == null)
        {
            throw new IllegalArgumentException("user is null");
        }

        TextChannel textChannel = jda.getTextChannelById(triggerCommandDto.getTextChannelId());
        MessageChannel messageChannel = null; // unsupported
        PrivateChannel privateChannel = null; // unsupported
        Message apiMessage = new ApiMessage("-" + triggerCommandDto.getCommandName() + " " + triggerCommandDto.getCommandArgs());

        Guild guild = jda.getGuildById(triggerCommandDto.getGuildId());
        if (guild == null)
        {
            throw new IllegalArgumentException("guild is null");
        }

        Member member = guild.getMember(user);
        if (member == null)
        {
            throw new IllegalArgumentException("member is null");
        }

        return new ApiCommandEvent(user, textChannel, messageChannel, privateChannel,
                apiMessage, member, jda, guild, ChannelType.TEXT,
                triggerCommandDto.getCommandArgs(), client);
    }
}
