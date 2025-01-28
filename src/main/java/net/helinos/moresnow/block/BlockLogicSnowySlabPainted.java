package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicSlabPainted;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowySlabPainted<T extends BlockLogic> extends BlockLogicSnowySlab<T, BlockLogicSlabPainted> {
	public BlockLogicSnowySlabPainted(Block<T> block, Class<BlockLogicSlabPainted> blockLogic,
			int[] excludedIds) {
		super(block, blockLogic, excludedIds);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return Blocks.SLAB_PLANKS_PAINTED.id();
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		return metadata & 0b11110000;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return metadata;
	}
}
