package fr.ruins.plugin.player;

import com.mongodb.client.MongoCollection;
import fr.ruins.plugin.database.MongoManager;
import org.bson.Document;

import java.util.UUID;

public class PlayerData {

    private final MongoCollection<Document> player_collection;

    public PlayerData(MongoCollection<Document> playerCollection) {
        player_collection = playerCollection;
    }

    // SCORE
    public int getPlayerScore(UUID uuid) {

        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        if (found != null) {
            return found.getInteger("score");
        }

        return 0;
    }

    public void addPlayerScore(UUID uuid, int amount) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$inc", new Document("score", amount))
        );
    }

    public void removePlayerScore(UUID uuid, int amount) {

        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        int current = 0;

        if (found != null && found.containsKey("score")) {
            current = found.getInteger("score");
        }

        int newValue = Math.max(0, current - amount);

        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("score", newValue))
        );
    }

    public void setPlayerScore(UUID uuid, int value) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("score", value)));
    }

    // MONEY
    public int getPlayerMoney(UUID uuid) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        if (found != null) {
            return found.getInteger("money");
        }

        return 0;
    }

    public void addPlayerMoney(UUID uuid, int amount) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$inc", new Document("money", amount))
        );
    }

    public void removePlayerMoney(UUID uuid, int amount) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        int current = 0;

        if (found != null && found.containsKey("money")) {
            current = found.getInteger("money");
        }

        int newValue = Math.max(0, current - amount);

        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("money", newValue))
        );
    }

    public void setPlayerMoney(UUID uuid, int value) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("money", value)));
    }

    // DEATHS
    public int getPlayerDeaths(UUID uuid) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        if (found != null) {
            return found.getInteger("deaths");
        }

        return 0;
    }

    public void addPlayerDeaths(UUID uuid, int amount) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$inc", new Document("deaths", amount))
        );
    }

    public void removePlayerDeaths(UUID uuid, int amount) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        int current = 0;

        if (found != null && found.containsKey("deaths")) {
            current = found.getInteger("deaths");
        }

        int newValue = Math.max(0, current - amount);

        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("deaths", newValue))
        );
    }

    public void setPlayerDeaths(UUID uuid, int value) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("deaths", value)));
    }

    // KILLS
    public int getPlayerKills(UUID uuid) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        if (found != null) {
            return found.getInteger("kills");
        }

        return 0;
    }

    public void addPlayerKills(UUID uuid, int amount) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$inc", new Document("kills", amount))
        );
    }

    public void removePlayerKills(UUID uuid, int amount) {
        Document found = player_collection.find(new Document("uuid", uuid.toString())).first();

        int current = 0;

        if (found != null && found.containsKey("kills")) {
            current = found.getInteger("kills");
        }

        int newValue = Math.max(0, current - amount);

        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("kills", newValue))
        );
    }

    public void setPlayerKills(UUID uuid, int value) {
        player_collection.updateOne(
                new Document("uuid", uuid.toString()),
                new Document("$set", new Document("kills", value)));
    }

}
