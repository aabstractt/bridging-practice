package dev.aabstractt.bridging.island.breezily;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.breezily.listener.BreezilyBlockPlace;
import dev.aabstractt.bridging.player.ModeData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @EqualsAndHashCode(callSuper = true)
public final class BreezilyIsland extends Island {

    public final static @NonNull String ORIGINAL_NAME = "Breezily";

    private @NonNull BreezilyIslandDirection breezilyIslandDirection = BreezilyIslandDirection.NORMAL;
    private @NonNull BreezilyIslandHeight breezilyIslandHeight = BreezilyIslandHeight.NORMAL;
    private @NonNull BreezilyIslandHits breezilyIslandHits = BreezilyIslandHits.SOMETHING;

    public BreezilyIsland(@NonNull UUID uniqueId) {
        super(uniqueId);

        this.blockPlaceListener = new BreezilyBlockPlace();
    }

    @Override
    public void firstJoin(@NonNull ModeData modeData) {
        modeData.putInt("distance", 3)
                .putString("height", BreezilyIslandHeight.NORMAL.name())
                .putString("direction", BreezilyIslandDirection.NORMAL.name())
                .putString("hits", BreezilyIslandHits.SOMETHING.name());
    }

    @Override
    public void load(@NonNull ModeData modeData) {
        this.breezilyIslandDirection = BreezilyIslandDirection.valueOf(modeData.getString("direction"));
        this.breezilyIslandHeight = BreezilyIslandHeight.valueOf(modeData.getString("height"));
        this.breezilyIslandHits = BreezilyIslandHits.valueOf(modeData.getString("hits"));

        super.load(modeData);
    }
}