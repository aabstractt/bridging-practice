package dev.aabstractt.bridging.utils;

import lombok.NonNull;
import org.bukkit.ChatColor;

public enum Messages {

    PLAYER_NOT_FOUND(),
    PLAYER_MUST_BE_ON_ISLAND(),
    ADMIN_RESTORED_YOUR_ISLAND(),
    ADMIN_RESET_SUCCESS();

    public @NonNull String build(@NonNull String... args) {
        return ChatColor.translateAlternateColorCodes('&', "&fNo translate &a" + this.name());
    }
}