package net.helinos.moresnow.model;

import net.helinos.moresnow.block.BlockLogicSnowyFenceThin;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFenceThin;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;

public class BlockModelSnowyFenceThin<T extends BlockLogic, F extends BlockLogicFenceThin> extends BlockModelSnowy<T> {
    private final IconCoordinate normalTexture;
    private final IconCoordinate bottomTexture;
    private final IconCoordinate topTexture;
    private final IconCoordinate columnTexture;
    private final Class<F> storedBlockLogicClass;

    public BlockModelSnowyFenceThin(Block<T> block, Class<F> storedBlockLogicClass, IconCoordinate normalTexture, IconCoordinate bottomTexture, IconCoordinate topTexture, IconCoordinate columnTexture) {
        super(block);
        this.normalTexture = normalTexture;
        this.bottomTexture = bottomTexture;
        this.topTexture = topTexture;
        this.columnTexture = columnTexture;
        this.storedBlockLogicClass = storedBlockLogicClass;
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        BlockLogicSnowyFenceThin<?, ?> logic = (BlockLogicSnowyFenceThin<?, ?>) this.block.getLogic();
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        boolean somethingRendered = false;

        // Render fence
        if (logic.getLayers(metadata) != 8) {
            float brightness = 1.0F;
            if (LightmapHelper.isLightmapEnabled()) {
                tessellator.setLightmapCoord(this.block.getLightmapCoord(renderBlocks.blockAccess, x, y, z));
            } else {
                brightness = this.getBlockBrightness(renderBlocks.blockAccess, x, y, z);
            }

            float brightnessBottom = brightness;
            float brightnessTop = brightness;
            float brightnessNorthSouth = brightness;
            float brightnessEastWest = brightness;
            if (RenderBlocks.enableDirectionalLight) {
               brightnessBottom *= 0.5F;
               brightnessNorthSouth *= 0.8F;
               brightnessEastWest *= 0.6F;
            }

            if (logic.shouldDrawColumn(renderBlocks.blockAccess, x, y, z)) {
                double onePixel = 1.0 / 16.0;

                double minU = this.columnTexture.getIconUMin();
                double minV = this.columnTexture.getIconVMin();
                double maxU = this.columnTexture.getSubIconU(onePixel * 2.0);
                double maxV = this.columnTexture.getIconVMax();

                double minX = x + onePixel * 7.0;
                double minY = y;
                double minZ = z + onePixel * 7.0;
                double maxX = x + onePixel * 9.0;
                double maxY = y + 1.0;
                double maxZ = z + onePixel * 9.0;

                tessellator.setColorOpaque_F(brightnessNorthSouth, brightnessNorthSouth, brightnessNorthSouth);
                tessellator.addVertexWithUV(maxX, minY, minZ, minU, maxV);
                tessellator.addVertexWithUV(minX, minY, minZ, maxU, maxV);
                tessellator.addVertexWithUV(minX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, maxY, minZ, minU, minV);
                tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
                tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);

                tessellator.setColorOpaque_F(brightnessEastWest, brightnessEastWest, brightnessEastWest);
                tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
                tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
                tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV);
                tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);

                minU = this.columnTexture.getIconUMin();
                minV = this.columnTexture.getIconVMin();
                maxU = this.columnTexture.getSubIconU(onePixel * 2.0);
                maxV = this.columnTexture.getSubIconV(onePixel * 2.0);

                tessellator.setColorOpaque_F(brightnessTop, brightnessTop, brightnessTop);
                tessellator.addVertexWithUV(minX, maxY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);

                tessellator.setColorOpaque_F(brightnessBottom, brightnessBottom, brightnessBottom);
                tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
                tessellator.addVertexWithUV(maxX, minY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
            }

