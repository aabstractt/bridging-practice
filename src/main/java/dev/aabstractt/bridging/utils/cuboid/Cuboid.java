package dev.aabstractt.bridging.utils.cuboid;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {

    private String worldName;
    private int firstX;
    private int firstY;
    private int firstZ;
    private int secondX;
    private int secondY;
    private int secondZ;

    public Cuboid(@NonNull World world, int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ) {
        this(world.getName(), firstX, firstY, firstZ, secondX, secondY, secondZ);
    }

    private Cuboid(@NonNull String worldName, int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ) {
        this.worldName = worldName;

        this.firstX = Math.min(firstX, secondX);
        this.firstY = Math.min(firstY, secondY);
        this.firstZ = Math.min(firstZ, secondZ);

        this.secondX = Math.max(firstX, secondX);
        this.secondY = Math.max(firstY, secondY);
        this.secondZ = Math.max(firstZ, secondZ);
    }

    public Cuboid(@NonNull Location first, @NonNull Location second) {
        World world = first.getWorld();

        this.worldName = world == null ? Bukkit.getWorlds().get(0).getName() : world.getName();

        this.firstX = Math.min(first.getBlockX(), second.getBlockX());
        this.firstY = Math.min(first.getBlockY(), second.getBlockY());
        this.firstZ = Math.min(first.getBlockZ(), second.getBlockZ());

        this.secondX = Math.max(first.getBlockX(), second.getBlockX());
        this.secondY = Math.max(first.getBlockY(), second.getBlockY());
        this.secondZ = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    public @NonNull Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put("worldName", this.worldName)
                .put("firstX", this.firstX)
                .put("firstY", this.firstY)
                .put("firstZ", this.firstZ)
                .put("secondX", this.secondX)
                .put("secondY", this.secondY)
                .put("secondZ", this.secondZ)
                .build();
    }

    public boolean hasBothPositionsSet() {
        return this.getMinimumPoint() != null && this.getMaximumPoint() != null;
    }

    public int getMinimumX() {
        return Math.min(this.firstX, this.secondX);
    }

    public int getMinimumZ() {
        return Math.min(this.firstZ, this.secondZ);
    }

    public int getMaximumX() {
        return Math.max(this.firstX, this.secondX);
    }

    public int getMaximumZ() {
        return Math.max(this.firstZ, this.secondZ);
    }

    public List<Vector> edges() {
        return this.edges(-1, -1, -1, -1);
    }

    public List<Vector> edges(int fixedMinX, int fixedMaxX, int fixedMinZ, int fixedMaxZ) {
        Vector v1 = this.getMinimumPoint().toVector();
        Vector v2 = this.getMaximumPoint().toVector();
        int minX = v1.getBlockX();
        int maxX = v2.getBlockX();
        int minZ = v1.getBlockZ();
        int maxZ = v2.getBlockZ();
        int capacity = (maxX - minX) * 4 + (maxZ - minZ) * 4;

        ArrayList<Vector> result = new ArrayList<>(capacity += 4);
        if (capacity == 0) {
            return result;
        }

        int minY = v1.getBlockY();
        int maxY = v1.getBlockY();
        for (int x = minX; x <= maxX; ++x) {
            result.add(new Vector(x, minY, minZ));
            result.add(new Vector(x, minY, maxZ));
            result.add(new Vector(x, maxY, minZ));
            result.add(new Vector(x, maxY, maxZ));
        }
        for (int z = minZ; z <= maxZ; ++z) {
            result.add(new Vector(minX, minY, z));
            result.add(new Vector(minX, maxY, z));
            result.add(new Vector(maxX, minY, z));
            result.add(new Vector(maxX, maxY, z));
        }

        return result;
    }

    public Set<Player> getPlayers() {
        HashSet<Player> players = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.contains(player)) {
                continue;
            }
            players.add(player);
        }

        return players;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), this.firstX, this.firstY, this.firstZ);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), this.secondX, this.secondY, this.secondZ);
    }

    public Location getCenter() {
        int x1 = this.secondX + 1;
        int y1 = this.secondY + 1;
        int z1 = this.secondZ + 1;

        return new Location(this.getWorld(), this.firstX + (x1 - this.firstX) / 2.0,
                this.firstY + (y1 - this.firstY) / 2.0,
                this.firstZ + (z1 - this.firstZ) / 2.0);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public int getSizeX() {
        return this.secondX - this.firstX + 1;
    }

    public int getSizeY() {
        return this.secondY - this.firstY + 1;
    }

    public int getSizeZ() {
        return this.secondZ - this.firstZ + 1;
    }

    public Location[] getCornerLocations() {
        Location[] result = new Location[8];
        Block[] cornerBlocks = this.getCornerBlocks();
        for (int i = 0; i < cornerBlocks.length; ++i) {
            result[i] = cornerBlocks[i].getLocation();
        }

        return result;
    }

    public Block[] getCornerBlocks() {
        Block[] result = new Block[8];
        World world = this.getWorld();
        result[0] = world.getBlockAt(this.firstX, this.firstY, this.firstZ);
        result[1] = world.getBlockAt(this.firstX, this.firstY, this.secondZ);
        result[2] = world.getBlockAt(this.firstX, this.secondY, this.firstZ);
        result[3] = world.getBlockAt(this.firstX, this.secondY, this.secondZ);
        result[4] = world.getBlockAt(this.secondX, this.firstY, this.firstZ);
        result[5] = world.getBlockAt(this.secondX, this.firstY, this.secondZ);
        result[6] = world.getBlockAt(this.secondX, this.secondY, this.firstZ);
        result[7] = world.getBlockAt(this.secondX, this.secondY, this.secondZ);

        return result;
    }

    public Cuboid shift(CuboidDirection direction, int amount) throws IllegalArgumentException {
        return this.expand(direction, amount).expand(direction.opposite(), -amount);
    }

    public Cuboid inset(CuboidDirection direction, int amount) throws IllegalArgumentException {
        return this.outset(direction, -amount);
    }

    public Cuboid expand(CuboidDirection direction, int amount) throws IllegalArgumentException {
        switch (direction) {
            case NORTH: {
                return new Cuboid(this.worldName, this.firstX - amount, this.firstY, this.firstZ, this.secondX, this.secondY,
                        this.secondZ);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX + amount, this.secondY,
                        this.secondZ);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ - amount, this.secondX, this.secondY,
                        this.secondZ);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY,
                        this.secondZ + amount);
            }
            case DOWN: {
                return new Cuboid(this.worldName, this.firstX, this.firstY - amount, this.firstZ, this.secondX, this.secondY,
                        this.secondZ);
            }
            case UP: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY + amount,
                        this.secondZ);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + direction);
    }

    public Cuboid outset(CuboidDirection direction, int amount) throws IllegalArgumentException {
        switch (direction) {
            case HORIZONTAL: {
                return this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount)
                        .expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);
            }
            case VERTICAL: {
                return this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
            }
            case BOTH: {
                return this.outset(CuboidDirection.HORIZONTAL, amount)
                        .outset(CuboidDirection.VERTICAL, amount);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + direction);
    }

    public boolean contains(Cuboid cuboid) {
        return this.contains(cuboid.getMinimumPoint()) || this.contains(cuboid.getMaximumPoint());
    }

    public boolean contains(Player player) {
        return this.contains(player.getLocation());
    }

    public boolean contains(World world, int x, int z) {
        return (world == null || this.getWorld().equals(world)) && x >= this.firstX && x <= this.secondX
                && z >= this.firstZ && z <= this.secondZ;
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.firstX && x <= this.secondX && y >= this.firstY && y <= this.secondY && z >= this.firstZ
                && z <= this.secondZ;
    }

    public boolean contains(Block block) {
        return this.contains(block.getLocation());
    }

    public boolean contains(Location location) {
        if (location == null || this.worldName == null) {
            return false;
        }
        World world = location.getWorld();

        return world != null && this.worldName.equals(location.getWorld().getName()) && this.contains(
                location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public int getArea() {
        Location min = this.getMinimumPoint();
        Location max = this.getMaximumPoint();

        return (max.getBlockX() - min.getBlockX() + 1) * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public byte getAverageLightLevel() {
        long total = 0L;
        int count = 0;
        for (Block block : this) {
            if (!block.isEmpty()) {
                continue;
            }
            total += block.getLightLevel();
            ++count;
        }

        return count > 0 ? (byte) (total / count) : (byte) 0;
    }

    public Location getMinimumPoint() {
        return new Location(this.getWorld(), Math.min(this.firstX, this.secondX), Math.min(this.firstY, this.secondY),
                Math.min(this.firstZ, this.secondZ));
    }

    public Location getMaximumPoint() {
        return new Location(this.getWorld(), Math.max(this.firstX, this.secondX), Math.max(this.firstY, this.secondY),
                Math.max(this.firstZ, this.secondZ));
    }

    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX();
    }

    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY();
    }

    public int getLength() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ();
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH)
                .contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH)
                .contract(CuboidDirection.WEST);
    }

    public Cuboid contract(CuboidDirection direction) {
        Cuboid face = this.getFace(direction.opposite());
        switch (direction) {
            case DOWN: {
                while (face.containsOnly(Material.AIR) && face.firstY > this.firstY) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, face.secondY, this.secondZ);
            }
            case UP: {
                while (face.containsOnly(Material.AIR) && face.secondY < this.secondY) {
                    face = face.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.firstX, face.firstY, this.firstZ, this.secondX, this.secondY, this.secondZ);
            }
            case NORTH: {
                while (face.containsOnly(Material.AIR) && face.firstX > this.firstX) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, face.secondX, this.secondY, this.secondZ);
            }
            case SOUTH: {
                while (face.containsOnly(Material.AIR) && face.secondX < this.secondX) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, face.firstX, this.firstY, this.firstZ, this.secondX, this.secondY, this.secondZ);
            }
            case EAST: {
                while (face.containsOnly(Material.AIR) && face.firstZ > this.firstZ) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY, face.secondZ);
            }
            case WEST: {
                while (face.containsOnly(Material.AIR) && face.secondZ < this.secondZ) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.firstX, this.firstY, face.firstZ, this.secondX, this.secondY, this.secondZ);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + direction);
    }

    public Cuboid getFace(CuboidDirection direction) {
        switch (direction) {
            case DOWN: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, this.firstY, this.secondZ);
            }
            case UP: {
                return new Cuboid(this.worldName, this.firstX, this.secondY, this.firstZ, this.secondX, this.secondY, this.secondZ);
            }
            case NORTH: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.firstX, this.secondY, this.secondZ);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.secondX, this.firstY, this.firstZ, this.secondX, this.secondY, this.secondZ);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY, this.firstZ);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.firstX, this.firstY, this.secondZ, this.secondX, this.secondY, this.secondZ);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + direction);
    }

    public boolean containsOnly(Material material) {
        for (Block block : this) {
            if (block.getType() == material) {
                continue;
            }
            return false;
        }

        return true;
    }

    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) {
          return this;
        }

        int xMin = Math.min(this.firstX, other.firstX);
        int yMin = Math.min(this.firstY, other.firstY);
        int zMin = Math.min(this.firstZ, other.firstZ);
        int xMax = Math.max(this.secondX, other.secondX);
        int yMax = Math.max(this.secondY, other.secondY);
        int zMax = Math.max(this.secondZ, other.secondZ);

        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(this.firstX + x, this.firstY + y, this.firstZ + z);
    }

    public Block getRelativeBlock(World world, int x, int y, int z) {
        return world.getBlockAt(this.firstX + x, this.firstY + y, this.firstZ + z);
    }

    public List<Chunk> getChunks() {
        World world = this.getWorld();
        int x1 = this.firstX & -16;
        int x2 = this.secondX & -16;
        int z1 = this.firstZ & -16;
        int z2 = this.secondZ & -16;

        ArrayList<Chunk> result = new ArrayList<>(x2 - x1 + 16 + (z2 - z1) * 16);
        for (int x3 = x1; x3 <= x2; x3 += 16) {
            for (int z3 = z1; z3 <= z2; z3 += 16) {
                result.add(world.getChunkAt(x3 >> 4, z3 >> 4));
            }
        }

        return result;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidBlockIterator(this.getWorld(), this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY,
                this.secondZ);
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(this.getWorld(), this.firstX, this.firstY, this.firstZ, this.secondX, this.secondY,
                this.secondZ);
    }

    public Cuboid clone() {
        try {
            return (Cuboid) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("This could never happen", ex);
        }
    }

    public String toString() {
        return "Cuboid: " + this.worldName + ',' + this.firstX + ',' + this.firstY + ',' + this.firstZ + "=>"
                + this.secondX + ',' + this.secondY + ',' + this.secondZ;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setFirstX(int firstX) {
        this.firstX = firstX;
    }

    public void setFirstY(int firstY) {
        this.firstY = firstY;
    }

    public void setFirstZ(int firstZ) {
        this.firstZ = firstZ;
    }

    public void setSecondX(int secondX) {
        this.secondX = secondX;
    }

    public void setSecondY(int secondY) {
        this.secondY = secondY;
    }

    public void setSecondZ(int secondZ) {
        this.secondZ = secondZ;
    }
}

