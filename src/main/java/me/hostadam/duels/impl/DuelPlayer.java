package me.hostadam.duels.impl;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class DuelPlayer {

    private static final Map<UUID, DuelPlayer> PLAYER_MAP = new HashMap<>();

    @NonNull
    private UUID uniqueId;
    private int kills, deaths, wins, losses, winStreak;
    private Location locationAtDuelStart;

    public void load() {
        Document document;
    }

    public void save() {

    }

    public void updateWin() {
        this.wins++;
        this.winStreak++;
        this.save();
    }

    public void updateLoss() {
        this.losses--;
        if(this.winStreak > 0) this.winStreak = 0;
        this.save();
    }

    public void returnToLocation() {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if(player == null || this.locationAtDuelStart == null) return;
        player.teleport(this.locationAtDuelStart);
    }

    public static DuelPlayer fromPlayer(OfflinePlayer player) {
        return fromUniqueId(player.getUniqueId());
    }

    public static DuelPlayer fromUniqueId(UUID uniqueId) {
        if(!PLAYER_MAP.containsKey(uniqueId)) {
            DuelPlayer duelPlayer = new DuelPlayer(uniqueId);
            duelPlayer.load();
            PLAYER_MAP.put(uniqueId, duelPlayer);
            return duelPlayer;
        }

        return PLAYER_MAP.get(uniqueId);
    }
}
