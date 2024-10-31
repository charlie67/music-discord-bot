package bot.configuration;

import bot.utils.command.Command;
import bot.utils.command.CommandClient;
import bot.utils.command.CommandClientBuilder;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CommandClientConfig {
	private final BotConfiguration botConfiguration;
	private final List<Command> commands;

	@Bean
	public CommandClient commandClient() {
		CommandClientBuilder builder = new CommandClientBuilder();

		builder.setPrefix(botConfiguration.getCommandPrefix())
						.setActivity(Activity.customStatus("Vibing")) // todo set from option entity
						.setOwnerId(botConfiguration.getOwnerId())
						.addCommands(commands.toArray(new Command[0]))
						.setEmojis("\u1F44D", "\u26A0", "\u274C");

		return builder.build();
	}
}
