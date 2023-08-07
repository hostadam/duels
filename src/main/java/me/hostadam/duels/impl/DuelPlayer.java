package me.hostadam.duels.impl;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.hostadam.duels.DuelsPlugin;
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
    private int kills = 0, deaths = 0, wins = 0, losses = 0, winStreak = 0;
    private Location locationAtDuelStart;

    public void load() {
        Document document = DuelsPlugin.getInstance().getMongo().getCollection().find(Filters.eq("uniqueId", this.uniqueId)).first();
        if(document != null) {
            this.kills = document.getInteger("kills");
            this.deaths = document.getInteger("deaths");
            this.wins = document.getInteger("wins");
            this.losses = document.getInteger("losses");
            this.winStreak = document.getInteger("winStreak");
        }
    }

    public void save() {
        Document document = new Document("uniqueId", this.uniqueId);
        document.put("kills", this.kills);
        document.put("deaths", this.deaths);
        document.put("wins", this.wins);
        document.put("losses", this.losses);
        document.put("winStreak", this.winStreak);
        DuelsPlugin.getInstance().getMongo().getCollection().updateOne(Filters.eq("uniqueId", this.uniqueId.toString()), document, new UpdateOptions().upsert(true));
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
        this.locationAtDuelStart = null;
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
