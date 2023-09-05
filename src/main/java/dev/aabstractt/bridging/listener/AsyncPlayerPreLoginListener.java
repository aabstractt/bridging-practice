package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.AbstractPlugin;
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
            System.out.println("Non allowed login result");
            return;
        }

        System.out.println("Storing player");

        BridgingPlayer bridgingPlayer = new BridgingPlayer(ev.getUniqueId(), ev.getName());
        BridgingPlayer.store(bridgingPlayer);

        if (AbstractPlugin.isSingleServer()) {
            System.out.println("Single server");
            return;
        }

        System.out.println("Creating island");

        IslandManager.getInstance().createIsland(bridgingPlayer);
    }
}