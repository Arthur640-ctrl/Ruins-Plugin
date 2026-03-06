package fr.ruins.plugin.shop.commands;

import fr.ruins.plugin.shop.gui.ShopGUI;
import fr.ruins.plugin.shop.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CloseCommand implements CommandExecutor {

    private final ShopManager manager;

    public CloseCommand(ShopManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players.");
            return true;
        }

        if (args.length != 0) {
            sender.sendMessage("§cUsage: /closeshop");
            return true;
        }

        manager.setShopOpen(false);

        sender.sendMessage("Shop désactivé pour les jouers");

        return true;
    }

}
