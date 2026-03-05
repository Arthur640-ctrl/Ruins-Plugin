package fr.ruins.plugin.auth.managers;

import com.mongodb.client.MongoCollection;

import fr.ruins.plugin.auth.utils.PasswordHasher;
import org.bson.Document;

import java.util.UUID;

public class AuthManager {

    private final MongoCollection<Document> player_collection;


    public AuthManager(MongoCollection<Document> playerCollection) {
        player_collection = playerCollection;
    }

    public boolean isRegistered(UUID uuid) {
        return player_collection.find(new Document("uuid", uuid.toString())).first() != null;
    }

    public void register(UUID uuid, String name, String password) {

        String hash = PasswordHasher.hash(password);

        Document doc = new Document()
                .append("uuid", uuid.toString())
                .append("name", name)
                .append("password", hash)
                .append("score", 0)
                .append("money", 0)
                .append("deaths", 0)
                .append("kills", 0)
                .append("login", true)
                .append("createdAt", System.currentTimeMillis());

        player_collection.insertOne(doc);
    }

    public boolean login(UUID uuid, String password) {

        Document doc = player_collection.find(new Document("uuid", uuid.toString())).first();
        if (doc == null) return false;

        String storedHash = doc.getString("password");
        String inputHash = PasswordHasher.hash(password);

        if (storedHash.equals(inputHash)) {

            player_collection.updateOne(
                    new Document("uuid", uuid.toString()),
                    new Document("$set", new Document("login", true))
            );

            return true;
        }

        return false;
    }

    public void setLogin(UUID uuid, boolean status) {

        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("login", status))
        );
    }
}
