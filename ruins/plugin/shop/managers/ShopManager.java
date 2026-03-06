package fr.ruins.plugin.shop.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import fr.ruins.plugin.player.utils.PlayerData;
import fr.ruins.plugin.shop.utils.ShopItem;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopManager {

    private final MongoCollection<Document> shop_collection;
    private final PlayerData playerData;
    public Boolean shop_open = true;

    public ShopManager(MongoCollection<Document> shopCollection, PlayerData playerData) {
        this.shop_collection = shopCollection;
        this.playerData = playerData;
    }

    // Crée une annonce
    public void createListing(UUID owner, ItemStack item, double price, int amount) {
        ShopItem shopItem = ShopItem.create(owner, price, amount, item);
        shop_collection.insertOne(shopItem.toDocument());
    }

    // SUpprime une annonce
    public boolean removeListing(String id, UUID requester) {
        Document doc = shop_collection.find(Filters.eq("_id", id)).first();
        if (doc == null) return false;
        ShopItem s = ShopItem.fromDocument(doc);
        if (!s.getOwner().equals(requester)) {
            // on refuse si le requesteur n'est pas propriétaire (pas d'admin check ici)
            return false;
        }
        shop_collection.deleteOne(Filters.eq("_id", id));
        return true;
    }

    // Recupere une annonce
    public ShopItem getListing(String id) {
        Document doc = shop_collection.find(Filters.eq("_id", id)).first();
        if (doc == null) return null;
        return ShopItem.fromDocument(doc);
    }

    // Retourne une page d'annonces (page commençant à 1).
    public List<ShopItem> listListings(int page, int pageSize) {
        List<ShopItem> res = new ArrayList<>();
        int skip = (page - 1) * pageSize;
        for (Document doc : shop_collection.find().sort(Sorts.descending("createdAt")).skip(skip).limit(pageSize)) {
            res.add(ShopItem.fromDocument(doc));
        }
        return res;
    }

    // Achat synchrone (appelé depuis le thread du serveur). Retourne vrai si l'achat a réussi.
    public boolean buyListing(String id, UUID buyerUUID) {
        Document doc = shop_collection.find(Filters.eq("_id", id)).first();
        if (doc == null) return false;

        ShopItem s = ShopItem.fromDocument(doc);
        if (s.getOwner().equals(buyerUUID)) return false; // ne pas acheter sa propre annonce

        double price = s.getPrice();
        int bal = playerData.getPlayerMoney(buyerUUID); // utilise PlayerData

        if (bal < price) return false; // pas assez d'argent

        // Retirer argent acheteur et verser au vendeur
        playerData.removePlayerMoney(buyerUUID, (int) price);
        playerData.addPlayerMoney(s.getOwner(), (int) price);

        // Donner l'item à l'acheteur (si inventaire plein, le drop)
        Player buyer = Bukkit.getPlayer(buyerUUID);
        ItemStack item = s.getItem();
        if (buyer != null && buyer.isOnline()) {
            buyer.getInventory().addItem(item);
        } else {
            // si joueur offline, drop dans le monde du spawn
            Bukkit.getWorlds().get(0).dropItemNaturally(Bukkit.getWorlds().get(0).getSpawnLocation(), item);
        }

        // Supprimer l'annonce
        shop_collection.deleteOne(Filters.eq("_id", id));
        return true;
    }

    // Return true ou false en fonction si le shop est ouvert ou pas
    public boolean shopIsOpen() {
        return shop_open;
    }

    public void setShopOpen(Boolean open) {
        shop_open = open;
    }
}
