package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public final class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(@NonNull PlayerJoinEvent ev) {
        Player bukkitPlayer = ev.getPlayer();
        if (!bukkitPlayer.isOnline()) return;

        BridgingPlayer bridgingPlayer = BridgingPlayer.byPlayer(bukkitPlayer);
        if (bridgingPlayer == null) return;

        bridgingPlayer.setJoined(true);

        Island island = IslandManager.getInstance().byPlayer(bukkitPlayer);
        if (island == null) {
            return;
        }

        if (!Objects.equals(island.getOwnership(), bukkitPlayer.getUniqueId())) {
            return;
        }

        Cuboid cuboid = island.getCuboid();
        if (cuboid == null) {
            return;
        }

        if (cuboid.contains(bukkitPlayer.getLocation())) {
            return;
        }

        bukkitPlayer.teleport(island.getCenter());
    }
}