package dev.aabstractt.bridging.command.admin;

import dev.aabstractt.bridging.command.Argument;
import dev.aabstractt.bridging.island.Island;
import dev.aabstractt.bridging.island.chunk.PluginChunkRestoration;
import dev.aabstractt.bridging.manager.IslandManager;
import dev.aabstractt.bridging.utils.Messages;
import lombok.NonNull;
import lombok.SneakyThrows;
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

    @SneakyThrows
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
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to reset other player's island.");

            return;
        }

        Island island = IslandManager.getInstance().byPlayer(target);
        if (island == null) {
            commandSender.sendMessage(Messages.PLAYER_MUST_BE_ON_ISLAND.build(target.getName()));

            return;
        }

        island.setUpdating(true);

        try {
            PluginChunkRestoration.getInstance().reset(island);

            Location bukkitLocation = island.toBukkitLocation();
            String message = Messages.ADMIN_RESTORED_YOUR_ISLAND.build(
                    target.getName(),
                    String.valueOf(bukkitLocation.getBlockX()),
                    String.valueOf(bukkitLocation.getBlockZ())
            );

            commandSender.sendMessage(Messages.ADMIN_RESET_SUCCESS.build(
                    target.getName(),
                    String.valueOf(bukkitLocation.getBlockX()),
                    String.valueOf(bukkitLocation.getBlockZ())
            ));

            island.membersForEach(bridgingPlayer -> {
                Player bukkitPlayer = bridgingPlayer.toBukkitPlayer();
                if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
                    return;
                }

                bukkitPlayer.teleport(bukkitLocation);
                bukkitPlayer.sendMessage(message);
            });

            island.setUpdating(false);
        } catch (IllegalAccessException e) {
            //commandSender.sendMessage(Messages.ADMIN_RESET_ERROR.build(target.getName()));

            IslandManager.getInstance().unloadIsland(island);

            e.fillInStackTrace();
        }
    }
}