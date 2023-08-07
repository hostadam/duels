package me.hostadam.duels.listener;

import lombok.AllArgsConstructor;
import me.hostadam.duels.DuelsPlugin;
import me.hostadam.duels.impl.Duel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@AllArgsConstructor
public class DamageListener implements Listener {

    private final DuelsPlugin duelsPlugin;

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        Duel duel = this.duelsPlugin.getDuelHandler().getDuelByPlayer(player);
        if(duel != null && duel.isOngoing() && duel.getCountdownTime() > 0) {
            event.setCancelled(true);
        }
    }
}
