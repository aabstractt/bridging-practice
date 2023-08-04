package dev.aabstractt.bridging.manager;

import dev.aabstractt.bridging.island.chunk.IslandChunkRestoration;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
public final class IslandManager {

    @Getter private final static @NonNull IslandManager instance = new IslandManager();

    private final @NonNull IslandChunkRestoration chunkRestoration = new IslandChunkRestoration();

    public void init() {

    }

    public void sendChunkPacket(@NonNull CraftChunk chunk, @NonNull Player... bukkitPlayers) {
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(chunk.getHandle(), true, 20);

        for (Player bukkitPlayer : bukkitPlayers) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }
    }
}