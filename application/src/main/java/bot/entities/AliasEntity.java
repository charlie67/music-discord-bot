package bot.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AliasEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String serverId;

    private String name;

    private String command;

    private String args;

    public AliasEntity()
    {
        // no args constructor
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getServerId()
    {
        return serverId;
    }

    public AliasEntity setServerId(String serverId)
    {
        this.serverId = serverId;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public AliasEntity setName(String name)
    {
        this.name = name;
        return this;
    }

    public String getCommand()
    {
        return command;
    }

    public AliasEntity setCommand(String command)
    {
        this.command = command;
        return this;
    }

    public String getArgs()
    {
        return args;
    }

    public AliasEntity setArgs(String args)
    {
        this.args = args;
        return this;
    }
}
