package dev.aabstractt.bridging.island;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.island.chunk.IslandChunkRestoration;
import dev.aabstractt.bridging.island.listener.BridgingListener;
import dev.aabstractt.bridging.island.schematic.ModeSchematic;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor @Data
public abstract class Island {

    protected final int offset;
    protected final @NonNull UUID id;

    protected @Nullable Location center = null;
    protected @Nullable Cuboid cuboid = null;

    protected @Nullable String schematicName = null;
    protected @Nullable UUID ownership = null;

    protected boolean updating = false;

    protected int distance = 0;

    protected @NonNull Set<@NonNull UUID> members = new ConcurrentSet<>();

    protected final @NonNull Set<@NonNull BridgingListener> listeners = new HashSet<>();
    protected final @NonNull Map<String, ChunkSection[]> chunks = Maps.newConcurrentMap();

    public abstract @NonNull String getMode();

    public void paste(@NonNull ModeSchematic modeSchematic) throws IllegalAccessException {
        if (this.schematicName == null) {
            throw new IllegalArgumentException("Island must have a schematic name");
        }

        IslandChunkRestoration.getInstance().copy(this);

        modeSchematic.paste(this);
    }

    public void registerChunkSections(@NonNull String chunkHash, @NonNull ChunkSection[] chunkSections) {
        this.chunks.put(chunkHash, chunkSections);
    }

    public @NonNull ChunkSection[] getChunkSections(@NonNull String chunkHash) {
        return this.chunks.getOrDefault(chunkHash, new ChunkSection[0]);
    }

    public @NonNull Location getCenter() {
        if (this.center == null) {
            throw new IllegalArgumentException("Island must have a center");
        }

        return this.center.clone();
    }

    public void membersForEach(@NonNull Consumer<@NonNull BridgingPlayer> consumer) {
        for (UUID uuidParsed : this.members) {
            BridgingPlayer bridgingPlayer = BridgingPlayer.byId(uuidParsed);
            if (bridgingPlayer == null) {
                this.members.remove(uuidParsed);

                continue;
            }

            consumer.accept(bridgingPlayer);
        }
    }

    public boolean isInsideCuboid(@NonNull Location bukkitLocation) {
        return this.cuboid != null && this.cuboid.contains(bukkitLocation);
    }
}