            for (Direction direction : Direction.horizontalDirections) {
                if (!logic.canConnectTo(renderBlocks.blockAccess, x + direction.getOffsetX(), y, z + direction.getOffsetZ())) {
                    continue;
                }

                boolean northOrSouth = direction == Direction.NORTH || direction == Direction.SOUTH;
                boolean northOrWest = direction == Direction.NORTH || direction == Direction.WEST;
                boolean southOrEast = direction == Direction.SOUTH || direction == Direction.EAST;

                float b;
                if ( direction == Direction.NORTH || direction == Direction.SOUTH) {
                    b = brightnessEastWest;
                } else {
                    b = brightnessNorthSouth;
                }
                tessellator.setColorOpaque_F(b, b, b);

                Block<?> blockAbove = renderBlocks.blockAccess.getBlock(x, y + 1, z);
                BlockLogic blockAboveLogic = blockAbove != null ? blockAbove.getLogic() : null;
                boolean connectUp = blockAboveLogic == null ? false : this.storedBlockLogicClass.isInstance(blockAboveLogic) && logic.canConnectTo(renderBlocks.blockAccess, x + direction.getOffsetX(), y + 1, z + direction.getOffsetZ());
                Block<?> blockBelow = renderBlocks.blockAccess.getBlock(x, y - 1, z);
                BlockLogic blockBelowLogic = blockBelow != null ? blockBelow.getLogic() : null;
                boolean connectDown = blockBelowLogic == null ? false : this.storedBlockLogicClass.isInstance(blockBelowLogic)  && logic.canConnectTo(renderBlocks.blockAccess, x + direction.getOffsetX(), y - 1, z + direction.getOffsetZ());

                double minU;
                double minV;
                double maxU;
                double maxV;

                if (this.bottomTexture != null && !connectDown) {
                    minU = southOrEast ? this.bottomTexture.getSubIconU(0.5) : this.bottomTexture.getIconUMin();
                    minV = this.bottomTexture.getIconVMin();
                    maxU = northOrWest ? this.bottomTexture.getSubIconU(0.5) : this.bottomTexture.getIconUMax();
                    maxV = this.bottomTexture.getIconVMax();
                } else if (this.topTexture != null && !connectUp) {
                    minU = southOrEast ? this.topTexture.getSubIconU(0.5) : this.topTexture.getIconUMin();
                    minV = this.topTexture.getIconVMin();
                    maxU = northOrWest ? this.topTexture.getSubIconU(0.5) : this.topTexture.getIconUMax();
                    maxV = this.topTexture.getIconVMax();
                } else {
                    minU = southOrEast ? this.normalTexture.getSubIconU(0.5): this.normalTexture.getIconUMin();
                    minV = this.normalTexture.getIconVMin();
                    maxU = northOrWest ? this.normalTexture.getSubIconU(0.5) : this.normalTexture.getIconUMax();
                    maxV = this.normalTexture.getIconVMax();
                }

                double centerX = direction == Direction.WEST ? x : x + 0.5;
                double minY = y;
                double centerZ = direction == Direction.NORTH ? z : z + 0.5;
                double farX = direction == Direction.EAST ? x + 1.0 : x + 0.5;
                double maxY = y + 1.0;
                double farZ = direction == Direction.SOUTH ? z + 1.0 :z + 0.5;

                double x1 = northOrSouth ? farX : centerX;
                double x2 = northOrSouth ? centerX : farX;
                double z1 = northOrSouth ? farZ : centerZ;
                double z2 = northOrSouth ? centerZ : farZ;

                tessellator.addVertexWithUV(farX, minY, z1, minU, maxV);
                tessellator.addVertexWithUV(x1, minY, centerZ, maxU, maxV);
                tessellator.addVertexWithUV(x1, maxY, centerZ, maxU, minV);
                tessellator.addVertexWithUV(farX, maxY, z1, minU, minV);
                tessellator.addVertexWithUV(centerX, minY, z2, maxU, maxV);
                tessellator.addVertexWithUV(x2, minY, farZ, minU, maxV);
                tessellator.addVertexWithUV(x2, maxY, farZ, minU, minV);
                tessellator.addVertexWithUV(centerX, maxY, z2, maxU, minV);
            }
        }

        // Render snow
        this.renderingSnow = true;

        int layers = logic.getLayers(metadata);
        double height = layers * 2 / 16.0;
        AABB bounds = AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, bounds, x, y, z);

        this.renderingSnow = false;

        return somethingRendered;
    }
}
