package dev.aabstractt.bridging.manager;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.AbstractPlugin;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandDirection;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandHeight;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandHits;
import dev.aabstractt.bridging.island.schematic.LocalSchematic;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class IslandManager {

    @Getter private final static @NonNull IslandManager instance = new IslandManager();

    private final @NonNull Callable<Integer> calculateAvailableOffset = () -> {
        if (!this.availableOffsets.isEmpty()) {
            return this.availableOffsets.remove(0);
        }

        int offset = 500;
        while (this.unavailableOffsets.contains(offset)) {
            offset += 500;
        }

        return offset;
    };

    private final @NonNull Map<String, LocalSchematic> bridgingSchematics = new HashMap<>();

    private final @NonNull List<@NonNull Integer> availableOffsets = new ArrayList<>();
    private final @NonNull List<Integer> unavailableOffsets = new ArrayList<>();

    private final @NonNull Map<UUID, Island> islandsStored = Maps.newConcurrentMap();
    private final @NonNull Map<UUID, UUID> islandIds = Maps.newConcurrentMap();

    public void init() {
        ConfigurationSection mainSection = AbstractPlugin.getInstance().getConfig().getConfigurationSection("types");
        if (mainSection == null) {
            throw new NullPointerException("Cannot load types section");
        }

        for (String type : mainSection.getKeys(false)) {
            List<String> schematics = mainSection.getStringList(type);
            if (schematics == null) continue;
            if (schematics.isEmpty()) continue;

            for (String schematicName : schematics) {
                LocalSchematic localSchematic = WorldEditUtils.wrapLocalSchematic(type, schematicName);
                if (localSchematic == null) {
                    throw new NullPointerException("Cannot load  " + schematicName + " schematic for type " + type);
                }

                this.bridgingSchematics.put(schematicName, localSchematic);
            }
        }
    }

    public @NonNull CompletableFuture<@NonNull Island> findOne(@NonNull BridgingPlayer bridgingPlayer) {
        UUID islandUniqueId = this.islandIds.get(bridgingPlayer.getUniqueId());

        Island island = islandUniqueId != null ? this.islandsStored.get(islandUniqueId) : null;
        if (island != null) return CompletableFuture.completedFuture(island);

        LocalSchematic localSchematic = this.getBridgingSchematic(bridgingPlayer.getSchematicName());
        if (localSchematic == null) return CompletableFuture.failedFuture(new NullPointerException("Cannot find schematic " + bridgingPlayer.getSchematicName()));

        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger offset = new AtomicInteger(0);

            try {
                offset.set(this.calculateAvailableOffset.call());
            } catch (Exception e) {
                throw new UnsupportedOperationException("Cannot find offset", e);
            }

            Island finalIsland = this.wrapIsland(offset.get(), UUID.randomUUID(), bridgingPlayer, localSchematic);

            try {
                finalIsland.paste(localSchematic);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Cannot paste island " + finalIsland.getId(), e);
            }

            this.islandsStored.put(finalIsland.getId(), finalIsland);
            this.unavailableOffsets.add(offset.get());

            return finalIsland;
        });
    }

    private @NonNull Island wrapIsland(
            int offset,
            @NonNull UUID uniqueId,
            @NonNull BridgingPlayer bridgingPlayer,
            @NonNull LocalSchematic localSchematic
    ) {
        if (localSchematic.getMode().equals(BreezilyIsland.ORIGINAL_NAME)) {
            return new BreezilyIsland(
                    offset,
                    uniqueId,
                    BreezilyIslandDirection.NORMAL,
                    BreezilyIslandHeight.NORMAL,
                    BreezilyIslandHits.SOMETHING
            );
        }

        throw new NullPointerException("Cannot wrap island for schematic " + localSchematic.getOriginalName());
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends LocalSchematic> T getBridgingSchematic(@NonNull String schematicName) {
        return (T) this.bridgingSchematics.get(schematicName);
    }
}