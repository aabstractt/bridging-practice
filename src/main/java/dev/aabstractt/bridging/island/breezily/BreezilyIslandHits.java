package dev.aabstractt.bridging.island.breezily;

import lombok.Getter;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public enum BreezilyIslandHits {
    SOMETHING (0,0,0);
    //NO_HITS,
    //ALL(16),
    //THIRTYTWO_BLOCKS(32),
    //SIXTYFOUR_BLOCKS(64),
    //EXTREME(128);

    private final double x;
    private final double y;
    private final double z;

    BreezilyIslandHits(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BreezilyIslandHits next() {
        BreezilyIslandHits[] values = BreezilyIslandHits.values();
        try {
            return values[this.ordinal() + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return values[0];
        }
    }

    public BreezilyIslandHits previous() {
        BreezilyIslandHits[] values = BreezilyIslandHits.values();

        try {
            return values[this.ordinal() - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return values[values.length - 1];
        }
    }

    /*public Vector asVector() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int type = this.ordinal() + 1;
        switch (type) {
            case 1;
        }
        if (type == 1) {
            return new Vector(ThreadLocalRandom.current().nextDouble(-0.3, 0.3), ThreadLocalRandom.current().nextDouble(0.4, 0.65), ThreadLocalRandom.current().nextDouble(-0.3, 0.3));
        } else if (type == 2) {
            return new Vector(ThreadLocalRandom.current().nextDouble(0.15, 0.3), ThreadLocalRandom.current().nextDouble(0.4, 0.65), 0);
        } else if (type == 3) {
            return new Vector(ThreadLocalRandom.current().nextDouble(0.15, 0.3), ThreadLocalRandom.current().nextDouble(0.4, 0.65), ThreadLocalRandom.current().nextDouble(0.15, 0.3));
        } else if (type == 4) {
            return new Vector(0, ThreadLocalRandom.current().nextDouble(-0.3, 0.3), ThreadLocalRandom.current().nextDouble(0.1, 0.3));
        } else if (type == 5) {
            return new Vector(ThreadLocalRandom.current().nextDouble(0.15, 0.45), ThreadLocalRandom.current().nextDouble(0.4, 0.65), 0);
        } else if (type == 6) {
            return new Vector(ThreadLocalRandom.current().nextDouble(0.15, 0.45), ThreadLocalRandom.current().nextDouble(0.4, 0.65), ThreadLocalRandom.current().nextDouble(0.15, 0.45));
        }
        return null;

    }*/
}