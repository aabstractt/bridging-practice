package dev.aabstractt.bridging.island.listener;

import lombok.NonNull;
import org.bukkit.event.block.BlockPlaceEvent;

public interface BlockPlace extends BridgingListener {

    void onBlockPlaceEvent(@NonNull BlockPlaceEvent ev);
}