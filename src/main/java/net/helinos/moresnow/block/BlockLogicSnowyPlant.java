package net.helinos.moresnow.block;

import java.util.ArrayList;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSnowyPlant<T extends BlockLogic> extends BlockLogicSnowy<T> {
	public BlockLogicSnowyPlant(Block<T> block, Class<BlockLogicFlower> blockLogic,
			ArrayList<Integer> excludedIds) {
		super(block, Material.topSnow, blockLogic, excludedIds.stream().mapToInt(i -> i).toArray(), false, false);
	}

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = (layers + 1) * 2 / 16.0f;
		return AABB.getTemporaryBB(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}
  
	@Override
	public boolean isCubeShaped() {
		return false;
	}
}
