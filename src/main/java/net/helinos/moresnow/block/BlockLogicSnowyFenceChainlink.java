package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFenceChainlink;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSnowyFenceChainlink<T extends BlockLogic> extends BlockLogicSnowyFenceThin<T, BlockLogicFenceChainlink> {
    public BlockLogicSnowyFenceChainlink(Block<T> block) {
       super(block, Blocks.FENCE_CHAINLINK.id(), BlockLogicFenceChainlink.class);
    }
 
    @Override
    public boolean canConnectTo(WorldSource world, int x, int y, int z) {
       Block<?> block = world.getBlock(x, y, z);
       return BlockTags.CHAINLINK_FENCES_CONNECT.appliesTo(block) || block != null && (block.getMaterial().isStone() || block.getMaterial().isMetal());
    }
 
    @Override
    public boolean isClimbable(World world, int x, int y, int z) {
       return true;
    }
 }