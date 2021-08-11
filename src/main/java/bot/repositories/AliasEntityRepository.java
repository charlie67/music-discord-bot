package bot.repositories;


import bot.Entities.AliasEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AliasEntityRepository extends CrudRepository<AliasEntity, String>
{
    Set<AliasEntity> findAllByServerId(String serverId);

    AliasEntity findByServerIdAndName(String serverId, String name);

    @Query(name = "SELECT * FROM AliasEntity a WHERE a.serverId = :serverId AND a.name LIKE %:name%", nativeQuery = true)
    Set<AliasEntity> findAliasEntityByNameContainingAndServerId(String serverId, String name);
}
