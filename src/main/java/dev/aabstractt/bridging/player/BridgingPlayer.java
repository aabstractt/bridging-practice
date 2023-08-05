package dev.aabstractt.bridging.player;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor @Data
public final class BridgingPlayer {

    private final static @NonNull Map<UUID, BridgingPlayer> bridgingPlayersStored = Maps.newConcurrentMap();

    private @NonNull UUID uniqueId;
    private @NonNull String name;

    private int islandId = 0;

    public static @Nullable BridgingPlayer byPlayer(@NonNull Player bukkitPlayer) {
        return bridgingPlayersStored.get(bukkitPlayer.getUniqueId());
    }

    public static void store(@NonNull BridgingPlayer bridgingPlayer) {
        bridgingPlayersStored.put(bridgingPlayer.getUniqueId(), bridgingPlayer);
    }
}