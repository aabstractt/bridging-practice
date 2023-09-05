package dev.aabstractt.bridging.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public final class BaseCommandExecutor implements CommandExecutor {

    private final @NonNull String commandUsage;

    private final @NonNull Set<@NonNull Argument> arguments = new HashSet<>();

    public @NonNull BaseCommandExecutor addArgument(@NonNull Argument argument) {
        this.arguments.add(argument);

        return this;
    }

    public @Nullable Argument getArgumentByName(@NonNull String commandLabel) {
        return this.arguments.stream()
                .filter(argument -> argument.getName().equalsIgnoreCase(commandLabel))
                .findFirst().orElse(null);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.commandUsage.replace("<label>", s)));

            return true;
        }

        Argument argument = this.getArgumentByName(args[0]);
        if (argument == null) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.commandUsage.replace("<label>", s)));

            return true;
        }

        if (argument.getPermission() != null && !commandSender.hasPermission(argument.getPermission())) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");

            return true;
        }

        String[] copyArgs = Arrays.copyOfRange(args, 1, args.length);
        if (copyArgs.length < argument.getMinArgs()) {
            commandSender.sendMessage(ChatColor.RED + "Usage: " + argument.getUsage().replace("<label>", s));

            return true;
        }

        argument.execute(commandSender, copyArgs);

        return true;
    }
}