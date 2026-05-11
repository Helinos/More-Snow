package net.helinos.moresnow.model;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;

public class BlockModelSnowySlab<T extends BlockLogic> extends BlockModelSnowy<T> {
    public BlockModelSnowySlab(Block<T> block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        // Render the slab
        AABB bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        boolean somethingRendered = this.renderStandardBlock(tessellator, bounds, x, y, z);

        // Render the snow
        this.renderingSnow = true;
        int layers = ((BlockLogicSnowy<?>) block.getLogic()).getLayers(metadata);
        double height = layers * 2 / 16.0;

        bounds.set(0.0, 0.5, 0.0, 1.0, 0.5 + height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        this.renderingSnow = false;

        return somethingRendered;
    }
}
