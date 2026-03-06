package fr.ruins.plugin.money.commands;

import fr.ruins.plugin.player.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    private final PlayerData playerData;

    public MoneyCommand(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /score <player> <add|remove|set> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }

        UUID uuid = target.getUniqueId();

        String action = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cLe montant doit être un nombre.");
            return true;
        }

        switch (action.toLowerCase()) {

            case "add":
                playerData.addPlayerMoney(uuid, amount);
                break;

            case "remove":
                playerData.removePlayerMoney(uuid, amount);
                break;

            case "set":
                playerData.setPlayerMoney(uuid, amount);
                break;

            default:
                sender.sendMessage("§cAction invalide.");
        }

        return true;
    }
}
