package dev.aabstractt.bridging.manager;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.AbstractPlugin;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandDirection;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandHeight;
import dev.aabstractt.bridging.island.breezily.BreezilyIslandHits;
import dev.aabstractt.bridging.island.chunk.IslandChunkRestoration;
import dev.aabstractt.bridging.island.schematic.SchematicData;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.player.ModeData;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final @NonNull Map<String, SchematicData> schematicsData = new HashMap<>();

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
                SchematicData schematicData = WorldEditUtils.wrapModeSchematic(type, schematicName);
                if (schematicData == null) {
                    throw new NullPointerException("Cannot load  " + schematicName + " schematic for type " + type);
                }

                WorldEditUtils.initializeSchematic(schematicData.getFirstSchematicName());
                WorldEditUtils.initializeSchematic(schematicData.getSecondSchematicName());

                this.schematicsData.put(schematicData.getCompleteName(), schematicData);
            }
        }
    }

    public @Nullable Island byPlayer(@Nullable Player bukkitPlayer) {
        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            return null;
        }

        UUID islandUniqueId = this.islandIds.get(bukkitPlayer.getUniqueId());
        if (islandUniqueId == null) {
            return null;
        }

        return this.islandsStored.get(islandUniqueId);
    }

    public @NonNull CompletableFuture<@NonNull Island> findOne(@NonNull BridgingPlayer bridgingPlayer, @NonNull ModeData modeData) {
        Island island = this.byPlayer(bridgingPlayer.toBukkitPlayer());
        if (island != null) {
            return CompletableFuture.completedFuture(island);
        }

        SchematicData schematicData = this.getSchematicData(bridgingPlayer.getCompleteSchematicName());
        if (schematicData == null) {
            return CompletableFuture.failedFuture(new NullPointerException("Cannot find schematic " + modeData.getSchematicName() + " for mode " + modeData.getName()));
        }

        return CompletableFuture.supplyAsync(() -> {
            AtomicInteger offset = new AtomicInteger(0);

            try {
                offset.set(this.calculateAvailableOffset.call());
            } catch (Exception e) {
                throw new UnsupportedOperationException("Cannot find offset", e);
            }

            Island finalIsland = this.wrapIsland(
                    offset.get(),
                    UUID.randomUUID(), modeData
            );

            try {
                finalIsland.paste(schematicData);
            } catch (Exception e) {
                throw new UnsupportedOperationException("Cannot paste island " + finalIsland.getId(), e);
            }

            this.islandsStored.put(finalIsland.getId(), finalIsland);
            this.unavailableOffsets.add(offset.get());

            finalIsland.setOwnership(bridgingPlayer.getUniqueId());
            finalIsland.getMembers().add(bridgingPlayer.getUniqueId());

            return finalIsland;
        });
    }

    public void createIsland(@NonNull BridgingPlayer bridgingPlayer) {
        Player bukkitPlayer = bridgingPlayer.toBukkitPlayer();
        if (bukkitPlayer == null) {
            return;
        }

        bukkitPlayer.teleport(AbstractPlugin.getInstance().getSpawnLocation());

        this.findOne(
                bridgingPlayer,
                bridgingPlayer.getModeData(bridgingPlayer.getMode())
        ).whenComplete((newIsland, throwable) -> {
            if (!bukkitPlayer.isOnline()) return;

            if (!bridgingPlayer.isJoined()) {
                return;
            }

            if (throwable != null) {
                bukkitPlayer.kickPlayer("Cannot find island");

                Bukkit.getLogger().severe("Cannot find island for " + bridgingPlayer.getName());

                return;
            }

            bukkitPlayer.teleport(newIsland.getCenter());
        });
    }

    public void closeIsland(@NonNull BridgingPlayer bridgingPlayer) throws IllegalAccessException {
        Island island = this.byPlayer(bridgingPlayer.toBukkitPlayer());
        if (island == null) {
            throw new IllegalAccessException("Cannot find island for " + bridgingPlayer.getName());
        }

        this.closeIsland(island);
    }

    public void closeIsland(@NonNull Island island) throws IllegalAccessException {
        UUID ownership = island.getOwnership();
        if (ownership == null) {
            throw new IllegalAccessException("Cannot find ownership for " + island.getId());
        }

        this.islandsStored.remove(island.getId());
        this.islandIds.remove(ownership);

        this.availableOffsets.add(island.getOffset());
        this.unavailableOffsets.remove(island.getOffset());

        IslandChunkRestoration.getInstance().reset(island);

        island.membersForEach(temporarilyBridgingPlayer -> {
            if (Objects.equals(temporarilyBridgingPlayer.getUniqueId(), ownership)) {
                return;
            }

            this.createIsland(temporarilyBridgingPlayer);
        });

        island.getMembers().clear();
    }

    private @NonNull Island wrapIsland(
            int offset,
            @NonNull UUID uniqueId,
            @NonNull ModeData modeData
    ) {
        if (modeData.getName().equals(BreezilyIsland.ORIGINAL_NAME)) {
            return new BreezilyIsland(
                    offset,
                    uniqueId,
                    BreezilyIslandDirection.valueOf(modeData.getString("direction")),
                    BreezilyIslandHeight.valueOf(modeData.getString("height")),
                    BreezilyIslandHits.valueOf(modeData.getString("hits"))
            );
        }

        throw new UnsupportedOperationException("Cannot wrap island for " + modeData.getName());
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends SchematicData> T getSchematicData(@NonNull String schematicName) {
        return (T) this.schematicsData.get(schematicName);
    }
}