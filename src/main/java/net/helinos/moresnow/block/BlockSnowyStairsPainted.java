package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;

public class BlockSnowyStairsPainted extends BlockSnowyStairs {
	public BlockSnowyStairsPainted(String key, int id, Material material, int minId, int maxId, int[] excludedIds, boolean fourLayers, boolean weirdShape) {
		super(key, id, material, minId, maxId, excludedIds, fourLayers, weirdShape);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return Block.stairsPlanksOakPainted.id;
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		int rotation = this.getRotation(metadata);
		return (metadata & 0b11110000) | rotation;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		int rotation = (metadata & 0b11) << 2;
		return (metadata & 0b11110000) | rotation;
	}
}
