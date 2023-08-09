package dev.aabstractt.bridging.command.admin;

import dev.aabstractt.bridging.command.Argument;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.chunk.PluginChunkRestoration;
import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.utils.Messages;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public final class ResetArgument extends Argument {

    public ResetArgument(@NonNull String name, @NonNull String usage, int minArgs, @Nullable String permission) {
        super(name, usage, minArgs, permission);
    }

    @Override
    public void execute(@NonNull CommandSender commandSender, @NonNull String[] args) {
        if (!(commandSender instanceof Player) && args.length == 0) {
            commandSender.sendMessage("Usage: " + this.getUsage());

            return;
        }

        Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) commandSender;
        if (target == null) {
            commandSender.sendMessage(Messages.PLAYER_NOT_FOUND.build(args[0]));

            return;
        }

        if (!target.equals(commandSender) && !commandSender.hasPermission("island.admin.reset.others")) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to reset other players' islands.");

            return;
        }

        Island island = IslandManager.getInstance().byPlayer(target);
        if (island == null) {
            commandSender.sendMessage(Messages.PLAYER_MUST_BE_ON_ISLAND.build(target.getName()));

            return;
        }

        island.setUpdating(true);

        // TODO: Bad idea due to two for each loops unnecessarily when we can just do one.
        Location center = island.getCenter();
        island.membersForEach(bridgingPlayer -> bridgingPlayer.teleport(center));
        island.broadcast(Messages.ADMIN_RESET_SUCCESS.build());

        try {
            PluginChunkRestoration.getInstance().reset(island);

            commandSender.sendMessage(Messages.ADMIN_RESET_SUCCESS.build(
                    target.getName(),
                    String.valueOf(center.getBlockX()),
                    String.valueOf(center.getBlockZ())
            ));

            island.setUpdating(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}