package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFencePainted;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowyFencePainted<T extends BlockLogic> extends BlockLogicSnowyFence<T, BlockLogicFencePainted> {
    public BlockLogicSnowyFencePainted(Block<T> block, Class<BlockLogicFencePainted> blockLogic) {
        super(block, blockLogic, null);
    }

    @Override
    public int getStoredBlockId(int metadata) {
        return Blocks.FENCE_PLANKS_OAK_PAINTED.id();
    }

    @Override
    public int getStoredBlockMetadata(int metadata) {
        return (metadata >> 4) & 0b00001111;
    }

    @Override
    protected int blockToMetadata(int blockId, int metadata) {
        return (metadata << 4) & 0b11110000;
    }
}
