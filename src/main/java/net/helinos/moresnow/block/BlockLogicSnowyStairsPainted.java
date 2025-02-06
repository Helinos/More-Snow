package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

public class BlockLogicSnowyStairsPainted<T extends BlockLogic> extends BlockLogicSnowy<T> implements IBlockLogicSnowyStairs, IBlockLogicSnowyRotation {
	public BlockLogicSnowyStairsPainted(Block<T> block) {
		super(block, 4, 4, true);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return id == getStoredBlockId(metadata) && (metadata & 8) == 0;
	}

	@Override
	public boolean tryMakeSnowy(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta))
			return false;
		if (world.getBlockId(x, y + 1, z) == 0) {
			world.setBlockAndMetadataWithNotify(x, y + 1, z, MSBlocks.SNOWY_PARTIAL.id(), meta << 2);
		}
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id(), this.blockToMetadata(id, meta));
	}

	@Override
	public boolean tryMakeSnowy(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta))
			return false;
		if (chunk.getBlockID(x, y + 1, z) == 0) {
			chunk.setBlockIDWithMetadata(x, y + 1, z, MSBlocks.SNOWY_PARTIAL.id(), meta << 2);
		}
		return chunk.setBlockIDWithMetadata(x, y, z, this.block.id(), this.blockToMetadata(id, meta));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		Block<?> blockAbove = world.getBlock(x, y + 1, z);
		int metadata = world.getBlockMetadata(x, y, z);

		if (blockAbove != null && blockAbove.getLogic() instanceof BlockLogicSnowyPartial) {
			BlockLogicSnowyPartial<?> blockSnowyPartial = (BlockLogicSnowyPartial<?>) blockAbove.getLogic();
			int aboveMetadata = world.getBlockMetadata(x, y + 1, z);
			int aboveLayers = blockSnowyPartial.getLayers(aboveMetadata);

			if (aboveLayers != this.getLayers(metadata)) {
				world.setBlockMetadata(x, y, z, (metadata & 0b11111100) | aboveLayers - 1);
			}
		} else {
			this.removeSnow(world, metadata, x, y, z);
		}
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

	@Override
	public boolean isSolidRender() {
		return false;
	}
  
	@Override
	public boolean isCubeShaped() {
		return false;
	}
}
