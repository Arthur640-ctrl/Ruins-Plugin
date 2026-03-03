package fr.ruins.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RuinsCommand implements CommandExecutor {

    private static final String PERMISSION = "ruins.use";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Vérifie que c’est un joueur
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        Player player = (Player) sender;

        // Vérifie la permission
        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage("§cTu n'as pas la permission d'utiliser cette commande.");
            return true;
        }

        // Action principale
        player.sendMessage("§aBienvenue dans le plugin Ruins !");
        return true;
    }
}