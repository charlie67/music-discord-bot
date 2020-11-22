package bot.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "guildAliasHolders")
public class GuildAliasHolderEntity
{
    @Id
    public String guildId;

    @Field
    public List<AliasEntity> aliasEntityList;

    public GuildAliasHolderEntity()
    {
        this(null, new ArrayList<>());
    }

    public GuildAliasHolderEntity(String guildId, List<AliasEntity> aliasEntityList)
    {
        this.guildId = guildId;
        this.aliasEntityList = aliasEntityList;
    }

    public void addNewAliasEntity(AliasEntity aliasEntity)
    {
        aliasEntityList.add(aliasEntity);
    }

    public String getGuildId()
    {
        return guildId;
    }

    public void setGuildId(String guildId)
    {
        this.guildId = guildId;
    }

    public void removeAliasWithName(String aliasCommandName) throws IllegalArgumentException
    {
        AliasEntity aliasToRemove = null;

        for (AliasEntity aliasEntity : aliasEntityList)
        {
            if (Objects.equals(aliasEntity.getAliasName(), aliasCommandName))
            {
                aliasToRemove = aliasEntity;
            }
        }

        if (aliasToRemove == null)
        {
            throw new IllegalArgumentException(String.format("%s is not an alias that can be removed", aliasCommandName));
        }

        aliasEntityList.remove(aliasToRemove);
    }

    public List<AliasEntity> getAliasEntityList()
    {
        return aliasEntityList;
    }
}
