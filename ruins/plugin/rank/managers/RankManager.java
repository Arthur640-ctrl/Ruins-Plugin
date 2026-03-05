package fr.ruins.plugin.rank.managers;

import fr.ruins.plugin.rank.utils.Rank;

import java.util.ArrayList;
import java.util.List;

public class RankManager {

    private final List<Rank> ranks = new ArrayList<>();

    public RankManager() {
        ranks.add(new Rank("Recrue", "§7", 0, 50));
        ranks.add(new Rank("Guerrier", "§a", 100, 100));
        ranks.add(new Rank("Chasseur", "§9", 300, 200));
        ranks.add(new Rank("Tueur à gage", "§5", 700, 500));
        ranks.add(new Rank("Tueur en série", "§6", 1500, 800));
        ranks.add(new Rank("Assaillant", "§c", 3000, 1200));
        ranks.add(new Rank("Maréchal", "§e", 6000, 2000));
    }

    public Rank getRankByScore(int score) {
        Rank result = ranks.get(0);

        for (Rank rank : ranks) {
            if (score >= rank.getMinScore()) {
                result = rank;
            } else {
                break;
            }
        }

        return result;

    }

    public List<Rank> getRanks() {
        return ranks;
    }

}
