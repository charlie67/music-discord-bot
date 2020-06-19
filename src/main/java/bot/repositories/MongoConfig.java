package bot.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration
{

    @Override
    public @NotNull MongoClient mongoClient()
    {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Override
    protected @NotNull String getDatabaseName()
    {
        return "discord";
    }
}
