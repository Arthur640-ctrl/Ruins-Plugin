package fr.ruins.plugin.rank.listeners;

import fr.ruins.plugin.player.utils.PlayerData;
import fr.ruins.plugin.rank.utils.Rank;
import fr.ruins.plugin.rank.managers.RankManager;
import fr.ruins.plugin.score.managers.ScoreManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {

    private final PlayerData playerData;
    private final ScoreManager scoreManager;
    private final RankManager rankManager;

    public PlayerKillListener(PlayerData playerData, ScoreManager scoreManager, RankManager rankManager) {
            this.playerData = playerData;
            this.scoreManager = scoreManager;
            this.rankManager = rankManager;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        Player killer = null;

        if (victim.getLastDamageCause() != null) {
            if (victim.getKiller() != null) {
                killer = victim.getKiller();
            }
        }

        if (killer != null) {
            Rank killerRank = rankManager.getRankByScore(playerData.getPlayerScore(killer.getUniqueId()));
            Rank victimRank = rankManager.getRankByScore(playerData.getPlayerScore(victim.getUniqueId()));

            int gain = scoreManager.calculateScoreGain(killerRank, victimRank);
            int prime = scoreManager.calculatePrime(killerRank, playerData.getPlayerScore(killer.getUniqueId()));

            playerData.addPlayerScore(killer.getUniqueId(), gain);
            playerData.addPlayerMoney(killer.getUniqueId(), prime);
            playerData.addPlayerKills(killer.getUniqueId(), 1);

            playerData.addPlayerDeaths(victim.getUniqueId(), 1);
            playerData.removePlayerMoney(victim.getUniqueId(), prime);

            int loss = scoreManager.calculateScoreLoss(victimRank);
            playerData.removePlayerScore(victim.getUniqueId(), loss);
        }

    }

}
