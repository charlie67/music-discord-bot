package bot.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration
{
    @Value("${MONGO_LOCATION:mongodb://localhost:27017}")
    private String MONGO_LOCATION;

    @Override
    public @NotNull MongoClient mongoClient()
    {
        return MongoClients.create(MONGO_LOCATION);
    }

    @Override
    protected @NotNull String getDatabaseName()
    {
        return "discord";
    }
}
