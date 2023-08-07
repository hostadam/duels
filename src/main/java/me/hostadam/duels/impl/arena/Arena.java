package me.hostadam.duels.impl.arena;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class Arena {

    @NonNull
    private Location spawnPointOne, spawnPointTwo;
    @Setter
    private boolean occupied = false;
}
