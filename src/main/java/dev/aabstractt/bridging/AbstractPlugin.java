package dev.aabstractt.bridging;

import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.manager.SchematicManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public final class AbstractPlugin extends JavaPlugin {

    private static @Nullable AbstractPlugin instance = null;

    @Nullable
    public static AbstractPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        IslandManager.getInstance().init();
        SchematicManager.getInstance().init();
    }
}