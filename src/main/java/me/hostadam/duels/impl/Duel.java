package me.hostadam.duels.impl;

import me.hostadam.duels.DuelsPlugin;
import me.hostadam.duels.impl.arena.Arena;
import me.hostadam.duels.impl.kit.Kit;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class Duel {

    private UUID sender, opponent;
    private Kit kit;
    private boolean ongoing = false;
    private int countdownTime = 5, gameTime = 0;
    private BukkitTask task;

    public Duel(UUID sender, UUID opponent, Kit kit) {
        this.kit = kit;
        this.sender = sender;
        this.opponent = opponent;
    }

    public void complete(UUID winner) {
        DuelPlayer senderDuelPlayer = DuelPlayer.fromUniqueId(sender), opponentDuelPlayer = DuelPlayer.fromUniqueId(opponent);
        senderDuelPlayer.returnToLocation();
        opponentDuelPlayer.returnToLocation();

        if(this.sender.equals(winner)) {
            senderDuelPlayer.updateWin();
            opponentDuelPlayer.updateLoss();
        } else {
            opponentDuelPlayer.updateWin();
            senderDuelPlayer.updateLoss();
        }
    }

    public boolean start(Arena arena) {
        return this.start(arena, Bukkit.getPlayer(this.sender), Bukkit.getPlayer(this.opponent));
    }

    public boolean start(Arena arena, Player initiator, Player opponent) {
        if(initiator == null || opponent == null) {
            return false;
        }

        arena.setOccupied(true);
        this.ongoing = true;

        DuelPlayer duelPlayer = DuelPlayer.fromPlayer(initiator);
        duelPlayer.setLocationAtDuelStart(initiator.getLocation());

        DuelPlayer opponentPlayer = DuelPlayer.fromPlayer(opponent);
        opponentPlayer.setLocationAtDuelStart(opponent.getLocation());

        initiator.teleport(arena.getSpawnPointOne());
        opponent.teleport(arena.getSpawnPointTwo());

        this.task = Bukkit.getScheduler().runTaskTimer(DuelsPlugin.getInstance(), () -> {
            if(countdownTime > 0) {
                countdownTime--;
                displayActionBar(initiator, "§eStarting in §f" + countdownTime + " §esecond" + (countdownTime != 1 ? "s" : "") + "§e.");
                displayActionBar(opponent, "§eStarting in §f" + countdownTime + " §esecond" + (countdownTime != 1 ? "s" : "") + "§e.");
            } else {
                gameTime++;
                displayActionBar(initiator, "§eGame Time§7: §f" + gameTime + "s");
                displayActionBar(opponent, "§eGame Time§7: §f" + gameTime + "s");
            }
        }, 20, 20);
        return true;
    }

    public void displayActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public boolean isIncluded(Player player) {
        final UUID uniqueId = player.getUniqueId();
        return this.sender.equals(uniqueId) || this.opponent.equals(uniqueId);
    }
}
