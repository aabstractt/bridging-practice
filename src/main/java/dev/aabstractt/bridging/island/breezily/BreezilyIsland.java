package dev.aabstractt.bridging.island.breezily;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.listener.BreezilyBlockPlace;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.player.ModeData;
import lombok.*;

import java.util.UUID;

@Getter @Setter @EqualsAndHashCode(callSuper = true)
public final class BreezilyIsland extends Island {

    public final static @NonNull String ORIGINAL_NAME = "Breezily";

    private @NonNull BreezilyIslandDirection breezilyIslandDirection;
    private @NonNull BreezilyIslandHeight breezilyIslandHeight;
    private @NonNull BreezilyIslandHits breezilyIslandHits;

    public BreezilyIsland(
            int offset,
            @NonNull UUID uniqueId,
            @NonNull BreezilyIslandDirection breezilyIslandDirection,
            @NonNull BreezilyIslandHeight breezilyIslandHeight,
            @NonNull BreezilyIslandHits breezilyIslandHits
    ) {
        super(offset, uniqueId);

        this.breezilyIslandDirection = breezilyIslandDirection;
        this.breezilyIslandHeight = breezilyIslandHeight;
        this.breezilyIslandHits = breezilyIslandHits;

        this.listeners.add(
                new BreezilyBlockPlace()
        );
    }

    @Override
    public @NonNull String getMode() {
        return ORIGINAL_NAME;
    }

    @Override
    public void firstJoin(@NonNull ModeData modeData) {
        modeData
                .putInt("distance", 3)
                .putString("height", BreezilyIslandHeight.NORMAL.name())
                .putString("direction", BreezilyIslandDirection.NORMAL.name())
                .putString("hits", BreezilyIslandHits.SOMETHING.name())
                .putString("schematic", "breezily");
    }
}