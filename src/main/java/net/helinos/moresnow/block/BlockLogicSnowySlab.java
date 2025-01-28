package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSlab;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

import java.util.List;
import java.util.Map;

public class BlockLogicSnowySlab<T extends BlockLogic, S extends BlockLogicSlab> extends BlockLogicSnowy<T>{
	public BlockLogicSnowySlab(Block<T> block, Class<S> blockLogic, List<Integer> excludedIds) {
		super(block, blockLogic, excludedIds);
		this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.625, 1.0);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		if (super.canReplaceBlock(id, metadata)) {
			return (metadata & 0b11) == 0;
		}

		return false;
	}

	// @Override
	// public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
	// 	int metadata = world.getBlockMetadata(x, y, z);
	// 	int layers = this.getLayers(metadata);
	// 	float height = layers * 2 / 16.0f;
	// 	return AABB.getTemporaryBB(x + this.bounds.minX, y + this.bounds.minY, z + this.bounds.minZ, x + this.bounds.maxX, y + 0.5f + height, z + this.bounds.maxZ);
	// }

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		double height = layers * 2 / 16.0;
		return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.5 + height, 1.0);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> 2) & 0b00111111;
		return (int) this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 2;
			}
		}

		return 0;
	}

	@Override
	public boolean isSolidRender() {
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
