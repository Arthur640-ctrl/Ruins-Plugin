package fr.ruins.plugin.tpa.commands;

import fr.ruins.plugin.tpa.managers.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class TpahereCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpahereCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage("§cUsage : /tpahere <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous téléporter à vous-même !");
            return true;
        }

        tpaManager.sendRequestHere(player, target);

        return true;
    }
}