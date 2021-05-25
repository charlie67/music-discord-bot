package bot.api.controller;

import bot.api.dto.TriggerCommandDto;
import bot.service.BotService;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/command")
@RestController
public class CommandTriggerController
{
    private final BotService botService;

    @Autowired
    public CommandTriggerController(BotService botService)
    {
        this.botService = botService;
    }


    @PostMapping()
    public ResponseEntity<String> triggerCommand(@RequestBody TriggerCommandDto triggerCommandDto)
    {
        Command command = botService.getCommandWithName(triggerCommandDto.getCommandName());

        if (!allRequiredVariablesPresent(triggerCommandDto) || command == null)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CommandEvent apiCommandEvent;
        try
        {
                apiCommandEvent = botService.createCommandEvent(triggerCommandDto);
        } catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        command.run(apiCommandEvent);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    private boolean allRequiredVariablesPresent(TriggerCommandDto triggerCommandDto)
    {
        if (triggerCommandDto.getCommandArgs() == null)
        {
            return false;
        }
        else if (triggerCommandDto.getCommandName() == null)
        {
            return false;
        }
        else if (triggerCommandDto.getAuthorId() == null)
        {
            return false;
        }
        else if (triggerCommandDto.getGuildId() == null)
        {
            return false;
        }
        else return triggerCommandDto.getTextChannelId() != null;
    }
}
