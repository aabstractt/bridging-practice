package dev.aabstractt.bridging.manager;

import dev.aabstractt.bridging.AbstractPlugin;
import dev.aabstractt.bridging.island.chunk.IslandChunkRestoration;
import dev.aabstractt.bridging.island.schematic.LocalSchematic;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class IslandManager {

    @Getter private final static @NonNull IslandManager instance = new IslandManager();

    private final @NonNull IslandChunkRestoration chunkRestoration = new IslandChunkRestoration();

    private final @NonNull Map<String, LocalSchematic> bridgingSchematics = new HashMap<>();

    public void init() {
        ConfigurationSection mainSection = AbstractPlugin.getInstance().getConfig().getConfigurationSection("types");
        if (mainSection == null) {
            throw new NullPointerException("Cannot load types section");
        }

        for (String type : mainSection.getKeys(false)) {
            List<String> schematics = mainSection.getStringList(type);
            if (schematics == null) continue;
            if (schematics.isEmpty()) continue;

            for (String schematicName : schematics) {
                LocalSchematic localSchematic = WorldEditUtils.wrapLocalSchematic(type, schematicName);
                if (localSchematic == null) {
                    throw new NullPointerException("Cannot load  " + schematicName + " schematic for type " + type);
                }

                this.bridgingSchematics.put(schematicName, localSchematic);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T extends LocalSchematic> T getBridgingSchematic(@NonNull String schematicName) {
        return (T) this.bridgingSchematics.get(schematicName);
    }
}