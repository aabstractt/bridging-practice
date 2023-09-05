package dev.aabstractt.bridging.island;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.aabstractt.bridging.island.chunk.PluginChunkRestoration;
import dev.aabstractt.bridging.island.listener.BlockBreak;
import dev.aabstractt.bridging.island.listener.BlockPlace;
import dev.aabstractt.bridging.island.listener.BridgingListener;
import dev.aabstractt.bridging.island.listener.PlayerMove;
import dev.aabstractt.bridging.island.schematic.SchematicData;
import dev.aabstractt.bridging.player.BridgingPlayer;
import dev.aabstractt.bridging.player.ModeData;
import dev.aabstractt.bridging.utils.JavaUtils;
import dev.aabstractt.bridging.utils.WorldEditUtils;
import dev.aabstractt.bridging.utils.cuboid.Cuboid;
import io.netty.util.internal.ConcurrentSet;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor @Data
public abstract class Island {

    protected final @NonNull UUID id;

    protected @Nullable Location center = null;

    protected @Nullable Cuboid firstCuboid = null;
    protected @Nullable Cuboid secondCuboid = null;
    protected @Nullable Cuboid cuboid = null;

    protected @Nullable String schematicName = null;
    protected @Nullable UUID ownership = null;

    protected boolean updating = false;

    protected int offset = 0;

    protected @NonNull Set<@NonNull UUID> members = new ConcurrentSet<>();

    protected final @NonNull Set<@NonNull BridgingListener> listeners = new HashSet<>();
    protected final @NonNull Map<String, ChunkSection[]> chunks = Maps.newConcurrentMap();

    protected @Nullable BlockBreak blockBreakListener = null;
    protected @Nullable BlockPlace blockPlaceListener = null;
    protected @Nullable PlayerMove playerMoveListener = null;

    public abstract void firstJoin(@NonNull ModeData modeData);

    public void load(@NonNull ModeData modeData, @NonNull SchematicData schematicData) throws IllegalAccessException {
        if (WorldEditUtils.BUKKIT_WORLD == null) {
            throw new IllegalArgumentException("WorldEditUtils.BUKKIT_WORLD cannot be null");
        }

        this.center = new Location(WorldEditUtils.BUKKIT_WORLD, this.offset, 100, this.offset);

        this.schematicName = modeData.getSchematicName();

        Clipboard clipboard = WorldEditUtils.getClipboard(schematicData.getFirstSchematicName());
        this.firstCuboid = WorldEditUtils.wrapCuboid(
                this.center,
                clipboard.getOrigin(),
                clipboard.getMinimumPoint(),
                clipboard.getMaximumPoint()
        );

        clipboard = WorldEditUtils.getClipboard(schematicData.getSecondSchematicName());
        this.secondCuboid = WorldEditUtils.wrapCuboid(
                this.center,
                clipboard.getOrigin(),
                clipboard.getMinimumPoint(),
                clipboard.getMaximumPoint()
        );

        this.cuboid = new Cuboid(
                JavaUtils.minLocation(this.firstCuboid.getUpperSW(), this.secondCuboid.getUpperSW()),
                JavaUtils.maxLocation(this.firstCuboid.getLowerNE(), this.secondCuboid.getLowerNE())
        );

        this.paste(schematicData);
    }

    public void paste(@NonNull SchematicData schematicData) throws IllegalAccessException {
        if (this.schematicName == null) {
            throw new IllegalArgumentException("Island must have a schematic name");
        }

        PluginChunkRestoration.getInstance().copy(this);

        schematicData.paste(this);
    }

    public void unload() {
        this.updating = true;

        this.chunks.clear();

        this.members.clear();

        this.blockBreakListener = null;
        this.blockPlaceListener = null;
        this.playerMoveListener = null;
    }

    public void registerChunkSections(@NonNull String chunkHash, @NonNull ChunkSection[] chunkSections) {
        this.chunks.put(chunkHash, chunkSections);
    }

    public @NonNull ChunkSection[] getChunkSections(@NonNull String chunkHash) {
        return this.chunks.getOrDefault(chunkHash, new ChunkSection[0]);
    }

    public @NonNull Location toBukkitLocation() {
        if (this.center == null) {
            throw new IllegalArgumentException("Island must have a center");
        }

        return this.center.clone();
    }

    public void broadcast(@NonNull String message) {
        this.membersForEach(bridgingPlayer -> {
            Player bukkitPlayer = bridgingPlayer.toBukkitPlayer();
            if (bukkitPlayer == null || !bukkitPlayer.isOnline()) return;

            bukkitPlayer.sendMessage(message);
        });
    }

    public void membersForEach(@NonNull Consumer<@NonNull BridgingPlayer> consumer) {
        for (UUID uuidParsed : this.members) {
            BridgingPlayer bridgingPlayer = BridgingPlayer.byId(uuidParsed);
            if (bridgingPlayer == null) {
                this.members.remove(uuidParsed);

                continue;
            }

            consumer.accept(bridgingPlayer);
        }
    }

    public boolean isInsideCuboid(@NonNull Location bukkitLocation) {
        return this.cuboid != null && this.cuboid.contains(bukkitLocation);
    }
}