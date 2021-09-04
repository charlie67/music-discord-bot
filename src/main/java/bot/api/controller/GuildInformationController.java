package bot.api.controller;

import bot.api.dto.GuildDto;
import bot.api.dto.MemberDto;
import bot.api.dto.TextChannelDto;
import bot.service.BotService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/guilds")
@RestController
public class GuildInformationController
{
    private static final Logger LOGGER = LogManager.getLogger(GuildInformationController.class);
    private final BotService botService;

    @Autowired
    public GuildInformationController(BotService botService)
    {
        this.botService = botService;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping()
    public List<GuildDto> getGuildsForBot()
    {
        List<Guild> guilds = botService.getJda().getGuilds();
        List<GuildDto> guildDtoList = new ArrayList<>(guilds.size());

        for (Guild guild : guilds)
        {
            GuildDto guildDto = new GuildDto(guild.getName(), guild.getId());
            guildDtoList.add(guildDto);
        }
        return guildDtoList;
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberDto>> getAuthorsForGuild(@PathVariable Long id)
    {
        Guild guild = botService.getJda().getGuildById(id);

        if (guild == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Member> members = guild.getMembers();
        List<MemberDto> memberDtoList = new ArrayList<>(members.size());


        for (Member member : members)
        {
            boolean inVoice = true;

            GuildVoiceState memberVoiceState = member.getVoiceState();

            if (memberVoiceState == null || !memberVoiceState.inVoiceChannel())
            {
                inVoice = false;
            }

            MemberDto memberDto = new MemberDto(member.getUser().getName(), member.getId(), String.valueOf(inVoice));
            memberDtoList.add(memberDto);
        }

        return ResponseEntity.ok(memberDtoList);
    }

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/{id}/textChannels")
    public ResponseEntity<List<TextChannelDto>> getTextChannelsForGuild(@PathVariable Long id)
    {
        Guild guild = botService.getJda().getGuildById(id);

        if (guild == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<TextChannel> textChannels = guild.getTextChannels();
        List<TextChannelDto> textChannelDtoList = new ArrayList<>(textChannels.size());

        for (TextChannel textChannel : textChannels)
        {
            // no point adding text channels that can't be used
            if (!textChannel.canTalk())
            {
                continue;
            }

            TextChannelDto textChannelDto = new TextChannelDto(textChannel.getName(), textChannel.getId());
            textChannelDtoList.add(textChannelDto);
        }

        return ResponseEntity.ok(textChannelDtoList);
    }
}
