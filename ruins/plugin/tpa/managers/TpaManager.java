package fr.ruins.plugin.tpa.managers;

import fr.ruins.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private final Map<UUID, UUID> requests = new HashMap<>();
    private final Map<UUID, Location> pendingTeleport = new HashMap<>();
    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();
    private final Map<UUID, Boolean> tpaHere = new HashMap<>();

    public void sendRequest(Player sender, Player target) {

        requests.put(target.getUniqueId(), sender.getUniqueId());

        sender.sendMessage("§aDemande de téléportation envoyée à §e" + target.getName());

        target.sendMessage(" ");
        target.sendMessage("§8§m----------------------------------------");
        target.sendMessage("§e§lDemande de téléportation");
        target.sendMessage("§f" + sender.getName() + " §7souhaite se téléporter à toi.");
        target.sendMessage(" ");
        target.sendMessage("§a/tpaccept §7pour accepter");
        target.sendMessage("§c/tpdeny §7pour refuser");
        target.sendMessage("§8§m----------------------------------------");
        target.sendMessage(" ");

        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
    }

    public void sendRequestHere(Player sender, Player target) {
        requests.put(target.getUniqueId(), sender.getUniqueId());
        tpaHere.put(target.getUniqueId(), true); // signal que c’est un /tpahere

        sender.sendMessage("§aDemande de téléportation envoyée à §e" + target.getName() + " §apour qu'il vienne vers vous.");

        target.sendMessage(" ");
        target.sendMessage("§8§m----------------------------------------");
        target.sendMessage("§e§lDemande de téléportation");
        target.sendMessage("§f" + sender.getName() + " §7souhaite que vous veniez à lui.");
        target.sendMessage(" ");
        target.sendMessage("§a/tpaccept §7pour accepter");
        target.sendMessage("§c/tpdeny §7pour refuser");
        target.sendMessage("§8§m----------------------------------------");
        target.sendMessage(" ");

        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
    }

    public UUID getRequester(Player target) {
        return requests.get(target.getUniqueId());
    }

    public void removeRequest(Player target) {
        requests.remove(target.getUniqueId());
    }

    public void startTeleport(Player requester, Player target) {

        Location startLocation = requester.getLocation();
        pendingTeleport.put(requester.getUniqueId(), startLocation);

        requester.sendMessage("§eTéléportation en cours... Ne bougez pas !");

        final int[] countdown = {3};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(
                Main.getInstance(),
                () -> {

                    if (!pendingTeleport.containsKey(requester.getUniqueId())) return;

                    if (countdown[0] > 0) {

                        requester.sendTitle(
                                "§eTéléportation dans " + countdown[0],
                                "",
                                0,
                                20,
                                0
                        );

                        requester.playSound(
                                requester.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_HAT,
                                1f,
                                1f
                        );

                        countdown[0]--;

                        return;
                    }

                    // Téléportation
                    requester.teleport(target.getLocation());

                    requester.sendTitle(
                            "§aTéléporté !",
                            "",
                            5,
                            40,
                            10
                    );

                    requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);

                    // Nettoyage
                    pendingTeleport.remove(requester.getUniqueId());

                    BukkitTask currentTask = teleportTasks.remove(requester.getUniqueId());
                    if (currentTask != null) {
                        currentTask.cancel();
                    }

                },
                0L,
                20L
        );

        teleportTasks.put(requester.getUniqueId(), task);
    }

    public void cancelTeleport(Player player) {

        UUID uuid = player.getUniqueId();

        if (!pendingTeleport.containsKey(uuid)) return;

        pendingTeleport.remove(uuid);

        if (teleportTasks.containsKey(uuid)) {
            teleportTasks.get(uuid).cancel();
            teleportTasks.remove(uuid);
        }

        player.sendMessage("§cTéléportation annulée car tu as bougé.");
    }

    public boolean isTeleporting(Player player) {
        return pendingTeleport.containsKey(player.getUniqueId());
    }

    public boolean isTpHere(Player target) {
        return tpaHere.getOrDefault(target.getUniqueId(), false);
    }

    public void removeTpHere(Player target) {
        tpaHere.remove(target.getUniqueId());
    }
}
