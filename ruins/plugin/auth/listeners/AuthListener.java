package fr.ruins.plugin.auth.listeners;

import fr.ruins.plugin.auth.managers.AuthManager;
import fr.ruins.plugin.auth.managers.SessionManager;
import fr.ruins.plugin.scoreboard.managers.ScoreboardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class AuthListener implements Listener {

    private final SessionManager sessionManager;
    private final AuthManager authManager;
    private final ScoreboardManager scoreboardManager;

    public AuthListener(SessionManager sessionManager, AuthManager authManager, ScoreboardManager scoreboardManager) {
        this.sessionManager = sessionManager;
        this.authManager = authManager;
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        sessionManager.logout(uuid);

        if (authManager.isRegistered(uuid)) {
            player.sendTitle("§aBienvenue !", "§7Utilise /login <motdepasse>", 10, 200, 10);
        } else {
            player.sendTitle("§eBienvenue !", "§7Utilise /register <motdepasse>", 10, 200, 10);
        }

        applyLoginEffects(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!sessionManager.is_authenticated(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (sessionManager.is_authenticated(uuid)) return;

        String msg = event.getMessage().toLowerCase();

        if (!msg.startsWith("/login") && !msg.startsWith("/register")) {
            event.setCancelled(true);
            player.sendMessage("§cTu dois te connecter !");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        sessionManager.logout(uuid);
        authManager.setLogin(uuid, false);
    }

    public void applyLoginEffects(Player player) {

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.BLINDNESS,
                Integer.MAX_VALUE,
                100,
                true,
                false,
                false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW_DIGGING,
                Integer.MAX_VALUE,
                255,
                true,
                false,
                false
        ));

        player.setWalkSpeed(0f);
        player.setFlySpeed(0f);

        scoreboardManager.removePlayer(player);
    }

    public void removeLoginEffects(Player player) {

        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);

        scoreboardManager.createScoreboard(player, "NONE", 0, 0, 0, 0);
    }
}