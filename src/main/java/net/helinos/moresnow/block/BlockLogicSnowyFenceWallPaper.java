package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFenceWallPaper;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSnowyFenceWallPaper<T extends BlockLogic> extends BlockLogicSnowyFenceThin<T, BlockLogicFenceWallPaper> {
    public BlockLogicSnowyFenceWallPaper(Block<T> block) {
        super(block, Blocks.FENCE_PAPER_WALL.id(), BlockLogicFenceWallPaper.class);
    }

    @Override
    public boolean canConnectTo(WorldSource world, int x, int y, int z) {
        int blockID = world.getBlockId(x, y, z);
        return Blocks.hasTag(blockID, BlockTags.FENCES_CONNECT);
    }
}
