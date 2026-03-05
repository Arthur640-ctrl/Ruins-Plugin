package fr.ruins.plugin.scoreboard.managers;

import fr.ruins.plugin.Main;
import fr.ruins.plugin.player.utils.PlayerData;
import fr.ruins.plugin.rank.utils.Rank;
import fr.ruins.plugin.rank.managers.RankManager;
import fr.ruins.plugin.score.managers.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final Map<UUID, Scoreboard> playersScoreboards = new HashMap<>();
    private final int updateRateSec = 1;

    private final PlayerData playerDataManager;
    private final RankManager rankManager;
    private final ScoreManager scoreManager;

    public ScoreboardManager(PlayerData playerDataManager, RankManager rankManager, ScoreManager scoreManager) {
        startAutoUpdate();
        this.playerDataManager = playerDataManager;
        this.rankManager = rankManager;
        this.scoreManager = scoreManager;
    }

    public void createScoreboard(Player player, String rank, int money, int scoreValue, int kills, int deaths) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("main", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "RUINS");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Ligne vide
        obj.getScore("    ").setScore(13);

        // Rank
        obj.getScore(ChatColor.YELLOW + "> Rank: " + ChatColor.WHITE + rank).setScore(12);

        // Argent
        obj.getScore(ChatColor.LIGHT_PURPLE + "> Argent: " + ChatColor.WHITE + money).setScore(11);

        // Score
        obj.getScore(ChatColor.AQUA + "> Score: " + ChatColor.WHITE + scoreValue).setScore(10);

        // Ligne vide
        obj.getScore("   ").setScore(9);

        // Kills
        obj.getScore(ChatColor.GREEN + "> Kills: " + ChatColor.WHITE + kills).setScore(8);

        // Morts
        obj.getScore(ChatColor.RED + "> Morts: " + ChatColor.WHITE + deaths).setScore(7);

        // Ligne vide
        obj.getScore("  ").setScore(6);

        // Classement global
        obj.getScore(ChatColor.YELLOW + "> Top joueurs:").setScore(5);


        // Classement détaillé dynamique
        List<String> top3 = scoreManager.getTop3(player.getUniqueId());

        if (top3.isEmpty()) {
            obj.getScore(ChatColor.GRAY + "Chargement...").setScore(4);
        } else {
            int scoreLine = 4;
            for (String line : top3) {
                obj.getScore(ChatColor.GRAY + line).setScore(scoreLine);
                scoreLine--;
            }
        }

        // Ligne vide
        obj.getScore(" ").setScore(1);

        // Footer
        obj.getScore(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "» ruins.rf.gd «").setScore(0);

        player.setScoreboard(board);
        playersScoreboards.put(player.getUniqueId(), board);
    }

    public void updateScoreboard() {
        for (UUID uuid : playersScoreboards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;

            String rank = rankManager.getRankByScore(playerDataManager.getPlayerScore(uuid)).getName();
            int money = playerDataManager.getPlayerMoney(uuid);
            int scoreValue = playerDataManager.getPlayerScore(uuid);
            int kills = playerDataManager.getPlayerKills(uuid);
            int deaths = playerDataManager.getPlayerDeaths(uuid);

            createScoreboard(player, rank, money, scoreValue, kills, deaths);
        }
    }

    private void startAutoUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateScoreboard();
                updateTabList();
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L * updateRateSec); // 20 ticks = 1 seconde
    }

    public void removePlayer(Player player) {
        if (player == null) return;

        try {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        } catch (IllegalStateException ignored) {

        }

        playersScoreboards.remove(player.getUniqueId());
    }

    private String getPlayerPrefix(Player player) {
        Rank rank = rankManager.getRankByScore(playerDataManager.getPlayerScore(player.getUniqueId()));
        String rankName = rank.getColor() + rank.getName(); // couleur du rang

        String role = "§9[JOUEUR]";
        if (player.getName().equalsIgnoreCase("Arthur")) {
            role = "§c[ADMIN]";
        }

        return role + " " + "[" + rankName + "]" + " ";
    }

    public void updateTabList() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setPlayerListHeaderFooter(
                    "§6§lBienvenue sur §e§lRuins",
                    "§7N'hésitez pas à rejoindre le §9Discord"
            );

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                String prefix = getPlayerPrefix(onlinePlayer);
                onlinePlayer.setPlayerListName(prefix + onlinePlayer.getName());
            }
        }
    }
}
