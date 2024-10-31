package bot.service;

import bot.utils.command.Command;
import bot.utils.command.CommandClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class CommandClientService {

	private final CommandClient commandClient;

	public Optional<Command> getCommandWithName(String name) {
		return commandClient.getCommandWithName(name);
	}
}
