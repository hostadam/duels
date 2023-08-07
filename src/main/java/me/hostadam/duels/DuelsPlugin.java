package me.hostadam.duels;

import lombok.Getter;
import me.hostadam.duels.util.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class DuelsPlugin extends JavaPlugin {

    @Getter
    private static DuelsPlugin instance;

    @Getter
    private Config arenaConfig, kitConfig;
    @Getter
    private DuelHandler duelHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.arenaConfig = new Config(this, "arena");
        this.kitConfig = new Config(this, "kit");

        this.duelHandler = new DuelHandler(this);
    }

    @Override
    public void onDisable() {
    }
}
