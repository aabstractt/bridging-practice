package dev.aabstractt.bridging.island.listener;

import lombok.NonNull;
import org.bukkit.event.player.PlayerMoveEvent;

public interface PlayerMove extends BridgingListener {

    void onPlayerMoveEvent(@NonNull PlayerMoveEvent ev);
}