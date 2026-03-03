package fr.ruins.plugin.listeners;

import fr.ruins.plugin.ban.BanEntry;
import fr.ruins.plugin.ban.BanManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanListener implements Listener {

    private final BanManager banManager;

    public BanListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        BanEntry ban = banManager.getBan(uuid);
        if (ban != null) {
            String msg = ChatColor.RED + "❌ Vous êtes banni ! ❌\n"
                    + ChatColor.YELLOW + "Raison: " + ban.getReason() + "\n"
                    + ChatColor.GRAY + "Fin du ban: " + ban.getEndTimeFormatted();
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, msg);
        }
    }
}
