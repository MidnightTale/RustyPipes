/**
 * Represents a network of connected pipes and endpoints (containers).
 * Uses NMS BlockPos for better performance and consistency.
 */
package fun.mntale.rustyPipes.models;

import net.minecraft.core.BlockPos;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a network of pipes and endpoints using NMS BlockPos.
 */
public class PipeNetwork {
    private final Set<PipeNode> pipes = new HashSet<>();
    private String worldName;

    /**
     * Constructs a PipeNetwork.
     * @param worldName The world name for this network.
     */
    public PipeNetwork(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the world name of this network.
     * @return The world name.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets the set of pipe nodes in this network.
     * @return Set of PipeNode.
     */
    public Set<PipeNode> getPipes() {
        return pipes;
    }

    /**
     * Adds a pipe node to the network.
     * @param node The PipeNode to add.
     */
    public void addPipe(PipeNode node) {
        pipes.add(node);
    }

    /**
     * Removes a pipe node from the network.
     * @param node The PipeNode to remove.
     */
    public void removePipe(PipeNode node) {
        pipes.remove(node);
    }

    /**
     * Checks if this network contains a specific position.
     * @param blockPos The position to check.
     * @return True if the position is in this network.
     */
    public boolean containsPosition(BlockPos blockPos) {
        return pipes.stream().anyMatch(node -> node.getBlockPos().equals(blockPos));
    }

    /**
     * Gets the total number of components in this network.
     * @return Total number of pipes.
     */
    public int getTotalComponents() {
        return pipes.size();
    }

    /**
     * Checks if this network has a pipe at the given BlockPos.
     * @param pos The BlockPos to check.
     * @return True if a pipe exists at the position.
     */
    public boolean hasPipeAt(BlockPos pos) {
        return pipes.stream().anyMatch(node -> node.getBlockPos().equals(pos));
    }
} 