package dev.aabstractt.bridging.island.schematic;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor @Data
public abstract class ModeSchematic {

    protected final @NonNull String mode;

    protected final @NonNull String originalName;

    protected final @NonNull String firstSchematicName;
    protected final @NonNull String secondSchematicName;

    public abstract void paste(@NonNull Island island);

    protected void pasteBoth(@NonNull Location firstLocation, @NonNull Location secondLocation) {
        WorldEditUtils.paste(this.firstSchematicName, firstLocation, true);
        WorldEditUtils.paste(this.secondSchematicName, secondLocation, true);
    }

    public @NonNull String getCompleteName() {
        return this.mode + "-" + this.originalName;
    }
}