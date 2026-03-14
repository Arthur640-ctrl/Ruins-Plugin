package fr.ruins.plugin.streak.managers;

import fr.ruins.plugin.player.utils.PlayerData;
import org.bukkit.entity.Player;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class StreakManager {

    private final PlayerData playerData;

    public StreakManager(PlayerData playerData) {
        this.playerData = playerData;
    }

    public boolean canClaim(UUID uuid) {

        long lastClaim = playerData.getLastClaim(uuid);

        if (lastClaim == 0) {
            return true;
        }

        LocalDate lastDate = Instant.ofEpochMilli(lastClaim)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return !lastDate.equals(LocalDate.now());
    }

    public int updateStreak(UUID uuid) {

        long lastClaim = playerData.getLastClaim(uuid);
        int streak = playerData.getStreak(uuid);

        LocalDate today = LocalDate.now();

        if (lastClaim == 0) {
            streak = 1;
        } else {

            LocalDate lastDate = Instant.ofEpochMilli(lastClaim)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            long days = ChronoUnit.DAYS.between(lastDate, today);

            if (days == 1) {
                streak++;
            }
            else if (days > 1) {
                streak = 1;
            }
        }

        playerData.setStreak(uuid, streak);
        playerData.setLastClaim(uuid, System.currentTimeMillis());

        return streak;
    }

    public void giveReward(Player player, int streak) {

        int reward = 100 + (streak * 50);

        playerData.addPlayerMoney(player.getUniqueId(), reward);

        player.sendMessage("§6+" + reward + "$ !");
        player.sendMessage("§eStreak: §a" + streak);
    }
}