package dev.aabstractt.bridging.island.chunk;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

public final class PluginChunkRestoration {

    @Getter private static final @NonNull PluginChunkRestoration instance = new PluginChunkRestoration();

    private final @NonNull PluginChunkReset pluginChunkReset = new PluginChunkReset();

    public void copy(@NonNull Island island) throws IllegalAccessException {
        Cuboid cuboid = island.getCuboid();
        if (cuboid == null) {
            throw new IllegalAccessException("Bridging cannot copy the current chunks without an Island initialized");
        }

        long startTime = System.currentTimeMillis();

        for (Chunk chunk : cuboid.getChunks()) {
            chunk.load();

            island.registerChunkSections(
                    PluginChunkReset.chunkHash(chunk),
                    this.pluginChunkReset.cloneSections(((CraftChunk) chunk).getHandle().getSections())
            );
        }

        System.out.println("Chunks copied! (" + (System.currentTimeMillis() - startTime) + "ms)");
    }

    public void reset(@NonNull Island island) throws IllegalAccessException {
        Cuboid cuboid = island.getCuboid();
        if (cuboid == null) {
            throw new IllegalAccessException("Bridging cannot reset the chunks without an Island initialized");
        }

        long startTime = System.currentTimeMillis();

        for (Chunk chunk : cuboid.getChunks()) {
            chunk.load();

            ChunkSection[] chunkSections = island.getChunkSections(PluginChunkReset.chunkHash(chunk));
            if (chunkSections == null) {
                continue;
            }

            this.pluginChunkReset.setSections(
                    ((CraftChunk) chunk).getHandle(),
                    this.pluginChunkReset.cloneSections(chunkSections)
            );

            chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ()); // let the mf server know that you've updated the chunk.
        }

        System.out.println("Chunks have been reset! (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }
}