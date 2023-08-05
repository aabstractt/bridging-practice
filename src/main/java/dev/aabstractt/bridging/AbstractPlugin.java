package dev.aabstractt.bridging;

import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.manager.SchematicManager;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public final class AbstractPlugin extends JavaPlugin {

    private static @Nullable AbstractPlugin instance = null;

    public static @NonNull AbstractPlugin getInstance() {
        if (instance == null) {
            throw new NullPointerException("AbstractPlugin instance is null");
        }

        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        IslandManager.getInstance().init();
        SchematicManager.getInstance().init();
    }
}