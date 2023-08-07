package dev.aabstractt.bridging.island.listener;

import lombok.NonNull;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreak extends BridgingListener {

    void onBlockBreakEvent(@NonNull BlockBreakEvent ev);
}