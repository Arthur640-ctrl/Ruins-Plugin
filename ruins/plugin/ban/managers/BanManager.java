package fr.ruins.plugin.ban.managers;

import com.mongodb.client.MongoCollection;
import fr.ruins.plugin.ban.utils.BanEntry;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

import java.util.HashMap;
import java.util.Map;

public class BanManager {

    private final MongoCollection<Document> bans;
    private final Map<String, BanEntry> cache = new HashMap<>();

    public BanManager(MongoCollection bans) {
        this.bans = bans;
        loadBans();
    }

    private void loadBans() {
        for (Document doc : bans.find()) {
            BanEntry entry = new BanEntry(
                    doc.getString("uuid"),
                    doc.getString("playerName"),
                    doc.getString("reason"),
                    doc.getLong("endTime"),
                    doc.getString("staff")
            );
            if (!entry.isExpired()) cache.put(entry.getUuid(), entry);
        }
    }

    public void addBan(BanEntry ban) {
        cache.put(ban.getUuid(), ban);
        Document doc = new Document("uuid", ban.getUuid())
                .append("playerName", ban.getPlayerName())
                .append("reason", ban.getReason())
                .append("endTime", ban.getEndTime())
                .append("staff", ban.getStaff());
        bans.insertOne(doc);
    }

    public void removeBan(String uuid) {
        cache.remove(uuid);
        bans.deleteOne(eq("uuid", uuid));
    }

    public BanEntry getBan(String uuid) {
        BanEntry ban = cache.get(uuid);
        if (ban != null && ban.isExpired()) {
            removeBan(uuid);
            return null;
        }
        return ban;
    }
}