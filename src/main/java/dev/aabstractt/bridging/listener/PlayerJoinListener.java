package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.manager.IslandManager;
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

        BridgingPlayer bridgingPlayer = new BridgingPlayer(bukkitPlayer.getUniqueId(), bukkitPlayer.getName());
        BridgingPlayer.store(bridgingPlayer);

        // TODO: Find an available island for the player to join.
        // TODO: Freeze the player until we find an island for them to join.

        IslandManager.getInstance().findOne(bridgingPlayer, bridgingPlayer.getModeData(bridgingPlayer.getMode())).whenComplete((island, throwable) -> {
            if (throwable != null) return;

            island.membersForEach(temporarilyBridgingPlayer -> temporarilyBridgingPlayer.teleport(island.getCenter()));
        });
    }
}