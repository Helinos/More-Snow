package net.helinos.moresnow.model;

import net.helinos.moresnow.block.BlockLogicSnowyFence;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;

public class BlockModelSnowyFence<T extends BlockLogic> extends BlockModelSnowy<T> {
    public BlockModelSnowyFence(Block<T> block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        BlockLogicSnowyFence<?, ?> logic = (BlockLogicSnowyFence<?, ?>) this.block.getLogic();
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        boolean somethingRendered = false;

        if (logic.getLayers(metadata) != 8) {
            // Center post
            AABB bounds = AABB.getTemporaryBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
            this.renderStandardBlock(tessellator, bounds, x, y, z);

            boolean connectEast = logic.canConnectTo(renderBlocks.blockAccess, x - 1, y, z);
            boolean connectWest = logic.canConnectTo(renderBlocks.blockAccess, x + 1, y, z);
            boolean connectNorth = logic.canConnectTo(renderBlocks.blockAccess, x, y, z - 1);
            boolean connectSouth = logic.canConnectTo(renderBlocks.blockAccess, x, y, z + 1);
            boolean renderEastWest = connectEast || connectWest;
            boolean renderNorthSouth = connectNorth || connectSouth;

            float east = connectEast ? 0.0F : 0.4375F;
            float west = connectWest ? 1.0F : 0.5625F;
            float north = connectNorth ? 0.0F : 0.4375F;
            float south = connectSouth ? 1.0F : 0.5625F;

            // Upper connecting posts
            if (renderEastWest) {
                bounds.set(east, 0.75, 0.4375, west, 0.9375, 0.5625);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
            }

            if (renderNorthSouth) {
                bounds.set(0.4375, 0.75, north, 0.5625, 0.9375, south);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
            }

            // Lower connecting posts
            if (renderEastWest) {
                bounds.set(east, 0.375, 0.4375, west, 0.5625, 0.5625);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
            }

            if (renderNorthSouth) {
                bounds.set(0.4375, 0.375, north, 0.5625, 0.5625, south);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
            }
        }

        this.renderingSnow = true;

        int layers = logic.getLayers(metadata);
        double height = layers * 2 / 16.0;
        AABB bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);

        this.renderingSnow = false;

        return somethingRendered;
    }
}
