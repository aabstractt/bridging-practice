package dev.aabstractt.bridging.utils;

import dev.aabstractt.bridging.AbstractPlugin;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public final class JavaUtils {

    public static @NonNull <T> CompletableFuture<T> failedFuture(@NonNull Throwable throwable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);

        return future;
    }

    public static void runAsync(@NonNull Runnable runnable) {
        if (!Bukkit.isPrimaryThread()) {
            runnable.run();

            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(AbstractPlugin.getInstance(), runnable);
    }
}