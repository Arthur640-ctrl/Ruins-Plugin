package fr.ruins.plugin.tpa.commands;

import fr.ruins.plugin.tpa.managers.TpaManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpDenyCommand implements CommandExecutor {

    private final TpaManager tpaManager;

    public TpDenyCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player target)) return true;

        UUID requesterUUID = tpaManager.getRequester(target);

        if (requesterUUID == null) {
            target.sendMessage("§cAucune demande.");
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);

        if (requester != null) {
            requester.sendMessage("§cDemande refusée.");
        }

        tpaManager.removeRequest(target);

        target.sendMessage("§cDemande de téléportation refusée.");

        requester.sendMessage(" ");
        requester.sendMessage("§8§m----------------------------------------");
        requester.sendMessage("§c§lTéléportation refusée");
        requester.sendMessage("§7Votre demande de téléportation vers §f" + target.getName() + " §7a été refusée.");
        requester.sendMessage("§8§m----------------------------------------");
        requester.sendMessage(" ");

        requester.playSound(requester.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);

        return true;
    }
}