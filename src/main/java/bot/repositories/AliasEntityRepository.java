package bot.repositories;


import bot.Entities.AliasEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AliasEntityRepository extends CrudRepository<AliasEntity, String>
{
    Set<AliasEntity> findAllByServerId(String serverId);

    AliasEntity findByServerIdAndName(String serverId, String name);

    @Query("SELECT * FROM AliasEntity a WHERE a.serverId = :serverID AND a.name LIKE %:name%")
    Set<AliasEntity> findAliasEntityByNameContainingAndServerId(String serverId, String name);
}
