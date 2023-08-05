package dev.aabstractt.bridging.island.schematic.impl;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import dev.aabstractt.bridging.island.schematic.LocalSchematic;
import lombok.NonNull;

public final class BreezilySchematic extends LocalSchematic {

    public BreezilySchematic(@NonNull String mode, @NonNull String originalName, @NonNull String firstSchematicName, @NonNull String secondSchematicName) {
        super(mode, originalName, firstSchematicName, secondSchematicName);
    }

    @Override
    public void paste(@NonNull Island island) {
        if (!(island instanceof BreezilyIsland)) {
            throw new IllegalArgumentException("Island must be a BreezilyIsland");
        }

        int distance = island.getDistance();
        if (distance == 0) {
            throw new IllegalArgumentException("Island must have a distance");
        }

        this.pasteBoth(
                island.getCenter(),
                island.getCenter().add(
                        ((BreezilyIsland) island).getBreezilyIslandDirection().getDistance(distance),
                        ((BreezilyIsland) island).getBreezilyIslandHeight().getDelta(),
                        distance
                )
        );
    }
}