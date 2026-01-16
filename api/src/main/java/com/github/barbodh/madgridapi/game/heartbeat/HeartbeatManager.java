package com.github.barbodh.madgridapi.game.heartbeat;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class HeartbeatManager {
    private static final Map<String, Long> playerHeartbeats = new ConcurrentHashMap<>();
    private static final long PLAYER_TIMEOUT_THRESHOLD = 25000;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 10000)
    public void check() {
        long currentTime = System.currentTimeMillis();
        playerHeartbeats.forEach((key, lastHeartbeatTime) -> {
            if (currentTime - lastHeartbeatTime > PLAYER_TIMEOUT_THRESHOLD) {
                timeout(key);
            }
        });
    }

    public void update(String gameId, String playerId) {
        playerHeartbeats.put(gameId + "/" + playerId, System.currentTimeMillis());
    }

    private void timeout(String key) {
        var info = key.split("/");
        playerHeartbeats.remove(key);
        eventPublisher.publishEvent(new PlayerTimeoutEvent(this, info[0], info[1]));
    }
}
