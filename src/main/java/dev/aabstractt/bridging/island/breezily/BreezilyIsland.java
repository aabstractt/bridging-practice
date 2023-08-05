package dev.aabstractt.bridging.island.breezily;

import dev.aabstractt.bridging.island.Island;
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
    }

    @Override
    public @NonNull String getMode() {
        return ORIGINAL_NAME;
    }
}