package dev.aabstractt.bridging;

import dev.aabstractt.bridging.command.BaseCommandExecutor;
import dev.aabstractt.bridging.command.admin.ResetArgument;
import dev.aabstractt.bridging.listener.*;
import dev.aabstractt.bridging.manager.IslandManager;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        this.saveDefaultConfig();

        IslandManager.getInstance().init();

        this.getServer().getPluginCommand("islandadmin").setExecutor(new BaseCommandExecutor("/<label> help")
                .addArgument(new ResetArgument("reset", "/<label> reset <player>", 2, "island.admin.reset"))
        );

        com.google.common.base.Throwables.

        this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    public @NonNull Location getSpawnLocation() {
        return Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    public static @NonNull String getDefaultSchematicName() {
        return getInstance().getConfig().getString("default-schematic", "default");
    }

    public static @NonNull String getDefaultMode() {
        return getInstance().getConfig().getString("default-mode", "solo");
    }

    public static boolean isSingleServer() {
        return !getInstance().getConfig().getBoolean("bungee-mode", true);
    }
}