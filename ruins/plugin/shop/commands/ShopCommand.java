package fr.ruins.plugin.shop.commands;

import fr.ruins.plugin.shop.gui.ShopGUI;
import fr.ruins.plugin.shop.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /shop -> ouvre la GUI principale
 */
public class ShopCommand implements CommandExecutor {

    private final ShopManager manager;
    private final ShopGUI gui;

    public ShopCommand(ShopManager manager) {
        this.manager = manager;
        this.gui = new ShopGUI(manager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }
        Player p = (Player) sender;
        int page = 1;
        if (args.length >= 1) {
            try { page = Integer.parseInt(args[0]); } catch (Exception ignored) { page = 1; }
        }
        gui.openShop(p, page);
        return true;
    }
}