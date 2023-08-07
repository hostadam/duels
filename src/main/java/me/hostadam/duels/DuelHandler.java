package me.hostadam.duels;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.hostadam.duels.impl.Duel;
import me.hostadam.duels.impl.DuelRequest;
import me.hostadam.duels.impl.arena.Arena;
import me.hostadam.duels.impl.kit.Kit;
import me.hostadam.duels.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

    public void cancelAll() {
        for(Duel duel : this.duels) {
            if(duel.isOngoing()) {
                duel.complete(null);
            }
        }

        this.duels.clear();
        this.duelQueue.clear();
        this.requests.clear();
    }

    public void load() {
        this.loadKits();
        this.loadArena();
    }

    private void loadArena() {
        Config arenaConfig = this.duelsPlugin.getArenaConfig();
        World world = Bukkit.getWorld(arenaConfig.getString("world"));
        if(world == null) return;

        Location firstSpawnLocation = new Location(world, arenaConfig.getDouble("player_one_spawn.x"), arenaConfig.getDouble("player_one_spawn.y"), arenaConfig.getDouble("player_one_spawn.z"));
        Location secondSpawnLocation = new Location(world, arenaConfig.getDouble("player_two_spawn.x"), arenaConfig.getDouble("player_two_spawn.y"), arenaConfig.getDouble("player_two_spawn.z"));
        this.arena = new Arena(firstSpawnLocation, secondSpawnLocation);
    }

    private void loadKits() {
        Config kitConfig = this.duelsPlugin.getKitConfig();
        String defaultKitName = kitConfig.getString("default_kit");

        for (String key : kitConfig.getConfigurationSection("kits").getKeys(false)) {
            String name = kitConfig.getString("kits." + key + ".name");
            if (name == null) continue;
            ItemStack[] armor = new ItemStack[4];
            ItemStack[] contents = new ItemStack[36];

            for (String contentKey : kitConfig.getConfigurationSection("kits." + name + ".inventory_content").getKeys(false)) {
                int slot = kitConfig.getInt("kits." + name + ".inventory_content." + contentKey + ".slot");
                ItemStack piece = new ItemStack(Material.valueOf("kits." + key + ".inventory_content." + contentKey + ".material"));
                piece.setAmount(kitConfig.getInt("kits." + key + ".inventory_content." + contentKey + ".amount"));
                contents[slot] = piece;
            }

            try {
                ItemStack piece = new ItemStack(Material.valueOf("kits." + key + ".armor_content.helmet.material"));
                piece.setAmount(kitConfig.getInt("kits." + key + ".armor_content.helmet.amount"));
                armor[0] = piece;
            } catch (Exception exception) {
                System.out.println("Failed to load helmet for kit " + name);
            }

            try {
                ItemStack piece = new ItemStack(Material.valueOf("kits." + key + ".armor_content.chestplate.material"));
                piece.setAmount(kitConfig.getInt("kits." + key + ".armor_content.chestplate.amount"));
                armor[1] = piece;
            } catch (Exception exception) {
                System.out.println("Failed to load chestplate for kit " + name);
            }

            try {
                ItemStack piece = new ItemStack(Material.valueOf("kits." + key + ".armor_content.leggings.material"));
                piece.setAmount(kitConfig.getInt("kits." + key + ".armor_content.leggings.amount"));
                armor[2] = piece;
            } catch (Exception exception) {
                System.out.println("Failed to load leggings for kit " + name);
            }

            try {
                ItemStack piece = new ItemStack(Material.valueOf("kits." + key + ".armor_content.boots.material"));
                piece.setAmount(kitConfig.getInt("kits." + key + ".armor_content.boots.amount"));
                armor[3] = piece;
            } catch (Exception exception) {
                System.out.println("Failed to load boots for kit " + name);
            }

            this.kits.add(new Kit(name, armor, contents, name.equalsIgnoreCase(defaultKitName)));
        }

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
