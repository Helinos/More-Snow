package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSlab;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

import java.util.Map;

public class BlockLogicSnowySlab<T extends BlockLogic, S extends BlockLogicSlab> extends BlockLogicSnowy<T>{
	public BlockLogicSnowySlab(Block<T> block, Class<S> blockLogic,
			int[] excludedIds) {
		super(block, Material.snow, blockLogic, excludedIds, true, true);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.625f, 1.0f);
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
		float height = (layers + 1) * 2 / 16.0f;
		return AABB.getTemporaryBB(0.0f, 0.0f, 0.0f, 1.0f, 0.5f + height, 1.0f);
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
}
