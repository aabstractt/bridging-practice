package dev.aabstractt.bridging.utils;

import dev.aabstractt.bridging.island.schematic.LocalSchematic;
import dev.aabstractt.bridging.island.schematic.impl.BreezilySchematic;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public final class WorldEditUtils {

    public static void primeWorldEditApi() {

    }

    public static void paste(@NonNull String schematicName, @NonNull Location location, boolean pasteWithAir) {
        primeWorldEditApi();


    }

    public void sendChunkPacket(@NonNull CraftChunk chunk, @NonNull Player... bukkitPlayers) {
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(chunk.getHandle(), true, 20);

        for (Player bukkitPlayer : bukkitPlayers) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }
    }

    public static @Nullable LocalSchematic wrapLocalSchematic(@NonNull String mode, @NonNull String originalName) {
        String firstSchematicName = originalName + "-start";
        String secondSchematicName = originalName + "-end";

        return switch (mode) {
            case "breezily" -> new BreezilySchematic(mode, originalName, firstSchematicName, secondSchematicName);
            case "godbridge" -> null;
            default -> null;
        };
    }
}