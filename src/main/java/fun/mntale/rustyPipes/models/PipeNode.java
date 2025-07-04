/**
 * Represents a single pipe block or endpoint in a pipe network.
 * Uses NMS BlockPos for better performance and consistency.
 */
package fun.mntale.rustyPipes.models;

import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import java.util.Objects;

/**
 * Represents a pipe block in the network using NMS BlockPos.
 */
public class PipeNode {
    private final BlockPos blockPos;
    private final String worldName;

    /**
     * Constructs a PipeNode from NMS BlockPos.
     * @param blockPos The NMS block position.
     * @param worldName The world name.
     */
    public PipeNode(BlockPos blockPos, String worldName) {
        this.blockPos = blockPos;
        this.worldName = worldName;
    }

    /**
     * Constructs a PipeNode from Bukkit Location.
     * @param location The Bukkit location.
     */
    public PipeNode(Location location) {
        this.blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.worldName = location.getWorld().getName();
    }

    /**
     * Gets the NMS BlockPos of this node.
     * @return The NMS block position.
     */
    public BlockPos getBlockPos() {
        return blockPos;
    }

    /**
     * Gets the world name of this node.
     * @return The world name.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets the Bukkit Location of this node.
     * @return The Bukkit location.
     */
    public Location getLocation() {
        return new Location(org.bukkit.Bukkit.getWorld(worldName), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Gets the X coordinate.
     * @return X coordinate.
     */
    public int getX() {
        return blockPos.getX();
    }

    /**
     * Gets the Y coordinate.
     * @return Y coordinate.
     */
    public int getY() {
        return blockPos.getY();
    }

    /**
     * Gets the Z coordinate.
     * @return Z coordinate.
     */
    public int getZ() {
        return blockPos.getZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PipeNode pipeNode = (PipeNode) o;
        return blockPos.equals(pipeNode.blockPos) && worldName.equals(pipeNode.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockPos, worldName);
    }

    @Override
    public String toString() {
        return "PipeNode{world=" + worldName + ", pos=" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + "}";
    }
} 