/**
 * Utility class for NMS operations to improve performance and reliability.
 */
package fun.mntale.rustyPipes.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Lightable;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for NMS operations.
 */
public class NMSUtils {
    
    // Material sets for quick lookups
    private static final Set<Material> PIPE_MATERIALS = Set.of(
        Material.COPPER_GRATE,
        Material.COPPER_BLOCK,
        Material.EXPOSED_COPPER,
        Material.WEATHERED_COPPER,
        Material.OXIDIZED_COPPER,
        Material.WAXED_COPPER_BLOCK,
        Material.WAXED_EXPOSED_COPPER,
        Material.WAXED_WEATHERED_COPPER,
        Material.WAXED_OXIDIZED_COPPER,
        Material.WAXED_COPPER_GRATE,
        Material.WAXED_EXPOSED_COPPER_GRATE,
        Material.WAXED_WEATHERED_COPPER_GRATE,
        Material.WAXED_OXIDIZED_COPPER_GRATE
    );
    
    private static final Set<Material> ENDPOINT_MATERIALS = Set.of(
        Material.COPPER_BULB,
        Material.EXPOSED_COPPER_BULB,
        Material.WEATHERED_COPPER_BULB,
        Material.OXIDIZED_COPPER_BULB,
        Material.WAXED_COPPER_BULB,
        Material.WAXED_EXPOSED_COPPER_BULB,
        Material.WAXED_WEATHERED_COPPER_BULB,
        Material.WAXED_OXIDIZED_COPPER_BULB
    );
    
    private static final Set<Material> CONTAINER_MATERIALS = Set.of(
        Material.CHEST,
        Material.TRAPPED_CHEST,
        Material.BARREL,
        Material.HOPPER,
        Material.DROPPER,
        Material.DISPENSER,
        Material.SHULKER_BOX,
        Material.WHITE_SHULKER_BOX,
        Material.ORANGE_SHULKER_BOX,
        Material.MAGENTA_SHULKER_BOX,
        Material.LIGHT_BLUE_SHULKER_BOX,
        Material.YELLOW_SHULKER_BOX,
        Material.LIME_SHULKER_BOX,
        Material.PINK_SHULKER_BOX,
        Material.GRAY_SHULKER_BOX,
        Material.LIGHT_GRAY_SHULKER_BOX,
        Material.CYAN_SHULKER_BOX,
        Material.PURPLE_SHULKER_BOX,
        Material.BLUE_SHULKER_BOX,
        Material.BROWN_SHULKER_BOX,
        Material.GREEN_SHULKER_BOX,
        Material.RED_SHULKER_BOX,
        Material.BLACK_SHULKER_BOX
    );

    /**
     * Gets the NMS Level from a Bukkit World.
     * @param world The Bukkit world.
     * @return The NMS Level.
     */
    public static Level getNMSWorld(World world) {
        return ((CraftWorld) world).getHandle();
    }

    /**
     * Gets the NMS BlockPos from a Bukkit Block.
     * @param block The Bukkit block.
     * @return The NMS BlockPos.
     */
    public static BlockPos getBlockPos(Block block) {
        return new BlockPos(block.getX(), block.getY(), block.getZ());
    }

