package fr.ruins.plugin.shop.gui;

import fr.ruins.plugin.Main;
import fr.ruins.plugin.shop.utils.ShopItem;
import fr.ruins.plugin.shop.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final ShopManager manager;
    private final int pageSize = 27; // personnalisable
    private final NamespacedKey key;

    public ShopGUI(ShopManager manager) {
        this.manager = manager;
        this.key = new NamespacedKey(Main.getInstance(), "shop_id");
    }

    public void openShop(Player p, int page) {
        List<ShopItem> listings = manager.listListings(page <= 0 ? 1 : page, pageSize);
        Inventory inv = Bukkit.createInventory(null, 54, "Boutique - Page " + page);

        // items
        int slot = 0;
        for (ShopItem s : listings) {
            ItemStack it = s.getItem();
            if (it == null || it.getType().isAir()) continue; // ignore cet item corrompu
            it = it.clone();
            ItemMeta meta = it.getItemMeta();
            if (meta != null) {
                String owner = s.getOwner().toString();
                meta.setDisplayName(meta.hasDisplayName() ? meta.getDisplayName() : it.getType().toString());
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add("");
                lore.add("§aPrix: §e" + s.getPrice() + "$");
                lore.add("§7Vendeur: §f" + s.getSellerName());
                lore.add("§7ID: §f" + s.getId());
                lore.add("");
                lore.add("§2Clic gauche pour acheter");
                meta.setLore(lore);
                // store id in PDC for click handling
                meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, s.getId());
                it.setItemMeta(meta);
            }
            inv.setItem(slot++, it);
            if (slot >= 45) break; // laisse les lignes du bas pour navigation
        }

        // navigation (exemples)
        ItemStack prev = new ItemStack(org.bukkit.Material.ARROW);
        ItemMeta pm = prev.getItemMeta();
        pm.setDisplayName("§ePage précédente");
        prev.setItemMeta(pm);
        inv.setItem(45, prev);

        ItemStack next = new ItemStack(org.bukkit.Material.ARROW);
        ItemMeta nm = next.getItemMeta();
        nm.setDisplayName("§ePage suivante");
        next.setItemMeta(nm);
        inv.setItem(53, next);

        // slot pour refresh ou info
        ItemStack info = new ItemStack(org.bukkit.Material.PAPER);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName("§6Boutique");
        List<String> lore = new ArrayList<>();
        lore.add("§7Cliquez sur un item pour l'acheter.");
        im.setLore(lore);
        info.setItemMeta(im);
        inv.setItem(49, info);

        p.openInventory(inv);
    }

    public NamespacedKey getKey() {
        return key;
    }

}
