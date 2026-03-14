package fr.ruins.plugin.streak.commands;

import fr.ruins.plugin.streak.managers.StreakManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CollectCommand implements CommandExecutor {

    private final StreakManager streakManager;

    public CollectCommand(StreakManager streakManager) {
        this.streakManager = streakManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (!streakManager.canClaim(player.getUniqueId())) {
            player.sendMessage("§cTu as déjà récupéré ta récompense aujourd'hui !");
            return true;
        }

        int streak = streakManager.updateStreak(player.getUniqueId());

        streakManager.giveReward(player, streak);

        return true;
    }
}