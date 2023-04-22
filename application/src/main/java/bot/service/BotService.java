package bot.service;

import static net.dv8tion.jda.api.requests.GatewayIntent.DIRECT_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_EMOJIS_AND_STICKERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MEMBERS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGE_REACTIONS;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_PRESENCES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_VOICE_STATES;
import static net.dv8tion.jda.api.requests.GatewayIntent.MESSAGE_CONTENT;

import bot.configuration.BotConfiguration;
import bot.configuration.CommandClientService;
import bot.listeners.VoiceChannelEventListener;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {

  private final BotConfiguration botConfiguration;

  private final CommandClientService commandClientService;

  private String discordBotKey;

  private JDA jda;

  @PostConstruct
  public void setup() {
    this.discordBotKey = botConfiguration.getDiscordKey();
  }

  public void startBot() throws LoginException {

    this.jda =
        JDABuilder.create(
                discordBotKey,
                GUILD_MEMBERS,
                GUILD_VOICE_STATES,
                GUILD_MESSAGES,
                GUILD_MESSAGE_REACTIONS,
                GUILD_PRESENCES,
                GUILD_EMOJIS_AND_STICKERS,
                DIRECT_MESSAGES,
                MESSAGE_CONTENT)
            .addEventListeners(
                commandClientService.getCommandClient(),
                new VoiceChannelEventListener(botConfiguration))
            .build();
  }

  public void shutdownBot() {
    this.jda.shutdown();
  }

  public JDA getJda() {
    return jda;
  }

  //  public CommandEvent createCommandEvent(TriggerCommandDto triggerCommandDto)
  //      throws IllegalArgumentException {
  //    User user = jda.getUserById(triggerCommandDto.getAuthorId());
  //    if (user == null) {
  //      throw new IllegalArgumentException("user is null");
  //    }
  //
  //    TextChannel textChannel;
  //    if (triggerCommandDto.isSilent()) {
  //      textChannel = new ApiTextChannel();
  //    } else {
  //      textChannel = jda.getTextChannelById(triggerCommandDto.getTextChannelId());
  //    }
  //    MessageChannel messageChannel = null; // unsupported
  //    PrivateChannel privateChannel = null; // unsupported
  //    Message apiMessage =
  //        new ApiMessage(
  //            "-" + triggerCommandDto.getCommandName() + " " +
  // triggerCommandDto.getCommandArgs());
  //
  //    Guild guild = jda.getGuildById(triggerCommandDto.getGuildId());
  //    if (guild == null) {
  //      throw new IllegalArgumentException("guild is null");
  //    }
  //
  //    Member member = guild.getMember(user);
  //    if (member == null) {
  //      throw new IllegalArgumentException("member is null");
  //    }
  //
  //    return new ApiCommandEvent(
  //        user,
  //        textChannel,
  //        messageChannel,
  //        privateChannel,
  //        apiMessage,
  //        member,
  //        jda,
  //        guild,
  //        ChannelType.TEXT,
  //        triggerCommandDto.getCommandArgs(),
  //        commandClientService.getCommandClient());
  //  }
}
