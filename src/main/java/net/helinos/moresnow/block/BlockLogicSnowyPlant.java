package net.helinos.moresnow.block;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicSnowyPlant<T extends BlockLogic, F extends BlockLogicFlower> extends BlockLogicSnowy<T> {
	public boolean killedByWeather;
	
	public BlockLogicSnowyPlant(Block<T> block, Class<F> blockLogic, ArrayList<Integer> excludedIds) {
		super(block, blockLogic, excludedIds);
		block.setTicking(true);
	}

	@Override
	public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		double height = layers * 2 / 16.0;
		return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> 3) & 0b00001111;
		return this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	@Override
    public int getStoredBlockMetadata(int metadata) {
        return (metadata) & 0b10000000;
    }

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 3;
			}
		}

		return 0;
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
			world.setBlockAndMetadataWithNotify(x, y, z, Blocks.LAYER_SNOW.id(), this.getLayers(z) - 1);
		}
	}

	public boolean getKilledByWeather(int metadata) {
		int blockID = this.getStoredBlockId(metadata);
		Block<?> block = Blocks.getBlock(blockID);
		if (block != null && block.getLogic() instanceof BlockLogicFlower) {
			return ((BlockLogicFlower) block.getLogic()).killedByWeather;
		}
			
		return false;
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
