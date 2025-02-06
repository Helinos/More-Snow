package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.helinos.moresnow.block.BlockLogicSnowyPlant;
import net.helinos.moresnow.block.IBlockLogicSnowyStairs;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlockLayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlockLayer.class, remap = false)
public class ItemBlockLayerMixin {
	@Inject(method = "onUseItemOnBlock", at = @At("HEAD"), cancellable = true)
	private void onUseItemOnBlock(ItemStack itemstack, Player player, World world, int blockX, int blockY,
			int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		int blockId = world.getBlockId(blockX, blockY, blockZ);
		int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
		Block<?> block = Blocks.getBlock(blockId);

		if (itemstack.stackSize <= 0) {
			cir.setReturnValue(false);
			return;
		}
		if (blockY == world.getHeightBlocks() - 1 && itemstack.itemID == Blocks.LAYER_SNOW.id()) {
			cir.setReturnValue(false);
			return;
		}

		// Incrementing layer count on snow covered blocks with the snow layer item
		if (itemstack.itemID == Blocks.LAYER_SNOW.id() && side == Side.TOP && block.getLogic() instanceof BlockLogicSnowy) {
			BlockLogicSnowy<?> blockSnowy = (BlockLogicSnowy<?>) block.getLogic();
			int newLayers = blockSnowy.getLayers(metadata) + 1;

			AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, block.getBounds().maxX, block.getBounds().maxY + 0.125f, block.getBounds().maxZ);
			if (!world.checkIfAABBIsClear(bbBox)) {
				cir.setReturnValue(false);
				return;
			}

			if (block.getLogic() instanceof BlockLogicSnowyPlant) {
				if (newLayers <= blockSnowy.getMaxLayers()) {
					world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, block.id(), (metadata & ~(blockSnowy.getMaxLayers() - 1)) | newLayers - 1);
				} else {
					int storedID = ((BlockLogicSnowyPlant<?, ?>) block.getLogic()).getStoredBlockId(metadata);
					block.getLogic().dropBlockWithCause(world, EnumDropCause.WORLD, blockX, blockY, blockZ, metadata, null, null);
					world.playBlockSoundEffect(player, blockX, blockY, blockZ, Blocks.getBlock(storedID), EnumBlockSoundEffectType.DIG);
					world.setBlockWithNotify(blockX, blockY, blockZ, Blocks.BLOCK_SNOW.id());
				}
			} else {
				if (newLayers <= blockSnowy.getMaxLayers()) {
					if (block.getLogic() instanceof IBlockLogicSnowyStairs && world.getBlock(blockX, blockY + 1, blockZ) == null) {
						world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, MSBlocks.SNOWY_PARTIAL.id(), newLayers - 1);
					}
					world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, block.id(), (metadata & ~(blockSnowy.getMaxLayers() - 1)) | newLayers - 1);
				} else {
					return;
				}
			}

			world.playBlockSoundEffect((Entity) player, (double) blockX + 0.5d, (double) blockY + 0.5d, (double) blockZ + 0.5d, Blocks.LAYER_SNOW, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(player);
			cir.setReturnValue(true);
			return;
		}

		// Cover blocks that can be covered
		if (itemstack.itemID == Blocks.LAYER_SNOW.id()) {
			if (!MSBlocks.tryMakeSnowy(world, blockId, blockX, blockY, blockZ))
				return;

			world.playBlockSoundEffect((Entity) player, (double) blockX + 0.5d, (double) blockY + 0.5d, (double) blockZ + 0.5d, Blocks.LAYER_SNOW, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(player);
			cir.setReturnValue(true);
		}
	}
}
