package dev.aabstractt.bridging.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import dev.aabstractt.bridging.island.schematic.ModeSchematic;
import dev.aabstractt.bridging.island.schematic.impl.BreezilySchematic;
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
    private static @Nullable BukkitWorld FAWE_WORLD = null;

    private static final @NonNull Map<String, Schematic> schematics = new HashMap<>();


    public static void initializeSchematic(@NonNull String schematicName) {
        World bukkitWorld = Bukkit.getWorld("bridges");
        if (bukkitWorld == null) {
            throw new NullPointerException("Bukkit world is null");
        }

        if (FAWE_WORLD == null) {
            FAWE_WORLD = new BukkitWorld(bukkitWorld);
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

    public static void sendChunkPacket(@NonNull CraftChunk chunk, @NonNull Player... bukkitPlayers) {
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(chunk.getHandle(), true, 20);

        for (Player bukkitPlayer : bukkitPlayers) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
        }
    }

    public static @Nullable ModeSchematic wrapModeSchematic(@NonNull String mode, @NonNull String originalName) {
        String firstSchematicName = mode + "-" + originalName + "-start";
        String secondSchematicName = mode + "-" + originalName + "-end";

        return switch (mode) {
            case "breezily" -> new BreezilySchematic(mode, originalName, firstSchematicName, secondSchematicName);
            case "godbridge" -> null;
            default -> null;
        };
    }
}