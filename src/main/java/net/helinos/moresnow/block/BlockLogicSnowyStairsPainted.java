package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicStairs;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowyStairsPainted<T extends BlockLogic> extends BlockLogicSnowyStairs<T> {
	public BlockLogicSnowyStairsPainted(Block<T> block, Class<BlockLogicStairs> blockLogic,
			int[] excludedIds) {
		super(block, blockLogic, excludedIds);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return Blocks.STAIRS_PLANKS_PAINTED.id();
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
