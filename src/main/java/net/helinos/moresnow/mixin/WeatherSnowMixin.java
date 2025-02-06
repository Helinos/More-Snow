package net.helinos.moresnow.mixin;

import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.block.BlockLogicSnowy;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFence;
import net.minecraft.core.block.BlockLogicFenceThin;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherSnow;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = WeatherSnow.class, remap = false)
public abstract class WeatherSnowMixin extends Weather {
	// So sorry if anyone else tries to inject into this method in the future, but the way I originally implemented this
	// was simply too hard for me to wrap my head around, especially when trying to update this to newer versions
	// of BTA. When this inevitably causes an incompatibilty with another mod let me know and I'll fix it.
	
	public WeatherSnowMixin(int id) {
		super(id);
	}
	
	@Inject(method = "doEnvironmentUpdate", at = @At(value = "HEAD"), cancellable = true)
	private void doEnvironmentUpdate(World world, Random random, int x, int z, CallbackInfo callbackInfo) {
		callbackInfo.cancel();
		
		double probability = 64.0 * 1.0 / world.weatherManager.getWeatherPower();
		boolean biomeHasDeeperSnow = world.getSeasonManager().getCurrentSeason() == null ? false : world.getSeasonManager().getCurrentSeason().hasDeeperSnow;
		if (biomeHasDeeperSnow) {
			probability /= 2;
		}

		boolean snowWillFall = random.nextInt((int) probability) == 0;
		// All snowy blocks have the snow material so they all technically "block motion".
		// Thus, this function will always return the y value of the block above them.
		int y = world.findTopSolidBlock(x, z);
		
		int blockIDBelow = world.getBlockId(x, y - 1, z);
		Block<?> blockBelow = Blocks.getBlock(blockIDBelow);
		BlockLogic blockBelowLogic = blockBelow != null ? blockBelow.getLogic() : null;
		
		while (blockBelowLogic instanceof BlockLogicFence || blockBelowLogic instanceof BlockLogicFenceThin) {
			y -= 1;
			blockBelow = world.getBlock(x, y - 1, z);
			blockBelowLogic = blockBelow != null ? blockBelow.getLogic() : null;
		};
		
		int blockID = world.getBlockId(x, y, z);
		
		Biome biome = world.getBlockBiome(x, y, z);

		if (ArrayUtils.contains(biome.blockedWeathers, ((WeatherSnow) (Object) this))
			|| world.weatherManager.getWeatherPower() <= 0.6 
			|| y < 0
			|| y >= world.getHeightBlocks()
			|| world.getSavedLightValue(LightLayer.Block, x, y, z) >= 10
		) {
			return;
		}

		if (blockIDBelow != 0) {
			if (blockID == 0
				&& Blocks.LAYER_SNOW.canPlaceBlockAt(world, x, y, z)
				&& blockIDBelow != Blocks.ICE.id()
			) {
				world.setBlockWithNotify(x, y, z, Blocks.LAYER_SNOW.id());
				return;
			}

			if (MSBlocks.tryMakeSnowy(world, blockID, x, y, z)) {
				return;
			}
			
			if (MSBlocks.tryMakeSnowy(world, blockIDBelow, x, y - 1, z)) {
				return;
			}
		}
		
		if (
			(
				blockID == Blocks.LAYER_SNOW.id()
				|| blockBelowLogic instanceof BlockLogicSnowy
			)
			&& world.getSeasonManager().getCurrentSeason() != null
			&& (biomeHasDeeperSnow || biome == Biomes.OVERWORLD_GLACIER)
		) {
			if (!snowWillFall) {
				return;
			}
			
			if (blockID == Blocks.LAYER_SNOW.id()) {
				Blocks.LAYER_SNOW.getLogic().accumulate(world, x, y, z);
			} else if (blockBelowLogic != null) {
				((BlockLogicSnowy<?>) blockBelowLogic).accumulate(world, x, y - 1, z);
			}

			return;
		}
		
		if (
			blockIDBelow == Blocks.FLUID_WATER_STILL.id()
			&& world.getBlockMetadata(x, y - 1, z) == 0
			&& random.nextFloat() < world.weatherManager.getWeatherPower() * world.weatherManager.getWeatherIntensity()
		) {
			for(Direction direction : Direction.horizontalDirections) {
				Block<?> block = world.getBlock(x + direction.getOffsetX(), y - 1, z + direction.getOffsetZ());
				if (block == Blocks.ICE || block != null && block.isSolidRender()) {
					world.setBlockWithNotify(x, y - 1, z, Blocks.ICE.id());
					break;
				}
			}
		}
	}

	@Inject(method = "doChunkLoadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;getBlockID(III)I", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo, int x, int worldX, int z, int worldZ, int y, Biome biome, int blockId) {
		if (
			y < 0 
			|| y >= world.getHeightBlocks() 
			|| chunk.getBrightness(LightLayer.Block, x, y, z) >= 10
		) {
			return;
		}

		if (MSBlocks.tryMakeSnowy(chunk, blockId, x, y, z)) {
			return;
		}

		int blockIDBelow = chunk.getBlockID(x, y - 1, z);
		MSBlocks.tryMakeSnowy(chunk, blockIDBelow, x, y - 1, z);
	}
}
