package me.hostadam.duels.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.hostadam.duels.DuelsPlugin;
import org.bson.Document;

@Getter
public class DuelMongo {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public DuelMongo(DuelsPlugin duelsPlugin) {
        ConnectionString string = new ConnectionString(duelsPlugin.getDatabaseConfig().getString("database.connection-string"));
        MongoClientSettings.Builder settings = MongoClientSettings.builder().applyConnectionString(string);

        try {
            this.client = MongoClients.create(settings.build());
            this.database = this.client.getDatabase(string.getDatabase());
            this.collection = this.database.getCollection(string.getCollection());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
