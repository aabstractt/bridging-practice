package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.player.BridgingPlayer;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public final class AsyncPlayerPreLoginListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(@NonNull AsyncPlayerPreLoginEvent ev) {
        if (!ev.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }

        BridgingPlayer bridgingPlayer = new BridgingPlayer(ev.getUniqueId(), ev.getName());
        BridgingPlayer.store(bridgingPlayer);

        IslandManager.getInstance().findOne(bridgingPlayer, bridgingPlayer.getModeData(bridgingPlayer.getMode())).whenComplete((island, throwable) -> {
            if (throwable != null) {
                return;
            }

            if (!bridgingPlayer.isJoined()) {
                return;
            }

            island.membersForEach(temporarilyBridgingPlayer -> temporarilyBridgingPlayer.teleport(island.getCenter()));
        });
    }
}