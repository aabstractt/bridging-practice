package dev.aabstractt.bridging.island;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Map;

@Data
public abstract class Island {

    protected @Nullable Location center = null;
    protected @Nullable Cuboid cuboid = null;

    protected int distance = 0;

    protected final @NonNull Map<String, ChunkSection[]> chunks = Maps.newConcurrentMap();

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
}