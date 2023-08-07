package me.hostadam.duels;

import com.google.common.collect.Table;
import me.hostadam.duels.impl.Duel;
import me.hostadam.duels.impl.DuelRequest;
import me.hostadam.duels.impl.arena.Arena;
import me.hostadam.duels.impl.kit.Kit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DuelHandler {

    private final DuelsPlugin duelsPlugin;
    private Arena arena;
    private Kit defaultKit;
    private List<Kit> kits;
    private List<Duel> duels, duelQueue;
    private Table<UUID, UUID, DuelRequest> requests;

    public DuelHandler(DuelsPlugin plugin) {
        this.duelsPlugin = plugin;
        this.load();
    }

    public void load() {

    }

    public void save() {

    }

    public void startDuel(Player initiator, Player opponent, DuelRequest request) {
        Duel duel = new Duel(initiator.getUniqueId(), opponent.getUniqueId(), request.getKit());
        this.duels.add(duel);

        if(this.arena.isOccupied()) {
            this.duelQueue.add(duel);
            initiator.sendMessage("§eA duel is already occurring. You have been placed in §cqueue§e.");
            opponent.sendMessage("§eA duel is already occurring. You have been placed in §cqueue§e.");
        } else {
            duel.start(this.arena, initiator, opponent);
        }
    }

    public void sendRequest(Player initiator, Player opponent, Kit kit) {
        final UUID initiatorUUID = initiator.getUniqueId(), opponentUUID = opponent.getUniqueId();
        this.requests.put(initiatorUUID, opponentUUID, new DuelRequest(kit));
    }

    public void finishMatch(Duel duel, Player winner) {
        duel.complete(winner.getUniqueId());
        this.duels.remove(duel);
        this.arena.setOccupied(false);

        if(!this.duelQueue.isEmpty()) {
            Duel newDuel = this.duelQueue.remove(0);
            if(!newDuel.start(this.arena)) {
                this.duels.remove(duel);
            }
        }
    }

    public Kit getKitByName(String name) {
        return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public DuelRequest getDuelRequest(Player initiator, Player opponent) {
        return this.requests.get(initiator.getUniqueId(), opponent.getUniqueId());
    }

    public Duel getDuelByPlayer(Player player) {
        return this.duels.stream().filter(duel -> duel.isIncluded(player)).findAny().orElse(null);
    }

    public Kit getDefaultKit() {
        return this.kits.stream().filter(Kit::isDefaultKit).findAny().orElse(null);
    }
}
