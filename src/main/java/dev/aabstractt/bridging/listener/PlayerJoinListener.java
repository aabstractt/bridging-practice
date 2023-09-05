package dev.aabstractt.bridging.listener;

import dev.aabstractt.bridging.AbstractPlugin;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.NonNull;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
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
        if (bridgingPlayer == null) {
            bukkitPlayer.kickPlayer("An error occurred while loading your data. Please rejoin.");

            return;
        }

        bridgingPlayer.setJoined(true);

        if (AbstractPlugin.isSingleServer()) {
            return;
        }

        Island island = IslandManager.getInstance().byPlayer(bukkitPlayer);
        if (island == null) {
            System.out.println("We not have island!");
            return;
        }

        if (!Objects.equals(island.getOwnership(), bukkitPlayer.getUniqueId())) {
            System.out.println("Non owner!");
            return;
        }

        if (island.isInsideCuboid(bukkitPlayer.getLocation())) {
            System.out.println("Im inside!");
            return;
        }

        System.out.println("teleporting");
        bukkitPlayer.teleport(island.toBukkitLocation());

        WorldEditUtils.sendChunkPacket(
                bukkitPlayer.getWorld().getChunkAt(island.toBukkitLocation()),
                bukkitPlayer
        );
    }
}