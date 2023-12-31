package dev.aabstractt.bridging.manager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import dev.aabstractt.bridging.AbstractPlugin;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import dev.aabstractt.bridging.island.chunk.PluginChunkRestoration;
import dev.aabstractt.bridging.island.schematic.SchematicData;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.player.ModeData;
import dev.aabstractt.bridging.utils.JavaUtils;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public final class IslandManager {

    @Getter private final static @NonNull IslandManager instance = new IslandManager();

    private final Map<@NonNull String, Class<? extends Island>> islandTypes = ImmutableMap.<String, Class<? extends Island>>builder()
            .put(BreezilyIsland.ORIGINAL_NAME, BreezilyIsland.class)
            .build();

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

    private final @NonNull Map<@NonNull String, @NonNull SchematicData> schematicsData = new HashMap<>();

    private final @NonNull List<@NonNull Integer> availableOffsets = new ArrayList<>();
    private final @NonNull List<@NonNull Integer> unavailableOffsets = new ArrayList<>();

    private final @NonNull Map<@NonNull UUID, @NonNull Island> islandsStored = Maps.newConcurrentMap();
    private final @NonNull Map<@NonNull UUID, @NonNull UUID> islandIds = Maps.newConcurrentMap();

    public void init() {
        ConfigurationSection mainSection = AbstractPlugin.getInstance().getConfig().getConfigurationSection("types");
        if (mainSection == null) {
            throw new NullPointerException("Cannot load types section");
        }

        for (String type : mainSection.getKeys(false)) {
            List<String> schematics = mainSection.getStringList(type);
            if (schematics == null || schematics.isEmpty()) {
                System.out.println("Not found");
                continue;
            }

            for (String schematicName : schematics) {
                SchematicData schematicData = WorldEditUtils.wrapSchematicData(type, schematicName);
                if (schematicData == null) {
                    throw new UnsupportedOperationException("Unsupported mode: " + type);
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

    /**
     *
     */
    public @NonNull CompletableFuture<@NonNull Island> findOne(@NonNull BridgingPlayer bridgingPlayer, @NonNull ModeData modeData) {
        Island island = this.byPlayer(bridgingPlayer.toBukkitPlayer());
        if (island != null) {
            return CompletableFuture.completedFuture(island);
        }

        SchematicData schematicData = this.getSchematicData(bridgingPlayer.getCompleteSchematicName());
        if (schematicData == null) {
            return JavaUtils.failedFuture(new NullPointerException("Cannot find schematic " + modeData.getSchematicName() + " for mode " + modeData.getName()));
        }

        return CompletableFuture.supplyAsync(() -> {
            Island finalIsland = this.wrapIsland(
                    UUID.randomUUID(),
                    modeData
            );

            finalIsland.setOwnership(bridgingPlayer.getUniqueId());
            finalIsland.getMembers().add(bridgingPlayer.getUniqueId());

            try {
                finalIsland.setOffset(this.calculateAvailableOffset.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (modeData.isEmpty()) {
                finalIsland.firstJoin(modeData);
            }

            Bukkit.getScheduler().runTask(AbstractPlugin.getInstance(), () -> {
                try {
                    finalIsland.load(modeData, schematicData);

                    AbstractPlugin.getInstance().getLogger().info(String.format(
                            "[Standalone Island] - %s placed at %s, %s. %s total islands",
                            finalIsland.getId(),
                            finalIsland.getOffset(),
                            finalIsland.getOffset(),
                            this.islandIds.size()
                    ));
                } catch (Exception e) {
                    e.printStackTrace();

                    throw new UnsupportedOperationException("Cannot allocate a new island", e);
                }
            });

            this.islandIds.put(bridgingPlayer.getUniqueId(), finalIsland.getId());

            this.islandsStored.put(finalIsland.getId(), finalIsland);
            this.unavailableOffsets.add(finalIsland.getOffset());

            return finalIsland;
        });
    }

    public void createIsland(@NonNull BridgingPlayer bridgingPlayer) {
        this.findOne(
                bridgingPlayer,
                bridgingPlayer.getModeData(bridgingPlayer.getMode())
        ).whenComplete((newIsland, throwable) -> {
            if (throwable != null) {
                Bukkit.getLogger().severe("Cannot find island for " + bridgingPlayer.getName() + ", reason " + throwable.getMessage());

                return;
            }

            if (!bridgingPlayer.isJoined()) {
                return;
            }

            Player bukkitPlayer = bridgingPlayer.toBukkitPlayer();
            if (bukkitPlayer == null) {
                return;
            }

            if (!bukkitPlayer.isOnline()) {
                return;
            }

            bukkitPlayer.teleport(newIsland.toBukkitLocation());
        });
    }

    public void unloadIsland(@NonNull BridgingPlayer bridgingPlayer) throws IllegalAccessException {
        Island island = this.byPlayer(bridgingPlayer.toBukkitPlayer());
        if (island == null) {
            throw new IllegalAccessException("Cannot find island for " + bridgingPlayer.getName());
        }

        this.unloadIsland(island);
    }

    public void unloadIsland(@NonNull Island island) throws IllegalAccessException {
        UUID ownership = island.getOwnership();
        if (ownership == null) {
            throw new IllegalAccessException("Cannot find ownership for " + island.getId());
        }

        this.islandsStored.remove(island.getId());
        this.islandIds.remove(ownership);

        this.availableOffsets.add(island.getOffset());
        this.unavailableOffsets.remove(island.getOffset());

        PluginChunkRestoration.getInstance().reset(island);

        island.membersForEach(bridgingPlayer -> {
            if (Objects.equals(bridgingPlayer.getUniqueId(), ownership)) {
                return;
            }

            this.createIsland(bridgingPlayer);
        });

        island.unload();
    }

    private @NonNull Island wrapIsland(
            @NonNull UUID uniqueId,
            @NonNull ModeData modeData
    ) {
        Class<? extends Island> clazz = this.islandTypes.get(modeData.getName());
        if (clazz == null) {
            throw new UnsupportedOperationException("Cannot wrap island for " + modeData.getName());
        }

        try {
            return clazz.getDeclaredConstructor(UUID.class).newInstance(uniqueId);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends SchematicData> T getSchematicData(@NonNull String schematicName) {
        return (T) this.schematicsData.get(schematicName);
    }
}