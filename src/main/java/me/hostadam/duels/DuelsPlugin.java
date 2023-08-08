package me.hostadam.duels;

import lombok.Getter;
import me.hostadam.duels.command.AcceptCommand;
import me.hostadam.duels.command.DuelCommand;
import me.hostadam.duels.command.StatsCommand;
import me.hostadam.duels.database.DuelMongo;
import me.hostadam.duels.listener.DamageListener;
import me.hostadam.duels.listener.DeathListener;
import me.hostadam.duels.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class DuelsPlugin extends JavaPlugin {

    @Getter
    private static DuelsPlugin instance;

    @Getter
    private Config arenaConfig, kitConfig, databaseConfig;
    @Getter
    private DuelHandler duelHandler;

    @Getter
    private DuelMongo mongo;

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        instance = this;

        this.databaseConfig = new Config(this, "database");
        this.arenaConfig = new Config(this, "arena");
        this.kitConfig = new Config(this, "kit");
        this.mongo = new DuelMongo(this);
        this.duelHandler = new DuelHandler(this);

        try {
            Field bukkitCommandMap = getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register("accept", new AcceptCommand(this.duelHandler));
            commandMap.register("duel", new DuelCommand(this.duelHandler));
            commandMap.register("stats", new StatsCommand());
        } catch(Exception exception) {
            exception.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
    }

    @Override
    public void onDisable() {
        this.duelHandler.cancelAll();
        this.mongo.getClient().close();
    }
}
