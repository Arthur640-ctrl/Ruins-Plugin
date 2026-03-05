package fr.ruins.plugin.score.managers;

import com.mongodb.client.MongoCollection;
import fr.ruins.plugin.rank.utils.Rank;
import fr.ruins.plugin.rank.managers.RankManager;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreManager {

    private final RankManager rankManager;
    private final MongoCollection<Document> player_collection;


    private int[][] table = {
            // Victim:  Recrue, Guerrier, Chasseur, Tueur à gage, Tueur en série, Assaillant, Maréchal
            {10, 5, 0, -5, -10, -20, -30},       // Recrue
            {20, 10, 5, 0, -5, -10, -20},        // Guerrier
            {30, 20, 10, 5, 0, -5, -10},         // Chasseur
            {50, 40, 30, 10, 5, 0, -5},          // Tueur à gage
            {70, 60, 50, 30, 10, 5, 0},          // Tueur en série
            {100, 90, 80, 50, 30, 10, 5},        // Assaillant
            {150, 120, 100, 80, 50, 30, 10}      // Maréchal
    };

    private double repetitionCost = 0.5;
    private int repetitionLenght = 5;

    // Colonne indices:
    // 0 -> Recrue
    // 1 -> Guerrier
    // 2 -> Chasseur
    // 3 -> Tueur à gage
    // 4 -> Tueur en série
    // 5 -> Assaillant
    // 6 -> Maréchal
    private List<Document> cachedClassement = new ArrayList<>();
    private long lastUpdate = 0;


    public ScoreManager(RankManager rankManager, MongoCollection<Document> playerCollection) {
        this.rankManager = rankManager;
        this.player_collection = playerCollection;
    }

    public int calculateScoreGain(Rank killer, Rank victim) {
        int diff = victim.getMinScore() - killer.getMinScore(); // différence de score
        int baseGain = 50; // score de base pour tuer un égal
        int scoreGain = baseGain + diff / 10; // ajuster selon différence de rang
        return Math.max(10, scoreGain); // minimum 10 points
    }

    public int calculateScoreLoss(Rank victim) {
        // La perte de score doit être petite, proportionnelle au rang
        return Math.max(5, victim.getMinScore() / 20);
    }

    public int calculatePrime(Rank rank, int currentScore) {
        int base = rank.getBasePrime();
        int bonus = (int) (Math.sqrt(currentScore));
        return base + bonus;
    }

    public List<Document> getClassement() {
        long now = System.currentTimeMillis();

        // Si plus de 5 secondes → on refresh
        if (now - lastUpdate > 5000) {
            refreshClassement();
            lastUpdate = now;
        }

        return cachedClassement;
    }

    private void refreshClassement() {

        // On récupère tous les joueurs triés par score croissant
        List<Document> classement = player_collection.find()
                .sort(new Document("score", -1)) // ordre croissant
                .projection(new Document("uuid", 1)
                        .append("name", 1)
                        .append("score", 1)) // on ne récupère que ces champs
                .into(new ArrayList<>());

        cachedClassement = classement;
    }

    public List<String> getTop3(UUID playerUuid) {
        List<String> top3 = new ArrayList<>();
        List<Document> classement = getClassement(); // récupère le classement déjà trié

        // Chercher la position du joueur
        int index = -1;
        for (int i = 0; i < classement.size(); i++) {
            if (classement.get(i).getString("uuid").equals(playerUuid.toString())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            // joueur pas trouvé dans le classement
            return top3;
        }

        // Déterminer les indices à récupérer
        int start, end;

        if (index == 0) {
            // premier joueur
            start = 0;
            end = Math.min(3, classement.size()); // récupère 3 ou moins si petit classement
        } else if (index == classement.size() - 1) {
            // dernier joueur
            start = Math.max(0, index - 2);
            end = classement.size();
        } else {
            // joueur au milieu
            start = index - 1;
            end = Math.min(index + 2, classement.size());
        }

        // Construire les chaînes
        for (int i = start; i < end; i++) {
            Document doc = classement.get(i);
            String name = doc.getString("name");
            int score = doc.getInteger("score", 0);
            String line = (i + 1) + ". " + name + " - " + score;
            top3.add(line);
        }

        return top3;
    }



}
