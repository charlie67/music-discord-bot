package bot.commands.alias;

import bot.utils.command.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AliasHelperService {
	private final List<Command> allCommands;

	protected boolean isCommandNamePresent(final String commandName) {
		return allCommands
						.stream()
						.flatMap(command -> command.getAllNames().stream())
						.anyMatch(name -> name.equals(commandName));
	}

	public Optional<Command> getCommandWithName(final String commandName) {
		return allCommands.stream().filter(command -> command.getAllNames().contains(commandName)).findFirst();
	}
}
