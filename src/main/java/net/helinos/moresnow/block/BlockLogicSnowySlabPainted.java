package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowySlabPainted<T extends BlockLogic> extends BlockLogicSnowy<T> {
	public BlockLogicSnowySlabPainted(Block<T> block) {
		super(block, 4, 4, true);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return id == getStoredBlockId(metadata) && (metadata & 3) == 0;
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

	@Override
	public boolean isSolidRender() {
		return false;
	}
}
