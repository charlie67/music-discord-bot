package bot.Entities;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "alias")
public class AliasEntity
{
    @Field
    public String aliasName;

    @Field
    public String aliasCommandArguments;

    @Field
    public String commandName;

    public AliasEntity()
    {
    }

    public AliasEntity(String aliasName, String aliasCommandArguments, String commandName)
    {
        this.aliasName = aliasName;
        this.aliasCommandArguments = aliasCommandArguments;
        this.commandName = commandName;
    }

    public String getAliasName()
    {
        return aliasName;
    }

    public void setAliasName(String aliasName)
    {
        this.aliasName = aliasName;
    }

    public String getAliasCommandArguments()
    {
        return aliasCommandArguments;
    }

    public void setAliasCommandArguments(String aliasCommandArguments)
    {
        this.aliasCommandArguments = aliasCommandArguments;
    }

    public String getCommandName()
    {
        return commandName;
    }

    public void setCommandName(String commandName)
    {
        this.commandName = commandName;
    }
}
