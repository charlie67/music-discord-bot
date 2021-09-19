package bot.repositories;

import bot.entities.OptionEntity;
import org.springframework.data.repository.CrudRepository;

public interface OptionEntityRepository extends CrudRepository<OptionEntity, String>
{
    String AUTOPLAY_NAME = "autoplay";
    String AUTOPLAY_HELP = "stops autoplaying music when the current track has finished.";

    OptionEntity findByServerIdAndName(String serverId, String name);
}
