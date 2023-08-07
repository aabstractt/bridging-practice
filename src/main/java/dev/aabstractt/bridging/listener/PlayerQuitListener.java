package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.manager.IslandManager;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(@NonNull PlayerQuitEvent ev) {
        Player bukkitPlayer = ev.getPlayer();

        Island island = IslandManager.getInstance().byPlayer(bukkitPlayer);
        if (island == null) {
            return;
        }

        island.getMembers().remove(bukkitPlayer.getUniqueId());

        if (!Objects.equals(bukkitPlayer.getUniqueId(), island.getOwnership())) {
            return;
        }

        // TODO: Remove the island from the cache
        // TODO: And add it to the queue to remove it
    }
}