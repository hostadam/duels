package me.hostadam.duels.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.hostadam.duels.DuelsPlugin;
import org.bson.Document;

import java.util.Arrays;

@Getter
public class DuelMongo {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public DuelMongo(DuelsPlugin duelsPlugin) {
        final String host = duelsPlugin.getDatabaseConfig().getString("database.host"),
                database = duelsPlugin.getDatabaseConfig().getString("database.database");
        final int port = duelsPlugin.getDatabaseConfig().getInt("database.port");

        final boolean authEnabled = duelsPlugin.getDatabaseConfig().getBoolean("database.auth.enabled");

        if(authEnabled) {
            final String authUsername = duelsPlugin.getDatabaseConfig().getString("database.auth.username"),
                    authDatabase = duelsPlugin.getDatabaseConfig().getString("database.auth.database"),
                    authPassword = duelsPlugin.getDatabaseConfig().getString("database.auth.password");

            this.client = new MongoClient(new ServerAddress(host, port), Arrays.asList(MongoCredential.createCredential(authUsername, authDatabase, authPassword.toCharArray())));
        } else {
            this.client = new MongoClient(new ServerAddress(host, port));
        }

        this.database = this.client.getDatabase(database);
    }


}
