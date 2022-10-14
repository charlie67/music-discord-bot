package bot.listeners;

import bot.dao.OptionEntityDao;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GuildJoinEventListener extends ListenerAdapter {

  private final OptionEntityDao optionEntityDao;

  @Override
  public void onGuildJoin(@NotNull GuildJoinEvent event) {
    optionEntityDao.setupDefaultOptions(event.getGuild().getId());
  }
}
