package me.hostadam.duels.impl;

import lombok.Getter;
import me.hostadam.duels.impl.kit.Kit;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class DuelRequest {

    private Kit kit;
    private final long requestTime;

    public DuelRequest(Kit kit) {
        this.kit = kit;
        this.requestTime = System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - this.requestTime >= TimeUnit.SECONDS.toMillis(60);
    }
}
