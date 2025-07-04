/**
 * Listener for pipe and container block placement and break events.
 * Uses NMS for optimal performance and comprehensive event handling.
 */
package fun.mntale.rustyPipes.listeners;

import fun.mntale.rustyPipes.managers.PipeNetworkManager;
import fun.mntale.rustyPipes.utils.NMSUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

/**
 * Listens for block events and updates pipe networks using NMS for optimal performance.
 */
public class PipeBlockListener implements Listener {
    private final PipeNetworkManager networkManager;

    /**
     * Constructs a new PipeBlockListener.
     * @param networkManager The manager responsible for pipe networks.
     */
    public PipeBlockListener(PipeNetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * Called when a block is placed. Updates pipe networks if relevant.
     * @param event The block place event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        Block block = event.getBlockPlaced();
        if (NMSUtils.isPipe(block.getType()) || NMSUtils.isEndpoint(block.getType()) || NMSUtils.isContainer(block.getType())) {
            networkManager.updateNetworks(block);
        }
    }

    /**
     * Called when a block is broken. Updates pipe networks if relevant.
     * @param event The block break event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Block block = event.getBlock();
        if (NMSUtils.isPipe(block.getType()) || NMSUtils.isEndpoint(block.getType()) || NMSUtils.isContainer(block.getType())) {
            networkManager.updateNetworks(block);
        }
    }

    /**
     * Called when blocks are destroyed by explosions. Updates pipe networks if relevant.
     * @param event The block explode event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.isCancelled()) return;
        
        for (Block block : event.blockList()) {
            if (NMSUtils.isPipe(block.getType()) || NMSUtils.isEndpoint(block.getType()) || NMSUtils.isContainer(block.getType())) {
                networkManager.updateNetworks(block);
            }
        }
    }

    /**
     * Called when blocks are destroyed by entity explosions. Updates pipe networks if relevant.
     * @param event The entity explode event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        
        for (Block block : event.blockList()) {
            if (NMSUtils.isPipe(block.getType()) || NMSUtils.isEndpoint(block.getType()) || NMSUtils.isContainer(block.getType())) {
                networkManager.updateNetworks(block);
            }
        }
    }

    /**
     * Called when a chunk is loaded. Scans for pipe networks in the chunk.
     * @param event The chunk load event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        // Scan chunk for pipe networks when loaded
        // This ensures networks are detected when chunks are loaded
        for (int x = 0; x < 16; x++) {
            for (int y = event.getWorld().getMinHeight(); y < event.getWorld().getMaxHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = event.getChunk().getBlock(x, y, z);
                    if (NMSUtils.isPipe(block.getType()) || NMSUtils.isEndpoint(block.getType())) {
                        networkManager.updateNetworks(block);
                        return; // Only need to trigger once per chunk
                    }
                }
            }
        }
    }

    /**
     * Called when a chunk is unloaded. Cleans up networks in the chunk.
     * @param event The chunk unload event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Note: We don't clear networks on chunk unload as they might be connected across chunks
        // The networks will be rebuilt when chunks are loaded again
    }

    /**
     * Called when a block receives a redstone signal. Updates pipe networks if relevant.
     * @param event The redstone event.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (NMSUtils.isEndpoint(block.getType())) {
            networkManager.updateNetworks(block);
        }
    }
}