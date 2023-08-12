package dev.aabstractt.bridging.island.breezily;

public enum BreezilyIslandDirection {
    NORMAL,
    SEMI_DIAGONAL,
    DIAGONAL;

    public int getDistance(int distance) {
        if (this.equals(NORMAL)) return 0;
        if (this.equals(SEMI_DIAGONAL)) return distance / 2;

        return distance;
    }

    public BreezilyIslandDirection next() {
        if (this.equals(NORMAL)) return SEMI_DIAGONAL;
        if (this.equals(SEMI_DIAGONAL)) return DIAGONAL;

        return NORMAL;
    }

    public BreezilyIslandDirection before() {
        if (this.equals(DIAGONAL)) return SEMI_DIAGONAL;
        if (this.equals(SEMI_DIAGONAL)) return NORMAL;

        return DIAGONAL;
    }
}