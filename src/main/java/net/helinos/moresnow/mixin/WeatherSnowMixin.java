package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherSnow;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WeatherSnow.class, remap = false)
public abstract class WeatherSnowMixin extends Weather {
	// So sorry if anyone else tries to inject into these methods in the future, but the way I originally implemented this
	// was simply too hard for me to wrap my head around, especially when trying to update this to newer versions
	// of BTA. When this inevitably causes an incompatibilty with another mod I'll change it again.
	
	public WeatherSnowMixin(int id) {
		super(id);
	}
	
	@Inject(method = "doEnvironmentUpdate", at = @At(value = "HEAD"), cancellable = true)
	private void doEnvironmentUpdate(World world, Random random, int x, int z, CallbackInfo callbackInfo) {
		double probability = 64.0 * 1.0 / world.weatherManager.getWeatherPower();
		boolean biomeHasDeeperSnow = world.getSeasonManager().getCurrentSeason() == null ? false : world.getSeasonManager().getCurrentSeason().hasDeeperSnow;
		if (biomeHasDeeperSnow) {
			probability /= 2;
		}

		boolean snowWillFall = random.nextInt((int) probability) == 0;
		// All snowy blocks have the snow material so they all technically "block motion".
		// Thus, this function will always return the y value of the block above them.
		int y = world.findTopSolidBlock(x, z);
		int blockID = world.getBlockId(x, y, z);
		int blockIDBelow = world.getBlockId(x, y - 1, z);
		BlockLogic blockBelowLogic = Blocks.getBlock(blockIDBelow).getLogic();
		Biome biome = world.getBlockBiome(x, y, z);

		if (ArrayUtils.contains(biome.blockedWeathers, ((WeatherSnow) (Object) this)) ||
			world.weatherManager.getWeatherPower() <= 0.6 || 
			y < 0 ||
			y >= world.getHeightBlocks() ||
			world.getSavedLightValue(LightLayer.Block, x, y, z) >= 10
		) {
			callbackInfo.cancel();
			return;
		}

		if (blockIDBelow != 0) {
			if (
				blockID == 0 &&
				Blocks.LAYER_SNOW.canPlaceBlockAt(world, x, y, z) && 
				blockIDBelow != Blocks.ICE.id()
			) {
				world.setBlockWithNotify(x, y, z, Blocks.LAYER_SNOW.id());
				callbackInfo.cancel();
				return;
			} else if (MSBlocks.tryMakeSnowy(world, blockID, x, y, z)) {
				callbackInfo.cancel();
				return;
			} else if (MSBlocks.tryMakeSnowy(world, blockIDBelow, x, y - 1, z)) {
				callbackInfo.cancel();
				return;
			}
		}
		
		if (
			(
				blockID == Blocks.LAYER_SNOW.id() || 
				blockBelowLogic instanceof BlockLogicSnowy
			) &&
			world.getSeasonManager().getCurrentSeason() != null &&
			(biomeHasDeeperSnow || biome == Biomes.OVERWORLD_GLACIER)
		) {
			if (!snowWillFall) {
				callbackInfo.cancel();
				return;
			}
			
			if (blockID == Blocks.LAYER_SNOW.id()) {
				Blocks.LAYER_SNOW.getLogic().accumulate(world, x, y, z);
			} else {
				((BlockLogicSnowy<?>) blockBelowLogic).accumulate(world, x, y - 1, z);
			}

			callbackInfo.cancel();
			return;
		}
		
		if (
			blockIDBelow == Blocks.FLUID_WATER_STILL.id() && 
			world.getBlockMetadata(x, y - 1, z) == 0 &&
			random.nextFloat() < world.weatherManager.getWeatherPower() * world.weatherManager.getWeatherIntensity()
		) {
			for(Direction direction : Direction.horizontalDirections) {
				Block<?> block = world.getBlock(x + direction.getOffsetX(), y - 1, z + direction.getOffsetZ());
				if (block == Blocks.ICE || block != null && block.isSolidRender()) {
					world.setBlockWithNotify(x, y - 1, z, Blocks.ICE.id());
					break;
				}
			}
		}

		callbackInfo.cancel();
	}

	// @Unique
	// private static boolean snowFell = false;
	// @Unique
	// private static boolean snowCoverHack = false;
	// @Unique
	// private static int idToStore = 0;
	// @Unique
	// private static int metadataToStore = 0;
	// @Unique
	// private static BlockLogicSnowy<?> snowCoverType = null;

