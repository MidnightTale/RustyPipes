/**
 * Manages all pipe networks in the world using NMS for optimal performance.
 * Handles scanning for connected pipes and containers, and moving items between endpoints.
 */
package fun.mntale.rustyPipes.managers;

import fun.mntale.rustyPipes.models.PipeNetwork;
import fun.mntale.rustyPipes.models.PipeNode;
import fun.mntale.rustyPipes.utils.DebugLogger;
import fun.mntale.rustyPipes.utils.NMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all pipe networks using NMS for optimal performance.
 */
public class PipeNetworkManager {
    private final Map<String, List<PipeNetwork>> networks = new ConcurrentHashMap<>();
    private static final Set<Material> PIPE_MATERIALS = Set.of(
        Material.COPPER_GRATE,
        Material.COPPER_BLOCK,
        Material.EXPOSED_COPPER,
        Material.WEATHERED_COPPER,
        Material.OXIDIZED_COPPER
    );

    private static final Set<Material> CONTAINER_MATERIALS = Set.of(
        Material.CHEST,
        Material.BARREL,
        Material.HOPPER,
        Material.DROPPER,
        Material.DISPENSER
    );
    private static final Set<Material> BOTH_ENDPOINTS = Set.of(
        Material.COPPER_BLOCK, Material.WAXED_COPPER_BLOCK,
        Material.EXPOSED_COPPER, Material.WAXED_EXPOSED_COPPER,
        Material.WEATHERED_COPPER, Material.WAXED_WEATHERED_COPPER,
        Material.OXIDIZED_COPPER, Material.WAXED_OXIDIZED_COPPER
    );
    private static final Set<Material> OUTPUT_ENDPOINTS = Set.of(
        Material.CUT_COPPER, Material.WAXED_CUT_COPPER,
        Material.EXPOSED_CUT_COPPER, Material.WAXED_EXPOSED_CUT_COPPER,
        Material.WEATHERED_CUT_COPPER, Material.WAXED_WEATHERED_CUT_COPPER,
        Material.OXIDIZED_CUT_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER
    );
    private static final Set<Material> INPUT_ENDPOINTS = Set.of(
        Material.CHISELED_COPPER, Material.WAXED_CHISELED_COPPER,
        Material.EXPOSED_CHISELED_COPPER, Material.WAXED_EXPOSED_CHISELED_COPPER,
        Material.WEATHERED_CHISELED_COPPER, Material.WAXED_WEATHERED_CHISELED_COPPER,
        Material.OXIDIZED_CHISELED_COPPER, Material.WAXED_OXIDIZED_CHISELED_COPPER
    );

    private final Plugin plugin = org.bukkit.Bukkit.getPluginManager().getPlugin("RustyPipes");

    /**
     * Scans the world for all pipe networks. (Not implemented)
     * @param world The world to scan.
     */
    public void scanWorld(World world) {
        // Not implemented for now
    }

