package fr.ruins.plugin.shop.utils;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public class ShopItem {

    private final String id; // uuid string
    private final UUID owner;
    private final double price;
    private final int amount;
    private final ItemStack item;
    private final Date createdAt;
    private final String sellerName;

    public ShopItem(UUID owner, String sellerName, double price, int amount, ItemStack item, Date createdAt) {
        this.id = generateId();
        this.owner = owner;
        this.price = price;
        this.amount = amount;
        this.item = item;
        this.createdAt = createdAt;
        this.sellerName = sellerName;
    }

    public String getId() { return id; }
    public UUID getOwner() { return owner; }
    public double getPrice() { return price; }
    public int getAmount() { return amount; }
    public ItemStack getItem() { return item; }
    public Date getCreatedAt() { return createdAt; }
    public String getSellerName() { return sellerName; }

    private static String generateId() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public Document toDocument() {
        Document doc = new Document("_id", id)
            .append("ownerUUID", owner.toString())
            .append("sellerName", sellerName)
            .append("price", price)
            .append("amount", amount)
            .append("item", ShopUtils.itemToBase64(item))
            .append("createdAt", createdAt);
        return doc;
    }

    public static ShopItem fromDocument(Document doc) {
        String id = doc.getString("_id");

        String ownerUUIDStr = doc.getString("ownerUUID");
        UUID owner = null;
        if (ownerUUIDStr != null && !ownerUUIDStr.isEmpty()) {
            owner = UUID.fromString(ownerUUIDStr);
        }

        String sellerName = doc.getString("sellerName");
        if (sellerName == null) sellerName = "Inconnu";

        double price = doc.getDouble("price");
        int amount = doc.getInteger("amount", 1);
        ItemStack item = ShopUtils.base64ToItem(doc.getString("item"));
        Date createdAt = doc.getDate("createdAt");

        return new ShopItem(owner, sellerName, price, amount, item, createdAt);
    }

    public static ShopItem create(UUID owner, double price, int amount, ItemStack item) {

        OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
        String playerName = player.getName() != null ? player.getName() : "Inconnu";

        return new ShopItem(owner, playerName, price, amount, item, new Date());
    }
}