	// @ModifyVariable(method = "doEnvironmentUpdate", at = @At("STORE"), ordinal = 0)
	// private boolean snow(boolean snow) {
	// 	snowFell = snow;
	// 	return snow;
	// }

	// @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;findTopSolidBlock(II)I"))
	// private int findTopBlock(World world, int x, int z) {
	// 	Chunk chunk = world.getChunkFromBlockCoords(x, z);
	// 	int testY;
	// 	int y = -1;
	// 	int chunkX = x;
	// 	int chunkZ = z;
	// 	chunkX &= 15;
	// 	chunkZ &= 15;

	// 	for (testY = world.getHeightBlocks() - 1; testY > 0; --testY) {
	// 		int id = chunk.getBlockID(chunkX, testY, chunkZ);
	// 		Block<?> block = Blocks.blocksList[id];
	// 		Material material = id != 0 ? block.getMaterial() : Material.air;
	// 		if ((material.blocksMotion() && !(block.getLogic() instanceof BlockLogicFence)) || material.isLiquid()) {
	// 			y = testY + 1;
	// 			break;
	// 		}
	// 	}

	// 	int idAbove = world.getBlockId(x, y, z);
	// 	int id = world.getBlockId(x, y - 1, z);
	// 	int metadata = world.getBlockMetadata(x, y - 1, z);

	// 	if ((MSBlocks.whichCanReplaceSolid(id, metadata) != null // Lower if block can be converted to snowy block
	// 			|| ArrayUtils.contains(MSBlocks.solidIds, id)) // Lower if block is snowy block for accumulate function
	// 			&& idAbove != Blocks.LAYER_SNOW.id()) {
	// 		return y - 1;
	// 	}

	// 	return y;
	// }

	// @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 0))
	// private int blockIdHack(World world, int x, int y, int z) {
	// 	int id = world.getBlockId(x, y, z);
	// 	if (!snowFell)
	// 		return id;
	// 	Block<?> block = Blocks.getBlock(id);

	// 	if (block != null && block.getLogic() instanceof BlockLogicSnowy) {
	// 		snowCoverHack = true;
	// 		return Blocks.LAYER_SNOW.id();
	// 	}

	// 	int metadata = world.getBlockMetadata(x, y, z);
	// 	BlockLogicSnowy<?> blockSnowy = MSBlocks.whichCanReplace(id, metadata);

	// 	if (blockSnowy != null) {
	// 		snowCoverType = blockSnowy;
	// 		idToStore = id;
	// 		metadataToStore = metadata;
	// 		return 0;
	// 	}

	// 	return id;
	// }

	// @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 1))
	// private int blockIdBelowHack(World world, int x, int y, int z) {
	// 	int id = world.getBlockId(x, y + 1, z);
	// 	int metadata = world.getBlockMetadata(x, y + 1, z);

	// 	if (MSBlocks.whichCanReplaceSolid(id, metadata) != null) {
	// 		return Blocks.STONE.id();
	// 	}

	// 	return world.getBlockId(x, y, z);
	// }

	// // @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target =
	// // "Lnet/minecraft/core/block/Block;canPlaceBlockAt(Lnet/minecraft/core/world/World;III)Z"))
	// // private boolean canPlaceBlockAt(Block block, World world, int x, int y, int
	// // z) {
	// // if (!(snowCoverType instanceof BlockSnowyPlant)) {
	// // return true;
	// // }

	// // BlockLayerSnow blockLayerSnow = (BlockLayerSnow) block;
	// // return blockLayerSnow.canPlaceBlockAt(world, x, y, z);
	// // }

	// @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z", ordinal = 0))
	// private boolean placeCoverOrSnow(World world, int x, int y, int z, int id) {
	// 	if (snowCoverType != null) {
	// 		boolean placed = snowCoverType.tryMakeSnowy(world, idToStore, metadataToStore, x, y, z);
	// 		snowCoverType = null;
	// 		idToStore = 0;
	// 		metadataToStore = 0;
	// 		return placed;
	// 	}

	// 	return world.setBlockWithNotify(x, y, z, Blocks.LAYER_SNOW.id());
	// }

	// @Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/BlockLogicLayerSnow;accumulate(Lnet/minecraft/core/world/World;III)V"))
	// private void accumulate(BlockLogicLayerSnow blockLayerSnow, World world, int x, int y, int z) {
	// 	int layers;
	// 	int metadata = world.getBlockMetadata(x, y, z);
	// 	Block<?> block = world.getBlock(x, y, z);

	// 	boolean snowCoverHack = WeatherSnowMixin.snowCoverHack;
	// 	WeatherSnowMixin.snowCoverHack = false;

