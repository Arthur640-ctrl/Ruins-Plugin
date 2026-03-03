package fr.ruins.plugin.listeners;

import fr.ruins.plugin.Main;
import fr.ruins.plugin.shop.ShopGUI;
import fr.ruins.plugin.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener pour gérer les clics sur les GUIs de la boutique et le chat pour entrer le prix lors de la vente.
 *
 * Flux vente simplifié :
 * - /sell -> ouvre inventaire spécial (le joueur place l'item en slot 13)
 * - clic sur "Confirmer" -> on demande le prix en chat ; la prochaine saisie numérique finalise la vente
 */
public class ShopListener implements Listener {

    private final ShopManager manager;
    private final ShopGUI gui;
    private final NamespacedKey shopIdKey;

    // état temporaire : joueur -> ItemStack à vendre (en attente de prix)
    private final Map<UUID, ItemStack> pendingSell = new HashMap<>();

    public ShopListener(ShopManager manager, ShopGUI gui) {
        this.manager = manager;
        this.gui = gui;
        this.shopIdKey = gui.getKey();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player == false) return;
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        String title = e.getView().getTitle();

        // --- Boutique principale ---
        if (title != null && title.startsWith("Boutique -")) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;

            // navigation
            if (e.getSlot() == 45) {
                // page précédente -> calcule page actuelle
                int page = extractPageFromTitle(title);
                gui.openShop(p, Math.max(1, page - 1));
                return;
            } else if (e.getSlot() == 53) {
                int page = extractPageFromTitle(title);
                gui.openShop(p, page + 1);
                return;
            }

            // Check PDC pour récupérer l'id de l'annonce
            String id = clicked.getItemMeta() != null && clicked.getItemMeta().getPersistentDataContainer().has(shopIdKey, org.bukkit.persistence.PersistentDataType.STRING)
                    ? clicked.getItemMeta().getPersistentDataContainer().get(shopIdKey, org.bukkit.persistence.PersistentDataType.STRING)
                    : null;
            if (id != null) {
                // Acheter
                boolean ok = manager.buyListing(id, p.getUniqueId());
                if (ok) {
                    p.sendMessage("§aAchat réussi !");
                    // refresh page 1 pour simplicité
                    gui.openShop(p, 1);
                } else {
                    p.sendMessage("§cImpossible d'acheter cet item (argent insuffisant / déjà vendu / propriétaire).");
                }
            }
            return;
        }

        // --- Inventory de vente (titre "Shop - Vendre") ---
        if (title != null && title.equals("Shop - Vendre")) {
            // si clic sur slot confirmer (22)
            if (e.getSlot() == 22) {
                e.setCancelled(true); // bloque juste le clic sur "Confirmer"
                ItemStack item = inv.getItem(13); // tu peux aussi prendre n'importe quel slot que tu veux
                if (item == null || item.getType().isAir()) {
                    p.sendMessage("§cPlace un item pour le vendre.");
                    return;
                }
                p.closeInventory();
                pendingSell.put(p.getUniqueId(), item.clone());
                p.sendMessage("§eEntrez le prix pour cet item dans le chat (nombre, ex: 250).");
                return;
            }

            // pour tous les autres slots, ne pas bloquer
        }
    }

    private int extractPageFromTitle(String title) {
        try {
            String[] parts = title.split("Page ");
            if (parts.length >= 2) return Integer.parseInt(parts[1].trim());
        } catch (Exception ignored) {}
        return 1;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        if (!pendingSell.containsKey(u)) return;
        e.setCancelled(true);
        String msg = e.getMessage().trim();
        double price;
        try {
            price = Double.parseDouble(msg);
        } catch (NumberFormatException ex) {
            e.getPlayer().sendMessage("§cPrix invalide. Entrez un nombre (ex: 150).");
            return;
        }
        ItemStack item = pendingSell.remove(u);
        // create listing (synchronisé sur le thread principal)
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            manager.createListing(u, item, price, item.getAmount());
            Player p = Bukkit.getPlayer(u);
            if (p != null && p.isOnline()) p.sendMessage("§aAnnonce créée au prix de §e" + price + "§a.");
        });
    }
}