package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class BlockLogicSnowy<T extends BlockLogic> extends BlockLogic {
	protected final Map<Integer, Integer> METADATA_TO_BLOCK_ID;
	protected final List<Integer> USED_IDS;
	protected int metadataID = 0;

	public BlockLogicSnowy(Block<T> block, Class<?> blockLogicClass, List<Integer> excludedIds) {
		super(block, Material.snow);
		if (excludedIds != null) {
			this.METADATA_TO_BLOCK_ID = this.initMetadataToBlockId(blockLogicClass, excludedIds);
		} else {
			this.METADATA_TO_BLOCK_ID = Collections.unmodifiableMap(new HashMap<Integer, Integer>());
		}
		

		if (METADATA_TO_BLOCK_ID != null) {
			this.USED_IDS = METADATA_TO_BLOCK_ID.values().stream().map(i -> i).collect(Collectors.toList());
		} else {
			this.USED_IDS = new ArrayList<>();
		}
	}

	protected Map<Integer, Integer> initMetadataToBlockId(Class<?> blockLogic, List<Integer> excludedIds) {
		Hashtable<Integer, Integer> tmp = new Hashtable<>();
		for (Block<?> b : Blocks.blocksList) {
			if (b == null)
				continue;
			int id = b.id();
			if (!blockLogic.isInstance(b.getLogic()) || excludedIds.contains(id))
				continue;
			tmp.put(this.metadataID++, id);
		}
		return Collections.unmodifiableMap(tmp);
	}
	
	/**
	 * Check a given block id with given metadata is capable of being replaced by a
	 * snow covered block.
	 */
	public boolean canReplaceBlock(int id, int metadata) {
		return this.METADATA_TO_BLOCK_ID.containsValue(id);
	}

	/**
	 * Check if the block can support having snow on it.
	 * 
	 * @see BlockLogicSnowy#canSupportSnow(Chunk, int, int, int)
	 */
	public boolean canSupportSnow(World world, int x, int y, int z) {
		int belowID = world.getBlockId(x, y - 1, z);
		Material belowMaterial = world.getBlockMaterial(x, y - 1, z);

		return this.canSupportSnow(belowID, belowMaterial);
	}

	/**
	 * Check if the block can support having snow on it.
	 * 
	 * @see BlockSnowy$canSupportSnow(World, int, int, int)
	 */
	public boolean canSupportSnow(Chunk chunk, int x, int y, int z) {
		int belowID = chunk.getBlockID(x, y - 1, z);
		Material belowMaterial = chunk.world.getBlockMaterial(x, y, z);

		return this.canSupportSnow(belowID, belowMaterial);
	}

	private boolean canSupportSnow(int belowID, Material belowMaterial) {
		if (this.supportsOwnSnow()) {
			return true;
		}

		if (
			belowID == 0 || 
			belowID == Blocks.ICE.id() ||
			(!Blocks.blocksList[belowID].isSolidRender() && !(Blocks.blocksList[belowID].getLogic() instanceof BlockLogicLeavesBase))
		) {
			return false;
		} else {
			return belowMaterial == Material.leaves || belowMaterial.blocksMotion();
		}
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(World world, int id, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.tryMakeSnowy(world, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id   The block id to be "stored" inside the snow covered block
	 * @param meta The metadata to be "stored"
	 * @return Whether the block was placed successfully
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta) || !canSupportSnow(world, x, y, z))
			return false;
		return world.setBlockAndMetadataWithNotify(x, y, z, this.block.id(), this.blockToMetadata(id, meta));
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(Chunk chunk, int id, int x, int y, int z) {
		int meta = chunk.getBlockMetadata(x, y, z);
		return this.tryMakeSnowy(chunk, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id   The block id to be "stored" inside the snow covered block
	 * @param meta The metadata to be "stored"
	 * @return Whether the block was placed successfully
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockLogicSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta) || !!canSupportSnow(chunk, x, y, z))
			return false;
		return chunk.setBlockIDWithMetadata(x, y, z, this.block.id(), this.blockToMetadata(id, meta));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockLogicSnowy#removeSnow(Chunk, int, int, int, int)
	 */
	public void removeSnow(World world, int metadata, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockLogicSnowy#removeSnow(World, int, int, int, int)
	 */
	public void removeSnow(Chunk chunk, int metadata, int x, int y, int z) {
		chunk.setBlockIDWithMetadata(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	// Vanilla accumulate function but get layers from function rather than directly
	// from metadata
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		if (layers >= this.getMaxLayers()) {
			return;
		}
		int relativeLayers = this.getRelativeLayers(metadata);

		if (!this.isBlockValid(world, x + 1, y, z, relativeLayers)) {
			return;
		}
		if (!this.isBlockValid(world, x, y, z + 1, relativeLayers)) {
			return;
		}
		if (!this.isBlockValid(world, x - 1, y, z, relativeLayers)) {
			return;
		}
		if (!this.isBlockValid(world, x, y, z - 1, relativeLayers)) {
			return;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata + 1);
	}

	private boolean isBlockValid(World world, int x, int y, int z, int relativeLayers) {
		return world.isBlockOpaqueCube(x, y, z) || this.isSnow(world, x, y, z) && this.getOthersLayers(world, x , y, z) >= relativeLayers;
	};

	/**
	 * @return True if the block at the given coordinates is either snow covered or
	 *         a snow layer
	 */
	private boolean isSnow(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);

		return ArrayUtils.contains(MSBlocks.blockIds, id) || id == Blocks.LAYER_SNOW.id();
	}

	/**
	 * @return How many layers a block at the given coordinates has, presuming the
	 *         block is snowy or a layer block
	 */
	private int getOthersLayers(World world, int x, int y, int z) {
		Block<?> block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		if (block.getLogic() instanceof BlockLogicSnowy) {
			return ((BlockLogicSnowy<?>) block.getLogic()).getRelativeLayers(metadata);
		}

		return metadata;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, Side side, int metadata, Player player,
			Item item) {
		this.removeSnow(world, metadata, x, y, z);
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case SILK_TOUCH: {
				return new ItemStack[] { new ItemStack(Blocks.LAYER_SNOW, this.getLayers(meta) + 1) };
			}
			case PICK_BLOCK: {
				return new ItemStack[] { new ItemStack(Blocks.LAYER_SNOW) };
			}
			case PROPER_TOOL: {
				return new ItemStack[] { new ItemStack(Items.AMMO_SNOWBALL, this.getLayers(meta) + 1) };
			}
			case IMPROPER_TOOL: {
				return null;
			}
			default: { // Drop the underlying block if it's destroyed by WORLD or EXPLOSION
				Block<?> block = Blocks.getBlock(this.getStoredBlockId(meta));
				if (block != null) {
					return block.getBreakResult(world, dropCause, x, y, z, this.getStoredBlockMetadata(meta), tileEntity);
				}
			}
		}
		
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
			int metadata = world.getBlockMetadata(x, y, z);
			//this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, metadata, null, null);
			this.removeSnow(world, metadata, x, y, z);
		}
		if (
			world.getBlockBiome(x, y, z) != null && 
			!world.getBlockBiome(x, y, z).hasSurfaceSnow() && 
			world.seasonManager.getCurrentSeason() != null &&
			world.seasonManager.getCurrentSeason().letWeatherCleanUpSnow
		) {
			int metadata = world.getBlockMetadata(x, y, z);
			//this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
			this.removeSnow(world, metadata, x, y, z);
		}
	}

	public int getLayers(int metadata) {
		return (metadata & (this.getMaxLayers() - 1)) + 1;
	}

	public int getRelativeLayers(int metadata) {
		int layers = getLayers(metadata);
		return layers + this.getLowestLayerHeight();
	}

	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> 4) & 0b00001111;
		return this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	public int getStoredBlockMetadata(int metadata) {
		return 0;
	}

	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 4;
			}
		}

		return 0;
	}

	public abstract boolean supportsOwnSnow();

	public int getMaxLayers() {
		return 8;
	};

	public int getLowestLayerHeight() {
		return 0;
	};
}
