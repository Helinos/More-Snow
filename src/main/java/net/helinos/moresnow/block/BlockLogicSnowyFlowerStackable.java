package net.helinos.moresnow.block;

import java.util.Random;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSnowyFlowerStackable<T extends BlockLogic> extends BlockLogicSnowy<T> implements IBlockLogicPlant {
	private int storedBlockID;
	
	public BlockLogicSnowyFlowerStackable(Block<T> block, int storedBlockID) {
		super(block, 8, 0, false);
		block.setTicking(true);

		this.storedBlockID = storedBlockID;
	}

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		double height = layers * 2 / 16.0;
		return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		super.updateTick(world, x, y, z, random);
		
		int metadata = world.getBlockMetadata(x, y, z);
		
		if (
			!BlockLogicFlower.isPermanent(metadata) &&
			world.getGameRuleValue(GameRules.DO_SEASONAL_GROWTH) &&
			world.getSeasonManager().getCurrentSeason() != null &&
			world.getSeasonManager().getCurrentSeason().killFlowers &&
			this.getKilledByWeather(metadata) &&
			random.nextInt(256) == 0
		) {
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
			world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAYER_SNOW.id(), this.getLayers(metadata) - 1);
		}
	}

	@Override
	public boolean getKilledByWeather(int metadata) {
		int blockID = this.getStoredBlockId(metadata);
		return BlockLogicSnowyPlant.doGetKilledByWeather(blockID);
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

	@Override
	public boolean isSolidRender() {
		return false;
	}
  
	@Override
	public boolean isCubeShaped() {
		return false;
	}
}
