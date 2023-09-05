package dev.aabstractt.bridging.utils;

import dev.aabstractt.bridging.AbstractPlugin;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Map;
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

    public static @NonNull Location minLocation(@NonNull Location firstLocation, @NonNull Location secondLocation) {
        return new Location(
                firstLocation.getWorld(),
                Math.min(firstLocation.getX(), secondLocation.getX()),
                Math.min(firstLocation.getY(), secondLocation.getY()),
                Math.min(firstLocation.getZ(), secondLocation.getZ())
        );
    }

    public static @NonNull Location maxLocation(@NonNull Location firstLocation, @NonNull Location secondLocation) {
        return new Location(
                firstLocation.getWorld(),
                Math.max(firstLocation.getX(), secondLocation.getX()),
                Math.max(firstLocation.getY(), secondLocation.getY()),
                Math.max(firstLocation.getZ(), secondLocation.getZ())
        );
    }
}