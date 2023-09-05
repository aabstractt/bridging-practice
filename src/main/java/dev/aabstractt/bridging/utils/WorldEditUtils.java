package dev.aabstractt.bridging.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import dev.aabstractt.bridging.island.schematic.SchematicData;
import dev.aabstractt.bridging.island.schematic.impl.BreezilySchematicData;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class WorldEditUtils {

    private final static @NonNull File SCHEMATICS_FILE = new File(WorldEditPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(),"schematics");
    public static @Nullable World BUKKIT_WORLD = null;
    private static @Nullable BukkitWorld FAWE_WORLD = null;

    @Getter private static final @NonNull Map<String, Schematic> schematics = new HashMap<>();

    public static void initializeSchematic(@NonNull String schematicName) {
        BUKKIT_WORLD = Bukkit.getWorld("bridges");
        if (BUKKIT_WORLD == null) {
            throw new NullPointerException("Bukkit world is null");
        }

        if (FAWE_WORLD == null) {
            FAWE_WORLD = new BukkitWorld(BUKKIT_WORLD);
        }

        if (!SCHEMATICS_FILE.exists()) {
            throw new NullPointerException("Schematics file does not exist");
        }

        File file = new File(SCHEMATICS_FILE, schematicName + ".schematic");
        if (!file.exists()) {
            throw new NullPointerException("Schematic file does not exist");
        }

        try {
            ClipboardFormat clipboardFormat = ClipboardFormat.findByFile(file);
            if (clipboardFormat == null) {
                throw new NullPointerException("Clipboard format is null");
            }

            schematics.put(schematicName, clipboardFormat.load(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void paste(@NonNull String schematicName, @NonNull Location location, boolean pasteWithAir) {
        if (FAWE_WORLD == null) {
            throw new NullPointerException("FAWE world is null");
        }

        Schematic schematic = schematics.get(schematicName);
        if (schematic == null) {
            throw new NullPointerException("Schematic is null");
        }

        schematic.paste(
                FAWE_WORLD,
                new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                pasteWithAir
        );
    }

    public static @NonNull Clipboard getClipboard(@NonNull String schematicName) {
        if (FAWE_WORLD == null) {
            throw new NullPointerException("FAWE world is null");
        }

        Schematic schematic = schematics.get(schematicName);
        if (schematic == null) {
            throw new NullPointerException("Schematic " + schematicName + " is null");
        }

        Clipboard clipboard = schematic.getClipboard();
        if (clipboard == null) {
            throw new NullPointerException("Clipboard is null");
        }

        return clipboard;
    }

    public static void sendChunkPacket(@NonNull CraftChunk chunk, @NonNull Player... bukkitPlayers) {
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(chunk.getHandle(), true, 20);

        for (Player bukkitPlayer : bukkitPlayers) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }
    }

    public static @Nullable SchematicData wrapSchematicData(@NonNull String mode, @NonNull String originalName) {
        String firstSchematicName = mode + "-" + originalName + "-start";
        String secondSchematicName = mode + "-" + originalName + "-end";

        if (mode.equals("Breezily")) {
            return new BreezilySchematicData(
                    mode,
                    originalName,
                    firstSchematicName,
                    secondSchematicName
            );
        }

        return null;
    }

    public static @NonNull Cuboid wrapCuboid(@NonNull Location center, @NonNull Vector origin, @NonNull Vector min, @NonNull Vector max) {
        return new Cuboid(
                center.clone().add(min.getBlockX() - origin.getBlockX(), 0, min.getBlockZ() - origin.getBlockZ()),
                center.clone().add(max.getBlockX() - origin.getBlockX(), 200, max.getBlockZ() - origin.getBlockZ())
        );
    }
}