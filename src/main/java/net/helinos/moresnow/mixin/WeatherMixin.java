package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = Weather.class, remap = false)
public class WeatherMixin {
	@Inject(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", shift = At.Shift.AFTER, ordinal = 1))
	private void doEnvironmentUpdate(World world, Random random, int x, int z, CallbackInfo ci) {
		int topY = world.findTopSolidBlock(x, z);
		for (int y = topY; y >= topY - 1; y--) {
			Block<?> block = world.getBlock(x, y, z);

			if (block == null) {
				continue;
			}

			if (block.getLogic() instanceof BlockLogicSnowy) {
				BlockLogicSnowy<?> blockSnowy = (BlockLogicSnowy<?>) block.getLogic();
				int metadata = world.getBlockMetadata(x, y, z);
				int layers = blockSnowy.getLayers(metadata);

				if (layers > 1) {
					world.setBlockMetadata(x, y, z, metadata - 1);
					world.markBlockNeedsUpdate(x, y, z);
				} else if (!world.getBlockBiome(x, y, z).hasSurfaceSnow()) {
					blockSnowy.removeSnow(world, metadata, x, y, z);
				}
			}
		}
	}

	// @Inject(method = "doChunkLoadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;getBlockID(III)I", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	// private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo, int x, int z, int y, int blockId) {
	// 	if (!world.getBlockBiome(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z).hasSurfaceSnow()) {
	// 		Block<?> block = Blocks.getBlock(blockId);

	// 		if (ArrayUtils.contains(MSBlocks.transparentIds, blockId)) {
	// 			BlockLogicSnowy<?> blockSnowy = (BlockLogicSnowy<?>) block.getLogic();
	// 			int metadata = chunk.getBlockMetadata(x, y, z);
	// 			blockSnowy.removeSnow(chunk, metadata, x, y, z);
	// 		}

	// 		int blockBelowId = chunk.getBlockID(x, y - 1, z);
	// 		block = Blocks.getBlock(blockBelowId);

	// 		if (ArrayUtils.contains(MSBlocks.solidIds, blockBelowId) && !(blockId == Blocks.LAYER_SNOW.id())) {
	// 			BlockLogicSnowy<?> blockSnowy = (BlockLogicSnowy<?>) block.getLogic();
	// 			int metadata = chunk.getBlockMetadata(x, y - 1, z);
	// 			blockSnowy.removeSnow(chunk, metadata, x, y - 1, z);
	// 		}
	// 	}
	// }
}
