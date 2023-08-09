package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.listener.BlockBreak;
import dev.aabstractt.bridging.island.listener.BlockPlace;
import dev.aabstractt.bridging.island.listener.BridgingListener;
import dev.aabstractt.bridging.manager.IslandManager;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public final class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(@NonNull BlockPlaceEvent ev) {
        Player bukkitPlayer = ev.getPlayer();
        if (!bukkitPlayer.isOnline()) {
            return;
        }

        Island island = IslandManager.getInstance().byPlayer(bukkitPlayer);
        if (island == null) {
            return;
        }

        if (!island.isInsideCuboid(bukkitPlayer.getLocation())) {
            ev.setCancelled(true);

            return;
        }

        if (island.isUpdating()) {
            ev.setCancelled(true);

            return;
        }

        for (BridgingListener bridgingListener : island.getListeners()) {
            if (!(bridgingListener instanceof BlockPlace)) continue;

            ((BlockPlace) bridgingListener).onBlockPlaceEvent(ev);
        }
    }
}