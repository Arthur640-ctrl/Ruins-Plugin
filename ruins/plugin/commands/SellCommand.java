package fr.ruins.plugin.commands;

import fr.ruins.plugin.shop.ShopGUI;
import fr.ruins.plugin.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * /sell -> ouvre l'inventaire de vente où le joueur place l'item à vendre et clique confirmer.
 */
public class SellCommand implements CommandExecutor {

    private final ShopManager manager;
    private final ShopGUI gui;

    public SellCommand(ShopManager manager, ShopGUI gui) {
        this.manager = manager;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }
        Player p = (Player) sender;
        Inventory inv = Bukkit.createInventory(null, 27, "Shop - Vendre");

        // emplacement central pour l'item
        // slot 13 sera la place où le joueur met l'item.
        // bouton confirmer en slot 22
        ItemStack confirm = new ItemStack(org.bukkit.Material.EMERALD);
        ItemMeta cm = confirm.getItemMeta();
        cm.setDisplayName("§aConfirmer la vente (clique ici)");
        List<String> lore = new ArrayList<>();
        lore.add("§7Place ton item dans le slot centrale puis clique.");
        cm.setLore(lore);
        confirm.setItemMeta(cm);
        inv.setItem(22, confirm);

        p.openInventory(inv);
        return true;
    }
}