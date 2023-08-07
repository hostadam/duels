package me.hostadam.duels.command;

import me.hostadam.duels.DuelHandler;
import me.hostadam.duels.impl.DuelRequest;
import me.hostadam.duels.impl.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class AcceptCommand extends Command {

    private final DuelHandler duelHandler;

    public AcceptCommand(DuelHandler handler) {
        super("accept", "Accept a duel request", "/accept <name>", Collections.emptyList());
        this.duelHandler = handler;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to run this command.");
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage("§cUsage: /accept <name>");
            return true;
        }

        Player opponent = Bukkit.getPlayer(args[0]);
        if(opponent == null) {
            sender.sendMessage("§cInvalid player.");
            return true;
        }
        
        if(player.getUniqueId().equals(opponent.getUniqueId())) {
            sender.sendMessage("§cYou cannot duel yourself.");
            return true;
        }

        if(this.duelHandler.getDuelByPlayer(player) != null) {
            sender.sendMessage("§cYou are already in a duel.");
            return true;
        }

        if(this.duelHandler.getDuelByPlayer(opponent) != null) {
            sender.sendMessage("§cThe opponent is already in a duel.");
            return true;
        }

        DuelRequest request = this.duelHandler.getDuelRequest(opponent, player);
        if(request == null) {
            sender.sendMessage("§cThis player has not sent a duel request.");
            return true;
        }

        this.duelHandler.startDuel(opponent, player, request);
        return true;
    }
}
