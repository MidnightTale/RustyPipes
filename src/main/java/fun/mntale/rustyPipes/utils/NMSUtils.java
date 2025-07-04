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
     * Converts an NMS ItemStack to a Bukkit ItemStack.
     */
    public static org.bukkit.inventory.ItemStack toBukkitItemStack(net.minecraft.world.item.ItemStack nmsStack) {
        return org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(nmsStack);
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
     * Transfers up to 'amount' items from the given slot from source to dest. Returns true if any items were moved.
     */
    public static boolean transferItem(BaseContainerBlockEntity source, BaseContainerBlockEntity dest, int slot, int amount) {
        net.minecraft.world.item.ItemStack sourceItem = source.getItem(slot);
        if (sourceItem.isEmpty() || amount <= 0) return false;
        int moved = 0;
        int maxStack = sourceItem.getMaxStackSize();
        for (int destSlot = 0; destSlot < dest.getContainerSize() && moved < amount; destSlot++) {
            net.minecraft.world.item.ItemStack destItem = dest.getItem(destSlot);
            // Stack with same item
            if (!destItem.isEmpty() && net.minecraft.world.item.ItemStack.isSameItemSameComponents(sourceItem, destItem) && destItem.getCount() < maxStack) {
                int canMove = Math.min(amount - moved, Math.min(sourceItem.getCount(), maxStack - destItem.getCount()));
                if (canMove > 0) {
                    destItem.setCount(destItem.getCount() + canMove);
                    dest.setItem(destSlot, destItem);
                    sourceItem.setCount(sourceItem.getCount() - canMove);
                    moved += canMove;
                }
            }
        }
        // Fill empty slots
        for (int destSlot = 0; destSlot < dest.getContainerSize() && moved < amount; destSlot++) {
            net.minecraft.world.item.ItemStack destItem = dest.getItem(destSlot);
            if (destItem.isEmpty()) {
                int canMove = Math.min(amount - moved, sourceItem.getCount());
                if (canMove > 0) {
                    net.minecraft.world.item.ItemStack toPlace = sourceItem.copy();
                    toPlace.setCount(canMove);
                    dest.setItem(destSlot, toPlace);
                    sourceItem.setCount(sourceItem.getCount() - canMove);
                    moved += canMove;
                }
            }
        }
        source.setItem(slot, sourceItem.isEmpty() ? net.minecraft.world.item.ItemStack.EMPTY : sourceItem);
        if (moved > 0) {
            source.setChanged();
            dest.setChanged();
            return true;
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