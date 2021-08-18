package bot.repositories;


import bot.Entities.AliasEntity;
import org.springframework.data.jpa.repository.Query;
import bot.entities.AliasEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface AliasEntityRepository extends CrudRepository<AliasEntity, String>
{
    Set<AliasEntity> findAllByServerId(String serverId);

    AliasEntity findByServerIdAndName(String serverId, String name);

    @Query(value = "SELECT * FROM alias_entity a WHERE a.server_id = :server_id AND a.name LIKE %:searchTerm%",
            nativeQuery = true)
    Set<AliasEntity> findAliasEntityByNameContainingAndServerId(@Param("server_id") String serverId,
                                                                @Param("searchTerm") String searchTerm);
}