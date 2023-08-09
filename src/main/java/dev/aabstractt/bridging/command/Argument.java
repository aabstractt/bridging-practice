package dev.aabstractt.bridging.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

@AllArgsConstructor @RequiredArgsConstructor @Data
public abstract class Argument {

    private final @NonNull String name;
    private final @NonNull String usage;

    private final int minArgs;

    private @Nullable String permission = null;

    public abstract void execute(@NonNull CommandSender commandSender, @NonNull String[] args);
}