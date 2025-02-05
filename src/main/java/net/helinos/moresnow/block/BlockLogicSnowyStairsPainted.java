package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowyStairsPainted<T extends BlockLogic> extends BlockLogicSnowy<T> implements IBlockLogicSnowyStairs {
	public BlockLogicSnowyStairsPainted(Block<T> block) {
		super(block, 4, 4, true);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return id == getStoredBlockId(metadata) && (metadata & 8) == 0;
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

	@Override
	public int getRotation(int metadata) {
		return (metadata >> 2) & 0b11;
	}
}
