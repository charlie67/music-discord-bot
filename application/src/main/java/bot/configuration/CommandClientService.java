package bot.configuration;

import bot.commands.alias.AliasCreateCommand;
import bot.commands.alias.AliasDeleteCommand;
import bot.commands.alias.AliasListCommand;
import bot.commands.alias.AliasSearchCommand;
import bot.commands.audio.*;
import bot.commands.image.RedditSearchCommand;
import bot.commands.text.DmCommand;
import bot.commands.text.EchoTextCommand;
import bot.commands.text.WhisperTextCommand;
import bot.commands.utilities.OptionListCommand;
import bot.commands.utilities.OptionsCommand;
import bot.dao.OptionEntityDao;
import bot.repositories.AliasEntityRepository;
import bot.utils.command.Command;
import bot.utils.command.CommandClient;
import bot.utils.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class CommandClientService {

  private final BotConfiguration botConfiguration;
  private final AliasEntityRepository aliasEntityRepository;
  private final AliasCreateCommand aliasCreateCommand;
  private final AliasDeleteCommand aliasDeleteCommand;
  private final AliasListCommand aliasListCommand;
  private final AliasSearchCommand aliasSearchCommand;
  private final OptionsCommand optionsCommand;
  private final OptionListCommand optionListCommand;
  private final OptionEntityDao optionEntityDao;

  private final JoinCommand joinCommand;

  private final PlayCommand playCommand;

  private final PlayTopCommand playTopCommand;

  private final LeaveCommand leaveCommand;

  private final NowPlayingCommand nowPlayingCommand;

  private final SkipSongCommand skipSongCommand;

  private final ClearQueueCommand clearQueueCommand;

  private final SkipToCommand skipToCommand;

  private final PauseCommand pauseCommand;

  private final ResumeCommand resumeCommand;

  private final LoopCommand loopCommand;
  private final EventWaiter eventWaiter = new EventWaiter();
  private CommandClient commandClient;

  @PostConstruct
  public void createCommandClient() {
    CommandClientBuilder builder = new CommandClientBuilder();

    builder
        .setPrefix(botConfiguration.getCommandPrefix())
        .setActivity(Activity.competing("ur mum")) // todo set from option entity
        .setOwnerId(botConfiguration.getOwnerId())
        .addCommands(
            new RedditSearchCommand(),
            playCommand,
            playTopCommand,
            nowPlayingCommand,
            clearQueueCommand,
            new QueueCommand(eventWaiter))
        .addCommands(
            leaveCommand,
            joinCommand,
            skipSongCommand,
            new RemoveCommand(),
            new SeekCommand(),
            new ShuffleCommand(),
            skipToCommand,
            pauseCommand,
            resumeCommand,
            loopCommand,
            aliasListCommand,
            aliasDeleteCommand,
            aliasSearchCommand,
            new EchoTextCommand(),
            new WhisperTextCommand(),
            new HistoryCommand(eventWaiter),
            new DmCommand(),
            optionsCommand,
            optionListCommand)
        .setEmojis("U+1F44D", "U+26A0", "U+274C");

    this.commandClient = builder.build();
  }

  public Optional<Command> getCommandWithName(String name) {
    return commandClient.getCommandWithName(name);
  }
}
