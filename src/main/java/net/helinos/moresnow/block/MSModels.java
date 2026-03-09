package net.helinos.moresnow.block;

import net.helinos.moresnow.model.BlockModelSnowyFence;
import net.helinos.moresnow.model.BlockModelSnowyFenceGate;
import net.helinos.moresnow.model.BlockModelSnowyFenceThin;
import net.helinos.moresnow.model.BlockModelSnowyPlant;
import net.helinos.moresnow.model.BlockModelSnowySlab;
import net.helinos.moresnow.model.BlockModelSnowyStairs;
import net.minecraft.client.render.EntityRenderDispatcher;
import net.minecraft.client.render.TileEntityRenderDispatcher;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
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

        ModelHelper.setBlockModel(
            MSBlocks.SNOWY_FENCE_WALLPAPER,
            () -> new BlockModelSnowyFenceThin<>(
                MSBlocks.SNOWY_FENCE_WALLPAPER,
                Blocks.FENCE_PAPER_WALL.getLogic().getClass(),
                TextureRegistry.getTexture("minecraft:block/fence_paper/center"),
                null,
                null,
                TextureRegistry.getTexture("minecraft:block/fence_paper/column")
            ).setAllTextures(0, "minecraft:block/block_snow"));

        ModelHelper.setBlockModel(
            MSBlocks.SNOWY_FENCE_STEEL,
            () -> new BlockModelSnowyFenceThin<>(
                MSBlocks.SNOWY_FENCE_STEEL,
                Blocks.FENCE_STEEL.getLogic().getClass(),
                TextureRegistry.getTexture("minecraft:block/fence_steel/center"),
                null,
                TextureRegistry.getTexture("minecraft:block/fence_steel/top"),
                TextureRegistry.getTexture("minecraft:block/fence_steel/column")
            ).setAllTextures(0, "minecraft:block/block_snow"));

        ModelHelper.setBlockModel(
            MSBlocks.SNOWY_FENCE_CHAINLINK,
            () -> new BlockModelSnowyFenceThin<>(
                MSBlocks.SNOWY_FENCE_CHAINLINK,
                Blocks.FENCE_CHAINLINK.getLogic().getClass(),
                TextureRegistry.getTexture("minecraft:block/fence_chain/center"),
                null,
                TextureRegistry.getTexture("minecraft:block/fence_chain/top"),
                TextureRegistry.getTexture("minecraft:block/fence_chain/column")
            ).setAllTextures(0, "minecraft:block/block_snow"));

        ModelHelper.setBlockModel(MSBlocks.SNOWY_FENCE_GATE, () -> new BlockModelSnowyFenceGate<>(MSBlocks.SNOWY_FENCE_GATE).setAllTextures(0, "minecraft:block/block_snow"));

        for (Block<?> block : MSBlocks.SNOWY_FENCE_GATES_PAINTED) {
            ModelHelper.setBlockModel(block, () -> new BlockModelSnowyFenceGate<>(block).setAllTextures(0, "minecraft:block/block_snow"));
        }
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
