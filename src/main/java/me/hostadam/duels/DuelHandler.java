package me.hostadam.duels;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.hostadam.duels.impl.Duel;
import me.hostadam.duels.impl.DuelRequest;
import me.hostadam.duels.impl.arena.Arena;
import me.hostadam.duels.impl.kit.Kit;
import me.hostadam.duels.util.Config;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelHandler {

    private final DuelsPlugin duelsPlugin;
    private Arena arena;
    private Kit defaultKit;
    private List<Kit> kits = new ArrayList<>();
    private List<Duel> duels = new ArrayList<>(), duelQueue = new ArrayList<>();;
    private Table<UUID, UUID, DuelRequest> requests = HashBasedTable.create();

    public DuelHandler(DuelsPlugin plugin) {
        this.duelsPlugin = plugin;
        this.load();
    }

    public void load() {
        Config kitConfig = this.duelsPlugin.getKitConfig();
        for(String key : this.duelsPlugin.getKitConfig().getConfigurationSection("kits").getKeys(false)) {

        }
    }

    private void loadKits() {
        Config kitConfig = this.duelsPlugin.getKitConfig();
        for(String key : kitConfig.getConfigurationSection("kits").getKeys(false)) {
            String name = kitConfig.getString("kits." + key + ".name");
            ItemStack[] armor = new ItemStack[4];

            if(kitConfig.contains("kits." + key + ".armor-content.helmet")) {
                ItemStack helmet = new ItemStack(Material.valueOf("kits." + key + ".armor-content.helmet.material"));
                helmet.setAmount(kitConfig.getInt("kits." + key + ".armor-content.helmet.amount"));
                armor[0] = helmet;
            }

            if(kitConfig.contains("kits." + key + ".armor-content.chestplate")) {
                ItemStack chestplate = new ItemStack(Material.valueOf("kits." + key + ".armor-content.chestplate.material"));
                chestplate.setAmount(kitConfig.getInt("kits." + key + ".armor-content.chestplate.amount"));
                armor[1] = chestplate;
            }

            if(kitConfig.contains("kits." + key + ".armor-content.leggings")) {
                ItemStack leggings = new ItemStack(Material.valueOf("kits." + key + ".armor-content.leggings.material"));
                leggings.setAmount(kitConfig.getInt("kits." + key + ".armor-content.leggings.amount"));
                armor[2] = leggings;
            }

            if(kitConfig.contains("kits." + key + ".armor-content.leggings")) {
                ItemStack boots = new ItemStack(Material.valueOf("kits." + key + ".armor-content.boots.material"));
                boots.setAmount(kitConfig.getInt("kits." + key + ".armor-content.boots.amount"));
                armor[4] = boots;
            }
        }
        
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
