package dev.aabstractt.bridging.player;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor @Data
public final class BridgingPlayer {

    private final static @NonNull Map<UUID, BridgingPlayer> bridgingPlayersStored = Maps.newConcurrentMap();

    private final @NonNull UUID uniqueId;
    private final @NonNull String name;

    private @NonNull String mode = BreezilyIsland.ORIGINAL_NAME;

    private final @NonNull Set<ModeData> modesData = new ConcurrentSet<>();

    private int islandId = 0;

    public @NonNull String getCompleteSchematicName() {
        return this.mode + "-" + this.getModeData(this.mode).getSchematicName();
    }

    public @Nullable Player toBukkitPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    public void teleport(@NonNull Location location) {
        Player bukkitPlayer = this.toBukkitPlayer();
        if (bukkitPlayer == null) return;

        bukkitPlayer.teleport(location);
    }

    public @NonNull ModeData getModeData(@NonNull String mode) {
        ModeData modeData = this.modesData.stream()
                .filter(temporarilyModeData -> temporarilyModeData.getName().equals(mode))
                .findFirst().orElse(null);

        if (modeData != null) return modeData;

        modeData = new ModeData(mode, "default", new ConcurrentHashMap<>());
        this.modesData.add(modeData);

        return modeData;
    }

    public static @Nullable BridgingPlayer byPlayer(@NonNull Player bukkitPlayer) {
        return byId(bukkitPlayer.getUniqueId());
    }

    public static @Nullable BridgingPlayer byId(@NonNull UUID uniqueId) {
        return bridgingPlayersStored.get(uniqueId);
    }

    public static void store(@NonNull BridgingPlayer bridgingPlayer) {
        bridgingPlayersStored.put(bridgingPlayer.getUniqueId(), bridgingPlayer);
    }
}