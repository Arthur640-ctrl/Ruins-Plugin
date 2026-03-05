package fr.ruins.plugin.ban.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanEntry {
    private final String uuid;
    private final String playerName;
    private final String reason;
    private final long endTime; // timestamp ms
    private final String staff;

    public BanEntry(String uuid, String playerName, String reason, long endTime, String staff) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.reason = reason;
        this.endTime = endTime;
        this.staff = staff;
    }

    public String getUuid() { return uuid; }
    public String getPlayerName() { return playerName; }
    public String getReason() { return reason; }
    public long getEndTime() { return endTime; }
    public String getStaff() { return staff; }

    public boolean isExpired() {
        return System.currentTimeMillis() > endTime;
    }

    public String getEndTimeFormatted() {
        if (endTime == Long.MAX_VALUE) return "Permanent";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(new Date(endTime));
    }
}