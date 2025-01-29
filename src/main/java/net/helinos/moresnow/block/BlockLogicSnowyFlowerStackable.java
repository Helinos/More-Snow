package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlowerStackable;

public class BlockLogicSnowyFlowerStackable<T extends BlockLogic> extends BlockLogicSnowyPlant<T, BlockLogicFlowerStackable> {
	private int storedBlockID;
	
	public BlockLogicSnowyFlowerStackable(Block<T> block, int storedBlockID) {
		super(block, null, null);
		this.storedBlockID = storedBlockID;
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return id == storedBlockID;
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return storedBlockID;
	}

	@Override
    public int getStoredBlockMetadata(int metadata) {
        return (metadata) & 0b11100000;
    }

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return metadata;
	}
}
