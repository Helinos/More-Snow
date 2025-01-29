package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicStairs;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class BlockLogicSnowyStairs<T extends BlockLogic, S extends BlockLogicStairs> extends BlockLogicSnowy<T> {
	public BlockLogicSnowyStairs(Block<T> block, Class<S> blockLogic, List<Integer> excludedIds) {
		super(block, blockLogic, excludedIds);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	protected Map<Integer, Integer> initMetadataToBlockId(Class<?> blockLogic, List<Integer> excludedIds) {
		Hashtable<Integer, Integer> tmp = new Hashtable<>();
		for (Block<?> b : Blocks.blocksList) {
			if (this.metadataID == 16) {
				break;
			}
			if (b == null)
				continue;
			int id = b.id();
			if (!blockLogic.isInstance(b.getLogic()) || excludedIds.contains(id))
				continue;
			tmp.put(this.metadataID++, id);
		}
		return Collections.unmodifiableMap(tmp);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		if (super.canReplaceBlock(id, metadata)) {
			return (metadata & 0b1000) == 0;
		}

		return false;
	}

	@Override
	public boolean tryMakeSnowy(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta))
			return false;
		if (world.getBlockId(x, y + 1, z) == 0) {
			world.setBlockAndMetadataWithNotify(x, y + 1, z, MSBlocks.SNOWY_PARTIAL.id(), meta << 2);
		}
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id(), this.blockToMetadata(id, meta));
	}

	@Override
	public boolean tryMakeSnowy(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta))
			return false;
		if (chunk.getBlockID(x, y + 1, z) == 0) {
			chunk.setBlockIDWithMetadata(x, y + 1, z, MSBlocks.SNOWY_PARTIAL.id(), meta << 2);
		}
		return chunk.setBlockIDWithMetadata(x, y, z, this.block.id(), this.blockToMetadata(id, meta));
	}

	@Override
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int blockIdAbove = world.getBlockId(x, y + 1, z);

		if (blockIdAbove == 0) {
			world.setBlockAndMetadata(x, y, z, MSBlocks.SNOWY_PARTIAL.id(), metadata & 0b1111);
		}

		super.accumulate(world, x, y, z);
	}

	@Override
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
		int metadata = world.getBlockMetadata(x, y, z);
		int rotation = this.getRotation(metadata);
		int layers = this.getLayers(metadata);
		double heightFromSnow = layers * 2 / 16.0;
		if (rotation == 0) {
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 0.5, 0.5 + heightFromSnow, 1.0).move(x, y, z), aabbList);
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
		} else if (rotation == 1) {
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0).move(x, y, z), aabbList);
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.5, 0.0, 0.0, 1.0, 0.5 + heightFromSnow, 1.0).move(x, y, z), aabbList);
		} else if (rotation == 2) {
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.5 + heightFromSnow, 0.5).move(x, y, z), aabbList);
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
		} else {
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5).move(x, y, z), aabbList);
			this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.5, 1.0, 0.5 + heightFromSnow, 1.0).move(x, y, z), aabbList);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		Block<?> blockAbove = world.getBlock(x, y + 1, z);
		int metadata = world.getBlockMetadata(x, y, z);
		if (blockAbove != null && blockAbove.getLogic() instanceof BlockLogicSnowyPartial) {
			BlockLogicSnowyPartial<?> blockSnowyPartial = (BlockLogicSnowyPartial<?>) blockAbove.getLogic();
			int aboveMetadata = world.getBlockMetadata(x, y + 1, z);
			int aboveLayers = blockSnowyPartial.getLayers(aboveMetadata);

			if (aboveLayers != this.getLayers(metadata)) {
				world.setBlockMetadata(x, y, z, (metadata & 0b11111100) | aboveLayers - 1);
			}
		}
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		return getRotation(metadata);
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return (metadata << 2) | super.blockToMetadata(blockId, metadata);
	}

	public int getRotation(int metadata) {
		return (metadata >> 2) & 0b11;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
  
	@Override
	public boolean isCubeShaped() {
		return false;
	}

	@Override
	public boolean supportsOwnSnow() {
		return true;
	}

	@Override
	public int getMaxLayers() {
		return 4;
	};

	@Override
	public int getLowestLayerHeight() {
		return 4;
	};
}
