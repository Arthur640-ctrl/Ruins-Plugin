package fr.ruins.plugin.ban.commands;

import fr.ruins.plugin.ban.utils.BanEntry;
import fr.ruins.plugin.ban.managers.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class BanCommand implements CommandExecutor {

    private final BanManager banManager;

    public BanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /ban <player> <duration> <reason>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Joueur introuvable !");
            return true;
        }

        String durationStr = args[1];
        long duration = parseDuration(durationStr); // en ms
        if (duration < 0) {
            sender.sendMessage("Durée invalide ! Format: 1d, 2h, 30m");
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        long endTime = (duration == 0 ? Long.MAX_VALUE : System.currentTimeMillis() + duration);

        BanEntry ban = new BanEntry(target.getUniqueId().toString(), target.getName(), reason, endTime, sender.getName());
        banManager.addBan(ban);

        target.kickPlayer("Vous avez été banni !");
        sender.sendMessage("Joueur banni avec succès !");
        return true;
    }

    private long parseDuration(String str) {
        try {
            if (str.endsWith("d")) {
                return TimeUnit.DAYS.toMillis(Long.parseLong(str.replace("d", "")));
            } else if (str.endsWith("h")) {
                return TimeUnit.HOURS.toMillis(Long.parseLong(str.replace("h", "")));
            } else if (str.endsWith("m")) {
                return TimeUnit.MINUTES.toMillis(Long.parseLong(str.replace("m", "")));
            } else {
                return Long.parseLong(str) * 1000; // secondes
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
