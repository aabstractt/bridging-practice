package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.player.BridgingPlayer;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(@NonNull PlayerJoinEvent ev) {
        Player bukkitPlayer = ev.getPlayer();
        if (!bukkitPlayer.isOnline()) return;

        BridgingPlayer.store(new BridgingPlayer(bukkitPlayer.getUniqueId(), bukkitPlayer.getName()));

        // TODO: Find an available island for the player to join.
        // TODO: Freeze the player until we find an island for them to join.
    }
}