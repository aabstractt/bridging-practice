package dev.aabstractt.bridging.island;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.island.chunk.IslandChunkRestoration;
import dev.aabstractt.bridging.island.schematic.LocalSchematic;
import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public abstract class Island {

    protected @Nullable Location center = null;
    protected @Nullable Cuboid cuboid = null;

    protected @Nullable String schematicName = null;
    protected int distance = 0;

    protected @NonNull Set<@NonNull UUID> members = new ConcurrentSet<>();

    protected final @NonNull Map<String, ChunkSection[]> chunks = Maps.newConcurrentMap();

    public void paste() throws IllegalAccessException {
        if (this.schematicName == null) {
            throw new IllegalArgumentException("Island must have a schematic name");
        }

        if (this.center == null) {
            throw new IllegalArgumentException("Island must have a center");
        }

        LocalSchematic localSchematic = IslandManager.getInstance().getBridgingSchematic(this.schematicName);
        if (localSchematic == null) {
            throw new NullPointerException("Cannot load " + this.schematicName + " schematic");
        }

        IslandChunkRestoration.getInstance().copy(this);

        localSchematic.paste(this);

        this.membersForEach(bridgingPlayer -> bridgingPlayer.teleport(this.center));
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
}