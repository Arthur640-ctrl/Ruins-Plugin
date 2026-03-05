package fr.ruins.plugin;

import fr.ruins.plugin.auth.commands.LoginCommand;
import fr.ruins.plugin.auth.commands.RegisterCommand;
import fr.ruins.plugin.auth.managers.AuthManager;
import fr.ruins.plugin.auth.managers.SessionManager;
import fr.ruins.plugin.ban.commands.BanCommand;
import fr.ruins.plugin.ban.commands.UnbanCommand;
import fr.ruins.plugin.ban.managers.BanManager;
import fr.ruins.plugin.commands.*;
import fr.ruins.plugin.database.managers.MongoManager;
import fr.ruins.plugin.auth.listeners.AuthListener;
import fr.ruins.plugin.ban.listeners.BanListener;
import fr.ruins.plugin.rank.listeners.PlayerKillListener;
import fr.ruins.plugin.shop.listeners.ShopListener;
import fr.ruins.plugin.player.utils.PlayerData;
import fr.ruins.plugin.rank.managers.RankManager;
import fr.ruins.plugin.score.managers.ScoreManager;
import fr.ruins.plugin.scoreboard.managers.ScoreboardManager;
import fr.ruins.plugin.shop.gui.ShopGUI;
import fr.ruins.plugin.shop.managers.ShopManager;
import fr.ruins.plugin.shop.commands.SellCommand;
import fr.ruins.plugin.shop.commands.ShopCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private MongoManager mongoManager;
    private AuthManager authManager;
    private SessionManager sessionManager;
    private PlayerData playerData;
    private RankManager rankManager;
    private ScoreManager scoreManager;
    private ScoreboardManager scoreboardManager;
    private BanManager banManager;
    private ShopManager shopManager;
    private ShopGUI shopGUI;

    private static Main instance;

    @Override
    public void onEnable() {

        instance = this;

        // Connexion MongoDB
        mongoManager = new MongoManager("mongodb://localhost:27017", "Ruins");

        getLogger().info("MongoDB connected !");

        // Classes
        authManager = new AuthManager(mongoManager.getDatabase().getCollection("players"));

        playerData = new PlayerData(mongoManager.getDatabase().getCollection("players"));

        rankManager = new RankManager();

        sessionManager = new SessionManager();

        scoreManager = new ScoreManager(rankManager, mongoManager.getDatabase().getCollection("players"));

        scoreboardManager = new ScoreboardManager(playerData, rankManager, scoreManager);

        banManager = new BanManager(mongoManager.getDatabase().getCollection("bans"));

        shopManager = new ShopManager(mongoManager.getDatabase().getCollection("shop"), playerData);

        shopGUI = new ShopGUI(shopManager);

        // Commandes
        getCommand("ruins").setExecutor(
                new RuinsCommand());

        getCommand("register").setExecutor(
                new RegisterCommand(authManager, sessionManager, scoreboardManager));

        getCommand("login").setExecutor(
                new LoginCommand(authManager, sessionManager, scoreboardManager));

        getCommand("ban").setExecutor(
                new BanCommand(banManager));

        getCommand("unban").setExecutor(
                new UnbanCommand(banManager));

        getCommand("shop").setExecutor(
                new ShopCommand(shopManager));

        getCommand("sell").setExecutor(
                new SellCommand(shopManager, shopGUI));

        // Listeners
        getServer().getPluginManager().registerEvents(
                new AuthListener(sessionManager, authManager, scoreboardManager), this);

        getServer().getPluginManager().registerEvents(
                new PlayerKillListener(playerData, scoreManager, rankManager), this);

        getServer().getPluginManager().registerEvents(
                new BanListener(banManager), this);

        getServer().getPluginManager().registerEvents(
                new ShopListener(shopManager, shopGUI), this);

        getLogger().info("RuinsPlugin activé !");

    }

    @Override
    public void onDisable() {
        if (mongoManager != null) {
            mongoManager.close();
            getLogger().info("MongoDB connection closed!");
        }
    }

    public static Main getInstance() {
        return instance;
    }
}