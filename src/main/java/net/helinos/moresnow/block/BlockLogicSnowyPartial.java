package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.Map;

public class BlockLogicSnowyPartial<T extends BlockLogic> extends BlockLogicSnowy<T> {
	public BlockLogicSnowyPartial(Block<T> block) {
		super(block, Material.topSnow, null, new int[0], true, false);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
	}

	@Override
	protected Map<Integer, Integer> initMetadataToBlockId(Class<?> block, int[] excludedIds) {
		return null;
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return false;
	}

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int rotation = this.getRotation(metadata);
		int layers = this.getLayers(metadata);
		float heightFromSnow = (layers + 1) * 2 / 16.0f;
		if (rotation == 0) {
			return AABB.getTemporaryBB(0.5f, 0.0f, 0.0f, 1.0f, heightFromSnow, 1.0f);
		} else if (rotation == 1) {
			return AABB.getTemporaryBB(0.0f, 0.0f, 0.0f, 0.5f, heightFromSnow, 1.0f);
		} else if (rotation == 2) {
			return AABB.getTemporaryBB(0.0f, 0.0f, 0.5f, 1.0f, heightFromSnow, 1.0f);
		} else {
			return AABB.getTemporaryBB(0.0f, 0.0f, 0.0f, 1.0f, heightFromSnow, 0.5f);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		Block<?> blockBelow = world.getBlock(x, y - 1, z);
		if (blockBelow != null && blockBelow.getLogic() instanceof BlockLogicSnowyStairs) {
			BlockLogicSnowyStairs<?> blockSnowyStairs = (BlockLogicSnowyStairs<?>) blockBelow.getLogic();
			int metadata = world.getBlockMetadata(x, y, z);
			int belowMetadata = world.getBlockMetadata(x, y - 1, z);
			int belowLayers = blockSnowyStairs.getLayers(belowMetadata);

			if (belowLayers != this.getLayers(metadata)) {
				world.setBlockMetadata(x, y, z, (metadata & 0b11111100) | belowLayers);
			}
		} else {
			world.setBlockWithNotify(x, y, z, 0);
		}
	}

	@Override
	public int getRelativeLayers(int metadata) {
		return getLayers(metadata);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return 0;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return 0;
	}

	public int getRotation(int metadata) {
		return (metadata >> 2) & 0b11;
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
