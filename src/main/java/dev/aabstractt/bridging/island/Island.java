package dev.aabstractt.bridging.island;

import com.google.common.collect.Maps;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChunkSection;

import javax.annotation.Nullable;
import java.util.Map;

@Data
public final class Island {

    private @Nullable Cuboid cuboid = null;

    private final @NonNull Map<String, ChunkSection[]> chunks = Maps.newConcurrentMap();

    public void registerChunkSections(@NonNull String chunkHash, @NonNull ChunkSection[] chunkSections) {
        this.chunks.put(chunkHash, chunkSections);
    }

    public @NonNull ChunkSection[] getChunkSections(@NonNull String chunkHash) {
        return this.chunks.getOrDefault(chunkHash, new ChunkSection[0]);
    }
}