	// 	if (snowCoverHack) {
	// 		try {
	// 			layers = ((BlockLogicSnowy<?>) block.getLogic()).getRelativeLayers(metadata);
	// 		} catch (ClassCastException ignored) {
	// 			MoreSnow.LOGGER.warn("snowCoverHack was true when it shouldn't have been");
	// 			snowCoverHack = false;
	// 			layers = metadata;
	// 		}
	// 	} else {
	// 		layers = metadata;
	// 	}

	// 	for (Direction direction : Direction.horizontalDirections) {
	// 		int neighborX = x + direction.getOffsetX();
	// 		int neighborZ = z + direction.getOffsetZ();

	// 		int neighborId = world.getBlockId(neighborX, y, neighborZ);
	// 		int belowNeighborId = world.getBlockId(neighborX, y - 1, neighborZ);
	// 		Block<?> neighborBlock = Blocks.getBlock(neighborId);

	// 		// If the neighboring block can support snow
	// 		if (Blocks.LAYER_SNOW.canPlaceBlockAt(world, x, y, z) && belowNeighborId != 0) {
	// 			if (neighborId == 0) {
	// 				world.setBlockWithNotify(x, y, z, Blocks.LAYER_SNOW.id());
	// 				return;
	// 			} else if (MSBlocks.tryMakeSnowyTransparent(world, neighborId, neighborX, y, neighborZ)) {
	// 				return;
	// 			}
	// 		}

	// 		if (MSBlocks.tryMakeSnowySolid(world, neighborId, neighborX, y, neighborZ))
	// 			return;

	// 		// Check if the neighboring block is a snow cover and get how many layers it has
	// 		int neighborMetadata = world.getBlockMetadata(neighborX, y, neighborZ);
	// 		int neighborLayers;
	// 		if (neighborBlock == Blocks.LAYER_SNOW) {
	// 			neighborLayers = neighborMetadata;
	// 		} else if (neighborBlock != null && neighborBlock.getLogic() instanceof BlockLogicSnowy) {
	// 			neighborLayers = ((BlockLogicSnowy<?>) neighborBlock.getLogic()).getRelativeLayers(neighborMetadata);
	// 		} else {
	// 			continue;
	// 		}

	// 		// Accumulate the neighbor if its snow is lower than this one
	// 		if (layers > neighborLayers) {
	// 			if (neighborBlock != null && neighborBlock.getLogic() instanceof BlockLogicLayerSnow) {
	// 				((BlockLogicLayerSnow) neighborBlock.getLogic()).accumulate(world, neighborX, y, neighborZ);
	// 			} else if (neighborBlock != null && neighborBlock.getLogic() instanceof BlockLogicSnowy) {
	// 				((BlockLogicSnowy<?>) neighborBlock.getLogic()).accumulate(world, neighborX, y, neighborZ);
	// 			}

	// 			return;
	// 		}
	// 	}

	// 	if (snowCoverHack) {
	// 		((BlockLogicSnowy<?>) block.getLogic()).accumulate(world, x, y, z);
	// 	} else {
	// 		blockLayerSnow.accumulate(world, x, y, z);
	// 	}
	// }

	// @Inject(method = "doChunkLoadEffect", at = @At("HEAD"))
	// private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo) {
	// 	int chunkCornerX = chunk.xPosition * 16;
	// 	int chunkCornerZ = chunk.zPosition * 16;
	// 	int chunkCornerY = chunk.getHeightValue(0, 0);
	// 	Biome biome = world.getBlockBiome(chunkCornerX, chunkCornerY, chunkCornerZ);
	// 	if (ArrayUtils.contains(biome.blockedWeathers, this))
	// 		return;

	// 	for (int chunkX = 0; chunkX < 16; ++chunkX) {
	// 		for (int chunkZ = 0; chunkZ < 16; ++chunkZ) {
	// 			int y = chunk.getHeightValue(chunkX, chunkZ);
	// 			if (world.weatherManager.getWeatherPower() <= 0.6f || y < 0 || y >= world.getHeightBlocks()
	// 					|| chunk.getBrightness(LightLayer.Block, chunkX, y, chunkZ) >= 10)
	// 				continue;

	// 			int id = chunk.getBlockID(chunkX, y, chunkZ);
	// 			MSBlocks.tryMakeSnowyTransparent(chunk, id, chunkX, y, chunkZ);

	// 			id = chunk.getBlockID(chunkX, y - 1, chunkZ);
	// 			MSBlocks.tryMakeSnowySolid(chunk, id, chunkX, y - 1, chunkZ);
	// 		}
	// 	}
	// }
}
