package fr.ruins.plugin.auth.commands;

import fr.ruins.plugin.auth.managers.AuthManager;
import fr.ruins.plugin.auth.managers.SessionManager;
import fr.ruins.plugin.scoreboard.managers.ScoreboardManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RegisterCommand implements CommandExecutor {

    private final AuthManager authManager;
    private final SessionManager sessionManager;
    private final ScoreboardManager scoreboardManager;

    public RegisterCommand(AuthManager authManager, SessionManager sessionManager, ScoreboardManager scoreboardManager) {
        this.authManager = authManager;
        this.sessionManager = sessionManager;
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length != 1) {
            player.sendMessage("§cUsage: /register <password>");
            return true;
        }

        if (authManager.isRegistered(player.getUniqueId())) {
            player.sendMessage("§cTu es déjà enregistré.");
            return true;
        }

        authManager.register(player.getUniqueId(), player.getName(), args[0]);
        sessionManager.authenticate(player.getUniqueId());

        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setAllowFlight(false);

        scoreboardManager.createScoreboard(player, "NONE", 0, 0, 0, 0);

        player.sendMessage("§aCompte créé et connecté !");
        return true;
    }
}