package dev.aabstractt.bridging.manager;

import lombok.Getter;
import lombok.NonNull;

public final class SchematicManager {

    @Getter private final static @NonNull SchematicManager instance = new SchematicManager();

    public void init() {

    }
}