package bot.api.controller;

import bot.api.dto.TriggerCommandDto;
import bot.configuration.CommandClientService;
import bot.service.BotService;
import bot.utils.command.Command;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/command")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommandTriggerController {

  private final CommandClientService commandClientService;

  private final BotService botService;

  @PostMapping()
  public ResponseEntity<String> triggerCommand(@RequestBody TriggerCommandDto triggerCommandDto) {
    Optional<Command> commandOptional =
        commandClientService.getCommandWithName(triggerCommandDto.getCommandName());

    if (commandOptional.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    Command command = commandOptional.get();

    // try setting it to the bot because it probably isn't needed
    if (StringUtils.isEmpty(triggerCommandDto.getAuthorId())) {
      triggerCommandDto.setAuthorId(botService.getJda().getSelfUser().getId());
    }

    if (!allRequiredVariablesPresent(triggerCommandDto)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //		CommandEvent apiCommandEvent;
    //		try {
    //			apiCommandEvent = botService.createCommandEvent(triggerCommandDto);
    //		} catch (IllegalArgumentException e) {
    //			log.error("Error when creating command event", e);
    //			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //		}
    //		command.run(apiCommandEvent);
    //		log.info(
    //						String.format(
    //										"Ran command %s with arguments %s in server %s",
    //										triggerCommandDto.getCommandName(),
    //										triggerCommandDto.getCommandArgs(),
    //										triggerCommandDto.getGuildId()));

    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  private boolean allRequiredVariablesPresent(TriggerCommandDto triggerCommandDto) {
    if (!StringUtils.hasText(triggerCommandDto.getCommandName())) {
      return false;
    } else if (!StringUtils.hasText(triggerCommandDto.getAuthorId())) {
      return false;
    } else if (!StringUtils.hasText(triggerCommandDto.getGuildId())) {
      return false;
    } else {
      return StringUtils.hasText(triggerCommandDto.getTextChannelId());
    }
  }
}
