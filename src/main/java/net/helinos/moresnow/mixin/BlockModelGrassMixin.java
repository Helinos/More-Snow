package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.minecraft.client.render.block.model.BlockModelGrass;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockModelGrass.class, remap = false)
public class BlockModelGrassMixin {
	@Redirect(method = "getBlockTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/WorldSource;getBlockMaterial(III)Lnet/minecraft/core/block/material/Material;"))
	private Material correctSnowTexture(WorldSource blockAccess, int x, int y, int z) {
		Block<?> block = blockAccess.getBlock(x, y, z);
		if (block == null || !(block.getLogic() instanceof BlockLogicSnowy)) {
			Material material = blockAccess.getBlockMaterial(x, y, z);
			return material;
		}

		BlockLogicSnowy<?> logic = (BlockLogicSnowy<?>) block.getLogic();
		if (logic.supportsOwnSnow()) {
			return Material.stone;
		} else {
			return Material.topSnow;
		}
	}
}
