package net.helinos.moresnow.block;

import net.helinos.moresnow.block.model.BlockModelSnowyFence;
import net.helinos.moresnow.block.model.BlockModelSnowyPlant;
import net.helinos.moresnow.block.model.BlockModelSnowySlab;
import net.helinos.moresnow.block.model.BlockModelSnowyStairs;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.core.block.Block;
import turniplabs.halplibe.helper.ModelHelper;
import turniplabs.halplibe.util.ModelEntrypoint;

public class MSModels implements ModelEntrypoint {
    @Override
	public void initBlockModels(BlockModelDispatcher dispatcher) {
        ModelHelper.setBlockModel(MSBlocks.SNOWY_PLANT, () -> new BlockModelSnowyPlant<>(MSBlocks.SNOWY_PLANT).setAllTextures(0, "minecraft:block/block_snow"));
        
        for (Block<?> block : MSBlocks.SNOWY_FLOWER_STACKABLES) {
            ModelHelper.setBlockModel(block, () -> new BlockModelSnowyPlant<>(block).setAllTextures(0, "minecraft:block/block_snow"));
        }
        
        ModelHelper.setBlockModel(MSBlocks.SNOWY_SLAB, () -> new BlockModelSnowySlab<>(MSBlocks.SNOWY_SLAB).setAllTextures(0, "minecraft:block/block_snow"));
        ModelHelper.setBlockModel(MSBlocks.SNOWY_SLAB_PAINTED, () -> new BlockModelSnowySlab<>(MSBlocks.SNOWY_SLAB_PAINTED).setAllTextures(0, "minecraft:block/block_snow"));
    
        for (Block<?> block : MSBlocks.SNOWY_STAIRS) {
            ModelHelper.setBlockModel(block, () -> new BlockModelSnowyStairs<>(block).setAllTextures(0, "minecraft:block/block_snow"));
        }

        ModelHelper.setBlockModel(MSBlocks.SNOWY_STAIRS_PAINTED, () -> new BlockModelSnowyStairs<>(MSBlocks.SNOWY_STAIRS_PAINTED).setAllTextures(0, "minecraft:block/block_snow"));
        ModelHelper.setBlockModel(MSBlocks.SNOWY_PARTIAL, () -> new BlockModelStandard<>(MSBlocks.SNOWY_PARTIAL).setAllTextures(0, "minecraft:block/block_snow"));
        ModelHelper.setBlockModel(MSBlocks.SNOWY_FENCE, () -> new BlockModelSnowyFence<>(MSBlocks.SNOWY_FENCE).setAllTextures(0, "minecraft:block/block_snow"));
        ModelHelper.setBlockModel(MSBlocks.SNOWY_FENCE_PAINTED, () -> new BlockModelSnowyFence<>(MSBlocks.SNOWY_FENCE_PAINTED).setAllTextures(0, "minecraft:block/block_snow"));
    }

    @Override
    public void initItemModels(ItemModelDispatcher dispatcher) {}

    @Override
    public void initEntityModels(EntityRenderDispatcher dispatcher) {}

    @Override
    public void initTileEntityModels(TileEntityRenderDispatcher dispatcher) {}

    @Override
    public void initBlockColors(BlockColorDispatcher dispatcher) {}
}
