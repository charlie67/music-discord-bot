package bot.dao;

import static bot.utils.OptionsCommands.AUTOPLAY_NAME;

import bot.entities.OptionEntity;
import bot.repositories.OptionEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OptionEntityDao {

  private final OptionEntityRepository optionEntityRepository;

  public void setupDefaultOptions(String guildID) {
    setupDefaultAutoplay(guildID);
  }

  private void setupDefaultAutoplay(String guildID) {
    OptionEntity optionEntity = new OptionEntity();
    optionEntity.setOption(false);
    optionEntity.setServerId(guildID);
    optionEntity.setName(AUTOPLAY_NAME);

    optionEntityRepository.save(optionEntity);
  }

  public boolean autoplayEnabledForGuild(String guildId) {
    OptionEntity optionEntity =
        optionEntityRepository.findByServerIdAndName(guildId, AUTOPLAY_NAME);

    if (optionEntity == null) {
      log.error("Option entity is null for guild {}. Creating default AUTOPLAY option", guildId);
      setupDefaultAutoplay(guildId);
      return false;
    }

    return optionEntity.getOption();
  }
}
