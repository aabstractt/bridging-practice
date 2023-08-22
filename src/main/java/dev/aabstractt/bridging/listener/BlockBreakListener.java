package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.listener.BlockBreak;
import dev.aabstractt.bridging.island.listener.BridgingListener;
import dev.aabstractt.bridging.manager.IslandManager;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public final class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreakEvent(@NonNull BlockBreakEvent ev) {
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

        BlockBreak blockBreakListener = island.getBlockBreakListener();
        if (blockBreakListener == null) {
            return;
        }

        blockBreakListener.onBlockBreakEvent(ev);
    }
}