package net.helinos.moresnow.block;

import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFence;
import net.minecraft.core.block.Blocks;

public class BlockLogicSnowyFence<T extends BlockLogic, F extends BlockLogicFence> extends BlockLogicSnowy<T> {
    public BlockLogicSnowyFence(Block<T> block, Class<F> blockLogic, List<Integer> excludedIds) {
        super(block, blockLogic, excludedIds);
        this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    public boolean canConnectTo(WorldSource worldSource, int x, int y, int z) {
        int blockID = worldSource.getBlockId(x, y, z);
        return Blocks.hasTag(blockID, BlockTags.FENCES_CONNECT);
    }

    @Override
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
        int metadata = world.getBlockMetadata(x, y, z);
        int layers = this.getLayers(metadata);
        double height = layers * 2 / 16.0;

        this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0).move(x, y, z), aabbList);

        boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
        boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
        boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
        boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);

        AABB bounds = AABB.getTemporaryBB(
                0.0 + (connectXNeg ? 0.0 : 0.375),
                0.0,
                0.0 + (connectZNeg ? 0.0 : 0.375),
                1.0 - (connectXPos ? 0.0 : 0.375),
                1.5,
                1.0 - (connectZPos ? 0.0 : 0.375)
            ).move(x, y, z);
        this.addIntersectingBoundingBox(aabb, bounds, aabbList);
    }

    @Override
    public boolean canPlaceOnSurface() {
        return true;
    }

    @Override
    public boolean isSolidRender() {
        return false;
    }
  
    @Override
    public boolean isCubeShaped() {
        return false;
    }

    @Override
	public boolean supportsOwnSnow() {
		return false;
	}
}
