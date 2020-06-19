package bot.repositories;


import bot.Entities.AliasEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AliasEntityRepository extends MongoRepository<AliasEntity, String>
{
}
