package bot.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "guildAliasHolders")
public class GuildAliasHolderEntity
{
    @Id
    public String guildId;

    @Field
    public List<AliasEntity> aliasEntityList;

    public GuildAliasHolderEntity()
    {
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

    public List<AliasEntity> getAliasEntityList()
    {
        return aliasEntityList;
    }

    public void setAliasEntityList(List<AliasEntity> aliasEntityList)
    {
        this.aliasEntityList = aliasEntityList;
    }
}
