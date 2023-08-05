package dev.aabstractt.bridging.island.breezily;

import dev.aabstractt.bridging.island.Island;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Data @EqualsAndHashCode(callSuper = true)
public final class BreezilyIsland extends Island {

    private @NonNull BreezilyIslandDirection breezilyIslandDirection;
    private @NonNull BreezilyIslandHeight breezilyIslandHeight;
    private @NonNull BreezilyIslandHits breezilyIslandHits;
}