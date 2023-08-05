package dev.aabstractt.bridging.island.breezily;

import lombok.Getter;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public enum BreezilyIslandHeight {
    NORMAL(0),
    SIXTEEN_BLOCKS(16),
    THIRTYTWO_BLOCKS(32),
    SIXTYFOUR_BLOCKS(64),
    EXTREME(128);

    @Getter
    private final int delta;

    BreezilyIslandHeight(int delta) {
        this.delta = delta;
    }

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