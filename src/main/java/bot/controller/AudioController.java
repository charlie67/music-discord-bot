package bot.controller;

import bot.commands.audio.utils.VoiceChannelUtils;
import bot.service.BotService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/audio")
@RestController
public class AudioController
{
    private final BotService botService;
    private Logger LOGGER = LogManager.getLogger(AudioController.class);

    public AudioController(BotService botService)
    {
        this.botService = botService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test()
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/play")
    public ResponseEntity<String> addNewSong(@RequestParam String argument, @RequestParam String guildId, @RequestParam String textChannelId, @RequestParam String memberId)
    {
        try
        {
            VoiceChannelUtils.SearchAndPlaySong(botService.getJda(), argument, guildId, textChannelId, memberId, false, botService.getAudioPlayerManager());
        } catch (IllegalArgumentException e)
        {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
