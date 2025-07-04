/**
 * Main plugin class for RustyPipes. Uses NMS for optimal performance.
 * Registers listeners and schedules pipe logic with comprehensive lifecycle management.
 */
package fun.mntale.rustyPipes;

import fun.mntale.rustyPipes.managers.PipeNetworkManager;
import fun.mntale.rustyPipes.listeners.PipeBlockListener;
import fun.mntale.rustyPipes.utils.DebugLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for the RustyPipes Paper plugin using NMS for optimal performance.
 */
public final class RustyPipes extends JavaPlugin {
    private PipeNetworkManager pipeNetworkManager;
    private PipeBlockListener pipeBlockListener;
    private int tickTaskId = -1;

    /**
     * Called when the plugin is enabled. Registers listeners and starts the item transfer task.
     */
    @Override
    public void onEnable() {
        try {
            // Initialize managers
            pipeNetworkManager = new PipeNetworkManager();
            
            // Register listeners
            pipeBlockListener = new PipeBlockListener(pipeNetworkManager);
            getServer().getPluginManager().registerEvents(pipeBlockListener, this);
            
            // Schedule item transfer tick every 10 ticks (0.5 seconds)
            tickTaskId = getServer().getScheduler().runTaskTimer(this, pipeNetworkManager::tick, 20, 20L).getTaskId();
            
            DebugLogger.log("<#00ff99>RustyPipes enabled successfully!</#00ff99> <gray>Using NMS for optimal performance.</gray>");
            
        } catch (Exception e) {
            getLogger().severe("Failed to enable RustyPipes: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Called when the plugin is disabled. Cleans up resources and stops tasks.
     */
    @Override
    public void onDisable() {
        try {
            // Cancel the tick task
            if (tickTaskId != -1) {
                getServer().getScheduler().cancelTask(tickTaskId);
                tickTaskId = -1;
            }
            
            // Clear all networks
            if (pipeNetworkManager != null) {
                for (String worldName : pipeNetworkManager.getAllNetworks().keySet()) {
                    pipeNetworkManager.clearNetworks(worldName);
                }
            }
            
            DebugLogger.log("<#ff6b6b>RustyPipes disabled.</#ff6b6b> <gray>All networks cleared.</gray>");
            
        } catch (Exception e) {
            getLogger().severe("Error during plugin shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets the pipe network manager instance.
     * @return The PipeNetworkManager.
     */
    public PipeNetworkManager getPipeNetworkManager() {
        return pipeNetworkManager;
    }

    /**
     * Gets the pipe block listener instance.
     * @return The PipeBlockListener.
     */
    public PipeBlockListener getPipeBlockListener() {
        return pipeBlockListener;
    }
}
