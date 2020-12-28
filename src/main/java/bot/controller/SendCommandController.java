package bot.controller;

import bot.service.BotService;
import com.jagrosh.jdautilities.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/command")
@RestController
public class SendCommandController
{
    private final BotService botService;

    @Autowired
    public SendCommandController(BotService botService)
    {
        this.botService = botService;
    }


    @PostMapping()
    public ResponseEntity<String> sendCommand(@RequestParam String commandName, @RequestParam String commandArgs,
                                              @RequestParam String guildId, @RequestParam String textChannelId,
                                              @RequestParam String userId)
    {
        Command command = botService.getCommandFromName(commandName);

        if (command == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

//        command.run(new CommandEvent(new MessageReceivedEvent(botService.getJda())));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
