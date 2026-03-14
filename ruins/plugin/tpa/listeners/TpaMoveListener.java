package fr.ruins.plugin.tpa.listeners;

import fr.ruins.plugin.tpa.managers.TpaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TpaMoveListener implements Listener {

    private final TpaManager tpaManager;

    public TpaMoveListener(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (event.getFrom().distance(event.getTo()) == 0) return;

        if (tpaManager.isTeleporting(event.getPlayer())) {
            tpaManager.cancelTeleport(event.getPlayer());
        }
    }
}