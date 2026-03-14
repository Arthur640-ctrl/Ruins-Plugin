package fr.ruins.plugin.tpa.commands;

import fr.ruins.plugin.tpa.managers.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpAcceptCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpAcceptCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player target)) return true;

        UUID requesterUUID = tpaManager.getRequester(target);

        if (requesterUUID == null) {
            target.sendMessage("§cAucune demande de téléportation.");
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);

        if (requester == null) {
            target.sendMessage("§cLe joueur n'est plus connecté.");
            tpaManager.removeRequest(target);
            tpaManager.removeTpHere(target);
            return true;
        }

        boolean isTpHere = tpaManager.isTpHere(target);

        tpaManager.removeRequest(target);
        tpaManager.removeTpHere(target);

        if (isTpHere) {
            target.sendMessage("§aTéléportation vers §e" + requester.getName() + " §adans 3 secondes...");
            requester.sendMessage("§a" + target.getName() + " va se téléporter vers vous !");
            tpaManager.startTeleport(target, requester);
        } else {
            target.sendMessage("§aVous avez accepté la demande de téléportation de §e" + requester.getName() + "§a !");

            requester.sendMessage(" ");
            requester.sendMessage("§8§m----------------------------------------");
            requester.sendMessage("§a§lTéléportation acceptée");
            requester.sendMessage("§7Téléportation dans §e3 secondes...");
            requester.sendMessage("§cNe bougez pas !");
            requester.sendMessage("§8§m----------------------------------------");
            requester.sendMessage(" ");

            tpaManager.startTeleport(requester, target);
        }

        return true;
    }
}