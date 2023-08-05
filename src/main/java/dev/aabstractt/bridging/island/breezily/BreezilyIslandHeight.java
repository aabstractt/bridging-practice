package dev.aabstractt.bridging.island.breezily;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public enum BreezilyIslandHeight {
    NORMAL(0),
    SIXTEEN_BLOCKS(16),
    THIRTYTWO_BLOCKS(32),
    SIXTYFOUR_BLOCKS(64),
    EXTREME(128);

    private final int delta;

    public BreezilyIslandHeight next() {
        BreezilyIslandHeight[] values = BreezilyIslandHeight.values();
        try {
            return values[this.ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return values[0];
        }
    }

    public BreezilyIslandHeight previous() {
        BreezilyIslandHeight[] values = BreezilyIslandHeight.values();

        try {
            return values[this.ordinal() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return values[values.length - 1];
        }
    }
}