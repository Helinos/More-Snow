package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public abstract class BlockModelSnowy<T extends BlockLogic> extends BlockModelStandard<T> {
    public static final IconCoordinate SNOW_TEXTURE = TextureRegistry.getTexture("minecraft:block/block_snow");

    public BlockModelSnowy(Block<T> block) {
        super(block);
    }

    @Override
    public IconCoordinate getBlockTexture(WorldSource blockAccess, int x, int y, int z, Side side) {
        int metadata = blockAccess.getBlockMetadata(x, y, z);
        return this.getBlockTextureFromSideAndMetadata(side, metadata);
    }

    @Override
    public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int metadata) {
        BlockLogicSnowy<?> logic = (BlockLogicSnowy<?>) this.block.getLogic();
        int storedBlockID = logic.getStoredBlockId(metadata);
        Block<?> storedBlock = Blocks.getBlock(storedBlockID);
        int storedBlockMetadata = logic.getStoredBlockMetadata(metadata);

        try {
            return BlockModelDispatcher.getInstance().getDispatch(storedBlock).getBlockTextureFromSideAndMetadata(side,
                    storedBlockMetadata);
        } catch (NullPointerException _exception) {
            return BLOCK_TEXTURE_UNASSIGNED;
        }
    }
}
