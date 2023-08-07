package me.hostadam.duels.listener;

import lombok.AllArgsConstructor;
import me.hostadam.duels.DuelsPlugin;
import me.hostadam.duels.impl.Duel;
import me.hostadam.duels.impl.DuelPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@AllArgsConstructor
public class DeathListener implements Listener {

    private final DuelsPlugin duelsPlugin;

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Duel duel = this.duelsPlugin.getDuelHandler().getDuelByPlayer(player);

        if(duel != null) {
            this.duelsPlugin.getDuelHandler().finishMatch(duel, duel.getOpponentPlayer(player));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        DuelPlayer duelPlayer = DuelPlayer.fromPlayer(player);
        if(duelPlayer.getLocationAtDuelStart() != null) {
            duelPlayer.returnToLocation();
        }
    }
}
