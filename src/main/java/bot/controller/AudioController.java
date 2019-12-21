package bot.controller;

import bot.commands.audio.utils.VoiceChannelUtils;
import bot.service.BotService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/play")
    public ResponseEntity<String> addNewSong(@RequestParam boolean top, @RequestParam String argument,
                                             @RequestParam String guildId, @RequestParam String textChannelId,
                                             @RequestParam String memberId)
    {
        try
        {
            VoiceChannelUtils.SearchAndPlaySong(botService.getJda(), argument, guildId, textChannelId, memberId, top, botService.getAudioPlayerManager());
        } catch (IllegalArgumentException e)
        {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