    /**
     * Updates pipe networks when a block is placed or broken.
     * Now runs network scanning async for performance.
     * @param changedBlock The block that was changed.
     */
    public void updateNetworks(Block changedBlock) {
        String worldName = changedBlock.getWorld().getName();
        BlockPos changedPos = NMSUtils.getBlockPos(changedBlock);
        int radius = 3;
        World world = changedBlock.getWorld();
        // 1. Collect block data (main thread)
        Map<BlockPos, Material> blockMap = new HashMap<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = changedPos.offset(dx, dy, dz);
                    Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                    blockMap.put(pos, block.getType());
                }
            }
        }
        // 2. Run network scan async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Set<BlockPos> visited = new HashSet<>();
            List<PipeNetwork> newNetworks = new ArrayList<>();
            // Remove any networks that intersect the changed block (sync, later)
            // Scan for all unvisited pipe/endpoint blocks in the area
            for (BlockPos pos : blockMap.keySet()) {
                if (visited.contains(pos)) continue;
                Material mat = blockMap.get(pos);
                if (NMSUtils.isPipe(mat) || isEndpointBlock(mat)) {
                    PipeNetwork network = scanNetworkAsync(pos, blockMap, visited, worldName);
                    if (network != null && network.getTotalComponents() > 0) {
                        newNetworks.add(network);
                    }
                }
            }
            // 3. Apply results on main thread
            Bukkit.getScheduler().runTask(plugin, () -> {
                List<PipeNetwork> oldNetworks = getNetworks(worldName);
                oldNetworks.removeIf(network -> network.containsPosition(changedPos));
                oldNetworks.clear();
                oldNetworks.addAll(newNetworks);
                for (PipeNetwork network : newNetworks) {
                    DebugLogger.log("<#ffb300>Network rebuilt (async)</#ffb300> at <#00eaff>" + worldName + "</#00eaff> <gray>|</gray> <#00ff99>Pipes:</#00ff99> " + network.getPipes().size());
                }
            });
        });
    }

    /**
     * Async network scan using only local block data.
     */
    private PipeNetwork scanNetworkAsync(BlockPos start, Map<BlockPos, Material> blockMap, Set<BlockPos> visited, String worldName) {
        Queue<BlockPos> queue = new LinkedList<>();
        PipeNetwork network = new PipeNetwork(worldName);
        queue.add(start);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (visited.contains(pos)) continue;
            visited.add(pos);
            Material mat = blockMap.get(pos);
            if (mat == null) continue;
            if (NMSUtils.isPipe(mat) || isEndpointBlock(mat)) {
                network.addPipe(new fun.mntale.rustyPipes.models.PipeNode(pos, worldName));
            }
            for (BlockPos adj : NMSUtils.getAdjacentPositions(pos)) {
                if (!visited.contains(adj) && blockMap.containsKey(adj)) {
                    Material adjMat = blockMap.get(adj);
                    if (NMSUtils.isPipe(adjMat) || isEndpointBlock(adjMat)) {
                        queue.add(adj);
                    }
                }
            }
        }
        return network;
    }

    /**
     * Gets the list of pipe networks for a world.
     * @param worldName The world name.
     * @return List of PipeNetwork objects.
     */
    public List<PipeNetwork> getNetworks(String worldName) {
        return networks.computeIfAbsent(worldName, w -> new ArrayList<>());
    }

    /**
     * Checks if a material is a valid pipe block.
     * @param material The material to check.
     * @return True if the material is a pipe.
     */
    public static boolean isPipe(Material material) {
        return PIPE_MATERIALS.contains(material);
    }

    /**
     * Checks if a material is a valid container block.
     * @param material The material to check.
     * @return True if the material is a container.
     */
    public static boolean isContainer(Material material) {
        return CONTAINER_MATERIALS.contains(material);
    }

    /**
     * Called every tick to move items through all pipe networks using NMS.
     * Runs synchronously on the main thread.
     */
    public void tick() {
        for (Map.Entry<String, List<PipeNetwork>> entry : networks.entrySet()) {
            String worldName = entry.getKey();
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            
            Level nmsWorld = NMSUtils.getNMSWorld(world);
            
            // Iterate over a copy to avoid ConcurrentModificationException
            for (PipeNetwork network : new ArrayList<>(entry.getValue())) {
                // No more getInputEndpoints()/getOutputEndpoints() checks
                // Scan endpoints and classify
                List<Endpoint> outputs = new ArrayList<>();
                List<Endpoint> inputs = new ArrayList<>();
                for (PipeNode node : network.getPipes()) {
                    BlockPos pos = node.getBlockPos();
                    Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                    Material mat = block.getType();
                    int power = getRedstonePower(world, pos);
                    BlockPos containerPos = getAdjacentContainer(world, pos);
                    if (containerPos == null) continue;
                    if (BOTH_ENDPOINTS.contains(mat)) {
                        if (power > 0) {
                            inputs.add(new Endpoint(pos, containerPos, power));
                        } else {
                            outputs.add(new Endpoint(pos, containerPos, power));
                        }
                    } else if (OUTPUT_ENDPOINTS.contains(mat)) {
                        outputs.add(new Endpoint(pos, containerPos, power));
                    } else if (INPUT_ENDPOINTS.contains(mat)) {
                        inputs.add(new Endpoint(pos, containerPos, power));
                    }
                }
                // Sort inputs: highest power first, then nearest to output
                inputs.sort((a, b) -> Integer.compare(b.power, a.power));

                // Sort outputs: lowest power first
                outputs.sort(Comparator.comparingInt(a -> a.power));
                // For each output, move one item to best input
                for (Endpoint output : outputs) {
                    BaseContainerBlockEntity source = NMSUtils.getContainerBlockEntity(nmsWorld, output.containerPos);
                    if (source == null) continue;
                    // Find best input (highest power, then nearest, then prefer left)
                    Endpoint bestInput = null;
                    int bestDist = Integer.MAX_VALUE;
                    int bestLeft = Integer.MIN_VALUE;
                    BlockPos prevPipe = null;
                    // Find a pipe adjacent to the output (other than the container)
                    for (BlockFace face : BlockFace.values()) {
                        if (face == BlockFace.SELF) continue;
                        BlockPos adj = new BlockPos(output.pos.getX() + face.getModX(), output.pos.getY() + face.getModY(), output.pos.getZ() + face.getModZ());
                        if (!adj.equals(output.containerPos) && network.hasPipeAt(adj)) {
                            prevPipe = adj;
                            break;
                        }
                    }
                    for (Endpoint input : inputs) {
                        int dist = manhattan(output.pos, input.pos);
                        int left = 0;
                        if (prevPipe != null) {
                            left = leftness(output.pos, prevPipe, input.pos);
                        }
                        if (bestInput == null
                                || input.power > bestInput.power
                                || (input.power == bestInput.power && dist < bestDist)
                                || (input.power == bestInput.power && dist == bestDist && left > bestLeft)) {
                            bestInput = input;
                            bestDist = dist;
                            bestLeft = left;
                        }
                    }
                    if (bestInput == null) continue;
                    BaseContainerBlockEntity dest = NMSUtils.getContainerBlockEntity(nmsWorld, bestInput.containerPos);
                    if (dest == null) continue;
                    // Try to move one item
                    for (int slot = 0; slot < source.getContainerSize(); slot++) {
                        net.minecraft.world.item.ItemStack item = source.getItem(slot);
                        if (item.isEmpty()) continue;
                        if (NMSUtils.transferItem(source, dest, slot)) {
                            DebugLogger.log("<#00eaff>[NMS]</#00eaff> <#00ff99>Moved:</#00ff99> <#ffb300>" + NMSUtils.toBukkitItemStack(item).getType() + "</#ffb300> <gray>x1</gray> <gray>|</gray> <#00eaff>From:</#00eaff> <gray>" + world.getBlockAt(output.containerPos.getX(), output.containerPos.getY(), output.containerPos.getZ()).getType() + "</gray> <aqua>" + NMSUtils.formatPosition(worldName, output.containerPos) + "</aqua> <gray>|</gray> <#ffb300>To:</#ffb300> <gray>" + world.getBlockAt(bestInput.containerPos.getX(), bestInput.containerPos.getY(), bestInput.containerPos.getZ()).getType() + "</gray> <aqua>" + NMSUtils.formatPosition(worldName, bestInput.containerPos) + "</aqua>");
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all networks across all worlds.
     * @return Map of world name to list of networks.
     */
    public Map<String, List<PipeNetwork>> getAllNetworks() {
        return new HashMap<>(networks);
    }

    /**
     * Clears all networks for a specific world.
     * @param worldName The world name.
     */
    public void clearNetworks(String worldName) {
        networks.remove(worldName);
    }

    /**
     * Gets the total number of networks across all worlds.
     * @return Total number of networks.
     */
    public int getTotalNetworks() {
        return networks.values().stream().mapToInt(List::size).sum();
    }

    // Helper to get redstone power at a block
    private int getRedstonePower(World world, BlockPos pos) {
        Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        return block.getBlockPower();
    }

    // Helper to get adjacent container block position (returns null if none)
    private BlockPos getAdjacentContainer(World world, BlockPos pos) {
        for (BlockFace face : BlockFace.values()) {
            if (face == BlockFace.SELF) continue;
            Block b = world.getBlockAt(pos.getX() + face.getModX(), pos.getY() + face.getModY(), pos.getZ() + face.getModZ());
            if (NMSUtils.isContainer(b.getType())) {
                return new BlockPos(b.getX(), b.getY(), b.getZ());
            }
        }
        return null;
    }

    // Manhattan distance
    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    // Utility: Compute 'leftness' of candidate endpoint relative to pipe direction (XZ plane)
    private static int leftness(BlockPos from, BlockPos via, BlockPos candidate) {
        // Direction of travel: via -> from (previous pipe to current output)
        int dx1 = from.getX() - via.getX();
        int dz1 = from.getZ() - via.getZ();
        // Direction to candidate: from -> candidate
        int dx2 = candidate.getX() - from.getX();
        int dz2 = candidate.getZ() - from.getZ();
        // Cross product (Y component): dx1*dz2 - dz1*dx2
        return dx1 * dz2 - dz1 * dx2;
    }

    // Helper class for endpoint info
    private static class Endpoint {
        final BlockPos pos;
        final BlockPos containerPos;
        final int power;
        Endpoint(BlockPos pos, BlockPos containerPos, int power) {
            this.pos = pos;
            this.containerPos = containerPos;
            this.power = power;
        }
    }

    // Helper to check if a block is an endpoint (copper block, cut copper, chiseled copper, and all variants)
    private static boolean isEndpointBlock(Material mat) {
        return BOTH_ENDPOINTS.contains(mat) || OUTPUT_ENDPOINTS.contains(mat) || INPUT_ENDPOINTS.contains(mat);
    }
} 