package net.helinos.moresnow.block;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.BlockLogicSlab;
import net.minecraft.core.block.BlockLogicStairs;
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
import org.jetbrains.annotations.Nullable;

public abstract class BlockLogicSnowy<T extends BlockLogic> extends BlockLogic {
    private final int maxLayers;
    private final int lowestLayerHeight;
    private final boolean supportsOwnSnow;

    public BlockLogicSnowy(Block<T> block, int maxLayers, int lowestLayerHeight, boolean supportsOwnSnow) {
        super(block, Material.snow);

		this.maxLayers = maxLayers;
        this.lowestLayerHeight = lowestLayerHeight;
        this.supportsOwnSnow = supportsOwnSnow;
    }

    /**
	 * Check a given block id with given metadata is capable of being replaced by a
	 * snow covered block.
	 */
    public boolean canReplaceBlock(int id, int metadata) {
        return id == getStoredBlockId(metadata);
    }

    public abstract int getStoredBlockMetadata(int metadata);

    public abstract int getStoredBlockId(int metadata);

    protected abstract int blockToMetadata(int blockId, int metadata);

    public int getLayers(int metadata) {
		return (metadata & (this.getMaxLayers() - 1)) + 1;
	}

    public int getMaxLayers() {
		return this.maxLayers;
	};

	public int getLowestLayerHeight() {
		return this.lowestLayerHeight;
	};

    public int getRelativeLayers(int metadata) {
		int layers = this.getLayers(metadata);
		return layers + this.getLowestLayerHeight();
	}

    /**
	 * Check if the block can support having snow on it.
	 *
	 * @see BlockLogicSnowyMultiple#canSupportSnow(Chunk, int, int, int)
	 */
	public boolean canSupportSnow(World world, int x, int y, int z) {
		Block<?> belowBlock = world.getBlock(x, y - 1, z);
		int belowMetadata = world.getBlockMetadata(x, y - 1, z);

		return this.canSupportSnow(belowBlock, belowMetadata);
	}

	/**
	 * Check if the block can support having snow on it.
	 *
	 * @see BlockSnowy$canSupportSnow(World, int, int, int)
	 */
	public boolean canSupportSnow(Chunk chunk, int x, int y, int z) {
		int belowID = chunk.getBlockID(x, y - 1, z);
		int belowMetadata = chunk.getBlockMetadata(x, y - 1, z);
		Block<?> belowBlock = Blocks.getBlock(belowID);

		return this.canSupportSnow(belowBlock, belowMetadata);
	}

	private boolean canSupportSnow(@Nullable Block<?> belowBlock, int metadata) {
		if (this.supportsOwnSnow) {
			return true;
		} else if (belowBlock == null) {
			return false;
		}

		Material belowMaterial = belowBlock.getMaterial();

		if (belowBlock.getLogic() instanceof BlockLogicSlab) {
			int slabState = metadata & 3;

			if (slabState != 0) {
				return true;
			}
		} else if (belowBlock.getLogic() instanceof BlockLogicStairs) {
			int stairsState = metadata & 8;

			if (stairsState != 0) {
				return true;
			}
		}

		if (
			belowBlock == Blocks.ICE ||
			(!belowBlock.isSolidRender() && !(belowBlock.getLogic() instanceof BlockLogicLeavesBase))
		) {
			return false;
		} else {
			return belowMaterial == Material.leaves || belowMaterial.blocksMotion();
		}
	}

    public boolean getSupportsOwnSnow() {
        return this.supportsOwnSnow;
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
		if (!this.canReplaceBlock(id, meta) || !canSupportSnow(chunk, x, y, z))
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

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (!this.canSupportSnow(world, x, y, z)) {
			this.removeSnow(world, world.getBlockMetadata(x, y, z), x, y, z);
		}
	}
}
