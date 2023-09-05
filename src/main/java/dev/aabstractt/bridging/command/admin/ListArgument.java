package dev.aabstractt.bridging.command.admin;

import dev.aabstractt.bridging.command.Argument;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import lombok.NonNull;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public final class ListArgument extends Argument {

    public ListArgument(@NonNull String name, @NonNull String usage, int minArgs, @Nullable String permission) {
        super(name, usage, minArgs, permission);
    }

    @Override
    public void execute(@NonNull CommandSender commandSender, @NonNull String[] args) {
        for (String schematicName : WorldEditUtils.getSchematics().keySet()) {
            commandSender.sendMessage(schematicName);
        }
    }
}