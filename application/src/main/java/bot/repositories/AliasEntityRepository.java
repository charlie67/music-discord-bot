package bot.repositories;

import bot.entities.AliasEntity;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AliasEntityRepository extends CrudRepository<AliasEntity, String> {
  Set<AliasEntity> findAllByServerId(String serverId);

  AliasEntity findByServerIdAndName(String serverId, String name);

  @Query(
      value =
          "SELECT * FROM alias_entity a WHERE a.server_id = :server_id AND a.name LIKE %:searchTerm%",
      nativeQuery = true)
  Set<AliasEntity> findAliasEntityByNameContainingAndServerId(
      @Param("server_id") String serverId, @Param("searchTerm") String searchTerm);
}
