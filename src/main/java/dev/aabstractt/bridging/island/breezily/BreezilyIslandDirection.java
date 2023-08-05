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
        return switch (this) {
            case NORMAL -> SEMI_DIAGONAL;
            case SEMI_DIAGONAL -> DIAGONAL;
            default -> NORMAL;
        };
    }

    public BreezilyIslandDirection before() {
        return switch (this) {
            case DIAGONAL -> SEMI_DIAGONAL;
            case SEMI_DIAGONAL -> NORMAL;
            default -> DIAGONAL;
        };
    }
}