package bot.listeners;

import bot.dao.OptionEntityDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuildJoinEventListener extends ListenerAdapter {

  private final OptionEntityDao optionEntityDao;

  @Override
  public void onGuildJoin(@NotNull GuildJoinEvent event) {
    log.info("Just joined server {} - {}", event.getGuild().getName(), event.getGuild().getId());
    optionEntityDao.setupDefaultOptions(event.getGuild().getId());
  }
}
