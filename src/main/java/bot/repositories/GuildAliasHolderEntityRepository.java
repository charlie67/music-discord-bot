package bot.repositories;

import bot.Entities.GuildAliasHolderEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GuildAliasHolderEntityRepository extends MongoRepository<GuildAliasHolderEntity, String>
{
    @NotNull Optional<GuildAliasHolderEntity> findByGuildId(@NotNull String guildId);
}
