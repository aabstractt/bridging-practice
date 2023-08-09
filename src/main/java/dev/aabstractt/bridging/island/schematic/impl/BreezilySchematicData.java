package dev.aabstractt.bridging.island.schematic.impl;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.BreezilyIsland;
import dev.aabstractt.bridging.island.schematic.SchematicData;
import dev.aabstractt.bridging.player.BridgingPlayer;
import lombok.NonNull;

import java.util.UUID;

public final class BreezilySchematicData extends SchematicData {

    public BreezilySchematicData(@NonNull String mode, @NonNull String originalName, @NonNull String firstSchematicName, @NonNull String secondSchematicName) {
        super(mode, originalName, firstSchematicName, secondSchematicName);
    }

    @Override
    public void paste(@NonNull Island island) {
        if (!(island instanceof BreezilyIsland)) {
            throw new IllegalArgumentException("Island must be a BreezilyIsland");
        }

        UUID ownership = island.getOwnership();
        if (ownership == null) {
            throw new IllegalArgumentException("Island must have an ownership");
        }

        BridgingPlayer bridgingPlayer = BridgingPlayer.byId(ownership);
        if (bridgingPlayer == null) {
            throw new IllegalArgumentException("Island must have a valid ownership");
        }

        int distance = bridgingPlayer.getModeData(bridgingPlayer.getMode()).getInt("distance");
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