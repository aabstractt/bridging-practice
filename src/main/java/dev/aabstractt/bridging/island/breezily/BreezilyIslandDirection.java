package dev.aabstractt.bridging.island.breezily;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * don't remove this messages and
 * give me the credits. Arigato! n.n
 */
public enum BreezilyIslandDirection {
    NORMAL,
    SEMI_DIAGONAL,
    DIAGONAL;

    public int getDistanceFor(int distance) {
        if (this.equals(NORMAL)) {
            return 0;
        } else {
            if (this.equals(SEMI_DIAGONAL)) {
                return distance / 2;
            } else {
                return distance;
            }
        }
    }

    public BreezilyIslandDirection next() {
        if (this.equals(BreezilyIslandDirection.NORMAL)) {
            return SEMI_DIAGONAL;
        } else if (this.equals(BreezilyIslandDirection.SEMI_DIAGONAL)) {
            return DIAGONAL;
        } else {
            return NORMAL;
        }
    }

    public BreezilyIslandDirection before() {
        if (this.equals(BreezilyIslandDirection.DIAGONAL)) {
            return SEMI_DIAGONAL;
        } else if (this.equals(BreezilyIslandDirection.SEMI_DIAGONAL)) {
            return NORMAL;
        } else {
            return DIAGONAL;
        }
    }
}