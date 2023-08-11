package me.hostadam.duels.command;

import me.hostadam.duels.DuelHandler;
import me.hostadam.duels.impl.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class DuelCommand extends Command {

    private final DuelHandler duelHandler;

    public DuelCommand(DuelHandler handler) {
        super("duel", "Send a duel request", "/duel <name> [kit]", Collections.emptyList());
        this.duelHandler = handler;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to run this command.");
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage("§cUsage: /duel <name> [kit]");
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

        if(this.duelHandler.getDuelRequest(player, opponent) != null) {
            sender.sendMessage("§cYou have already sent a duel request.");
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

        Kit kit = this.duelHandler.getDefaultKit();
        if(args.length > 1 && this.duelHandler.getKitByName(args[1]) != null) {
            kit = this.duelHandler.getKitByName(args[1]);
        }

        this.duelHandler.sendRequest(player, opponent, kit);
        player.sendMessage("§eYou have invited §f" + opponent.getName() + " §eto a duel. They have §b60 seconds §eto accept.");
        opponent.sendMessage("§f" + player.getName() + " §ehas invited you to a §cduel§e using kit §f" + kit.getName() + "§e. Use §f/accept " + player.getName() + " §ewithin §b60 seconds §eto accept.");
        return true;
    }
}
