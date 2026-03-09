package net.helinos.moresnow.model;

import net.helinos.moresnow.block.BlockLogicSnowyFenceGate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.phys.AABB;

public class BlockModelSnowyFenceGate<T extends BlockLogic> extends BlockModelSnowy<T> {
    public BlockModelSnowyFenceGate(Block<T> block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        BlockLogicSnowyFenceGate<?> logic = (BlockLogicSnowyFenceGate<?>) this.block.getLogic();
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
        AABB bounds = this.block.getBounds();

        boolean somethingRendered = false;

        if (logic.getLayers(metadata) != 8) {
            int direction = logic.getDirection(metadata);
            boolean isOpen = logic.isOpen(metadata);

            if (direction != 3 && direction != 1) {
                bounds.set(0.0, 0.3125, 0.4375, 0.125, 1.0, 0.5625);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.3125, 0.4375, 1.0, 1.0, 0.5625);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            } else {
                bounds.set(0.4375, 0.3125, 0.0, 0.5625, 1.0, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.4375, 0.3125, 0.875, 0.5625, 1.0, 1.0);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            }

            if (!isOpen) {
                if (direction != 3 && direction != 1) {
                    bounds.set(0.375, 0.375, 0.4375, 0.5, 0.9375, 0.5625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.5, 0.375, 0.4375, 0.625, 0.9375, 0.5625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.625, 0.375, 0.4375, 0.875, 0.5625, 0.5625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.625, 0.75, 0.4375, 0.875, 0.9375, 0.5625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.125, 0.375, 0.4375, 0.375, 0.5625, 0.5625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.125, 0.75, 0.4375, 0.375, 0.9375, 0.5625);
                    somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
                } else {
                    bounds.set(0.4375, 0.375, 0.375, 0.5625, 0.9375, 0.5);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.4375, 0.375, 0.5, 0.5625, 0.9375, 0.625);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.4375, 0.375, 0.625, 0.5625, 0.5625, 0.875);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.4375, 0.75, 0.625, 0.5625, 0.9375, 0.875);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.4375, 0.375, 0.125, 0.5625, 0.5625, 0.375);
                    this.renderStandardBlock(tessellator, bounds, x, y, z);
                    bounds.set(0.4375, 0.75, 0.125, 0.5625, 0.9375, 0.375);
                    somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
                }
            } else if (direction == 3) {
                bounds.set(0.8125, 0.375, 0.0, 0.9375, 0.9375, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.8125, 0.375, 0.875, 0.9375, 0.9375, 1.0);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.5625, 0.375, 0.0, 0.8125, 0.5625, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.5625, 0.375, 0.875, 0.8125, 0.5625, 1.0);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.5625, 0.75, 0.0, 0.8125, 0.9375, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.5625, 0.75, 0.875, 0.8125, 0.9375, 1.0);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            } else if (direction == 1) {
                bounds.set(0.0625, 0.375, 0.0, 0.1875, 0.9375, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.0625, 0.375, 0.875, 0.1875, 0.9375, 1.0);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.1875, 0.375, 0.0, 0.4375, 0.5625, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.1875, 0.375, 0.875, 0.4375, 0.5625, 1.0);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.1875, 0.75, 0.0, 0.4375, 0.9375, 0.125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.1875, 0.75, 0.875, 0.4375, 0.9375, 1.0);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            } else if (direction == 0) {
                bounds.set(0.0, 0.375, 0.8125, 0.125, 0.9375, 0.9375);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.375, 0.8125, 1.0, 0.9375, 0.9375);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.0, 0.375, 0.5625, 0.125, 0.5625, 0.8125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.375, 0.5625, 1.0, 0.5625, 0.8125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.0, 0.75, 0.5625, 0.125, 0.9375, 0.8125);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.75, 0.5625, 1.0, 0.9375, 0.8125);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            } else if (direction == 2) {
                bounds.set(0.0, 0.375, 0.0625, 0.125, 0.9375, 0.1875);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.375, 0.0625, 1.0, 0.9375, 0.1875);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.0, 0.375, 0.1875, 0.125, 0.5625, 0.4375);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.375, 0.1875, 1.0, 0.5625, 0.4375);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.0, 0.75, 0.1875, 0.125, 0.9375, 0.4375);
                this.renderStandardBlock(tessellator, bounds, x, y, z);
                bounds.set(0.875, 0.75, 0.1875, 1.0, 0.9375, 0.4375);
                somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);
            }
        }

        this.renderingSnow = true;

        int layers = logic.getLayers(metadata);
        double height = layers * 2 / 16.0;
        bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);

        this.renderingSnow = false;

        return somethingRendered;
    }
}