    /**
     * Gets the NMS BlockPos from coordinates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return The NMS BlockPos.
     */
    public static BlockPos getBlockPos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    /**
     * Gets the NMS BlockEntity at a position.
     * @param world The NMS world.
     * @param pos The block position.
     * @return The BlockEntity, or null if none exists.
     */
    public static BlockEntity getBlockEntity(Level world, BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    /**
     * Gets the NMS BlockState at a position.
     * @param world The NMS world.
     * @param pos The block position.
     * @return The BlockState.
     */
    public static net.minecraft.world.level.block.state.BlockState getNMSBlockState(Level world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    /**
     * Gets a BaseContainerBlockEntity at a position.
     * @param world The NMS world.
     * @param pos The block position.
     * @return The BaseContainerBlockEntity, or null if not a container.
     */
    public static BaseContainerBlockEntity getContainerBlockEntity(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof BaseContainerBlockEntity ? (BaseContainerBlockEntity) blockEntity : null;
    }

    /**
     * Converts a Bukkit ItemStack to NMS ItemStack.
     * @param bukkitItem The Bukkit ItemStack.
     * @return The NMS ItemStack.
     */
    public static net.minecraft.world.item.ItemStack toNMSItemStack(org.bukkit.inventory.ItemStack bukkitItem) {
        return CraftItemStack.asNMSCopy(bukkitItem);
    }

    /**
     * Converts an NMS ItemStack to Bukkit ItemStack.
     * @param nmsItem The NMS ItemStack.
     * @return The Bukkit ItemStack.
     */
    public static org.bukkit.inventory.ItemStack toBukkitItemStack(net.minecraft.world.item.ItemStack nmsItem) {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    /**
     * Checks if a material is a pipe block.
     * @param material The material to check.
     * @return True if it's a pipe material.
     */
    public static boolean isPipe(Material material) {
        return PIPE_MATERIALS.contains(material);
    }

    /**
     * Checks if a material is an endpoint block.
     * @param material The material to check.
     * @return True if it's an endpoint material.
     */
    public static boolean isEndpoint(Material material) {
        return ENDPOINT_MATERIALS.contains(material);
    }

    /**
     * Checks if a material is a container block.
     * @param material The material to check.
     * @return True if it's a container material.
     */
    public static boolean isContainer(Material material) {
        return CONTAINER_MATERIALS.contains(material);
    }

    /**
     * Checks if a block is a lit endpoint (input).
     * @param block The block to check.
     * @return True if it's a lit endpoint.
     */
    public static boolean isLitEndpoint(Block block) {
        if (!isEndpoint(block.getType())) return false;
        BlockState state = block.getState();
        return state.getBlockData() instanceof Lightable lightable && lightable.isLit();
    }

    /**
     * Checks if a block is an unlit endpoint (output).
     * @param block The block to check.
     * @return True if it's an unlit endpoint.
     */
    public static boolean isUnlitEndpoint(Block block) {
        if (!isEndpoint(block.getType())) return false;
        BlockState state = block.getState();
        return state.getBlockData() instanceof Lightable lightable && !lightable.isLit();
    }

    /**
     * Gets all adjacent block positions in 6 directions.
     * @param pos The center position.
     * @return List of adjacent BlockPos.
     */
    public static List<BlockPos> getAdjacentPositions(BlockPos pos) {
        List<BlockPos> adjacent = new ArrayList<>(6);
        adjacent.add(pos.above());
        adjacent.add(pos.below());
        adjacent.add(pos.north());
        adjacent.add(pos.south());
        adjacent.add(pos.east());
        adjacent.add(pos.west());
        return adjacent;
    }

    /**
     * Performs an item transfer between two containers using NMS.
     * @param sourceContainer The source container.
     * @param destContainer The destination container.
     * @param sourceSlot The source slot.
     * @return True if transfer was successful.
     */
    public static boolean transferItem(BaseContainerBlockEntity sourceContainer, BaseContainerBlockEntity destContainer, int sourceSlot) {
        net.minecraft.world.item.ItemStack sourceItem = sourceContainer.getItem(sourceSlot);
        if (sourceItem.isEmpty()) return false;

        // Try to find a slot in destination
        for (int destSlot = 0; destSlot < destContainer.getContainerSize(); destSlot++) {
            net.minecraft.world.item.ItemStack destItem = destContainer.getItem(destSlot);
            if (destItem.isEmpty()) {
                // Empty slot, place item there
                net.minecraft.world.item.ItemStack toMove = sourceItem.copy();
                toMove.setCount(1);
                destContainer.setItem(destSlot, toMove);
                
                // Remove from source
                if (sourceItem.getCount() > 1) {
                    sourceItem.setCount(sourceItem.getCount() - 1);
                    sourceContainer.setItem(sourceSlot, sourceItem);
                } else {
                    sourceContainer.setItem(sourceSlot, net.minecraft.world.item.ItemStack.EMPTY);
                }
                
                // Mark as changed
                sourceContainer.setChanged();
                destContainer.setChanged();
                return true;
            } else if (destItem.getItem() == sourceItem.getItem() && destItem.getCount() < destItem.getMaxStackSize()) {
                // Can stack with existing item
                destItem.setCount(destItem.getCount() + 1);
                destContainer.setItem(destSlot, destItem);
                
                // Remove from source
                if (sourceItem.getCount() > 1) {
                    sourceItem.setCount(sourceItem.getCount() - 1);
                    sourceContainer.setItem(sourceSlot, sourceItem);
                } else {
                    sourceContainer.setItem(sourceSlot, net.minecraft.world.item.ItemStack.EMPTY);
                }
                
                // Mark as changed
                sourceContainer.setChanged();
                destContainer.setChanged();
                return true;
            }
        }
        return false;
    }

    /**
     * Formats a BlockPos as a readable string.
     * @param worldName The world name.
     * @param pos The block position.
     * @return String representation.
     */
    public static String formatPosition(String worldName, BlockPos pos) {
        return worldName + ", " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }
} 