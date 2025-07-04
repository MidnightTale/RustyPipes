/**
 * Utility for MiniMessage-formatted debug logging to the console.
 * Set debug to false to disable debug output.
 */
package fun.mntale.rustyPipes.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Utility for MiniMessage-formatted debug logging.
 */
public class DebugLogger {
    private static final boolean debug = true; // Set to false to disable debug

    /**
     * Logs a MiniMessage-formatted message to the console and to all players with debug permission.
     * @param miniMessage The MiniMessage string to log.
     */
    public static void log(String miniMessage) {
        if (!debug) return;
        Component component = MiniMessage.miniMessage().deserialize("<gray>[RustyPipes] </gray>" + miniMessage);
        Bukkit.getConsoleSender().sendMessage(component);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("rustypipes.debug")) {
                player.sendMessage(component);
            }
        }
    }
} 