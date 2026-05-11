package net.helinos.moresnow.model;

import net.helinos.moresnow.block.BlockLogicSnowy;
import net.helinos.moresnow.block.IBlockLogicSnowyRotation;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;

public class BlockModelSnowyStairs<T extends BlockLogic> extends BlockModelSnowy<T> {
    public BlockModelSnowyStairs(Block<T> block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        // Render the stairs
        boolean somethingRendered = false;
        BlockLogicSnowy<?> logic = (BlockLogicSnowy<?>) this.block.getLogic();
        int horizontalRotation = ((IBlockLogicSnowyRotation) logic).getRotation(metadata);

        AABB bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if (horizontalRotation == 0) {
            bounds.set(0.0, 0.0, 0.0, 0.5, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            bounds.set(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else if (horizontalRotation == 1) {
            bounds.set(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            bounds.set(0.5, 0.0, 0.0, 1.0, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else if (horizontalRotation == 2) {
            bounds.set(0.0, 0.0, 0.0, 1.0, 0.5, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            bounds.set(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else {
            bounds.set(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            bounds.set(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        }

        // Render the snow
        this.renderingSnow = true;
        int layers = logic.getLayers(metadata);
        double heightFromSnow = layers * 2 / 16.0;

        // Render the snow
        if (horizontalRotation == 0) {
            bounds.set(0.0, 0.5, 0.0, 0.5, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else if (horizontalRotation == 1) {
            bounds.set(0.5, 0.5, 0.0, 1.0, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else if (horizontalRotation == 2) {
            bounds.set(0.0, 0.5, 0.0, 1.0, 0.5 + heightFromSnow, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        } else {
            bounds.set(0.0, 0.5, 0.5, 1.0, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
        }
        this.renderingSnow = false;

        return somethingRendered;
    }
}
