package me.hostadam.duels.command;

import me.hostadam.duels.DuelHandler;
import me.hostadam.duels.impl.DuelPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", "View the statistics", "/stats [name]", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this.");
            return true;
        }

        if(args.length == 0) {
            DuelPlayer duelPlayer = DuelPlayer.fromPlayer(player);
            this.show(player, duelPlayer);
            return true;
        }

        Player opponent = Bukkit.getPlayer(args[0]);
        if(opponent == null) {
            DuelPlayer duelPlayer = DuelPlayer.fromPlayer(player);
            this.show(player, duelPlayer);
            return true;
        }

        DuelPlayer duelPlayer = DuelPlayer.fromPlayer(opponent);
        this.show(player, duelPlayer);
        return true;
    }

    private void show(CommandSender sender, DuelPlayer duelPlayer) {
        sender.sendMessage("§e§lStatistics");
        sender.sendMessage(" §eKills§7: §f" + duelPlayer.getKills());
        sender.sendMessage(" §eDeaths§7: §f" + duelPlayer.getDeaths());
        sender.sendMessage(" §eWins§7: §f" + duelPlayer.getWins());
        sender.sendMessage(" §eLosses§7: §f" + duelPlayer.getLosses());
        sender.sendMessage(" §eWin Streak§7: §f" + duelPlayer.getWinStreak());
    }
}
