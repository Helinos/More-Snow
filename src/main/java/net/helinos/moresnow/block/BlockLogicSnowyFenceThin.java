package net.helinos.moresnow.block;

import java.util.ArrayList;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFenceThin;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public abstract class BlockLogicSnowyFenceThin<T extends BlockLogic, F extends BlockLogicFenceThin> extends BlockLogicSnowy<T> {
    private final int storedBlockID;
    private final Class<F> storedBlockLogic;
    
    public BlockLogicSnowyFenceThin(Block<T> block, int storedBlockID, Class<F> storedBlockLogic) {
        super(block, 8, 0, false);
        this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.storedBlockID = storedBlockID;
        this.storedBlockLogic = storedBlockLogic;
    }

    public abstract boolean canConnectTo(WorldSource worldSource, int i, int j, int k);

    @Override
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
        int metadata = world.getBlockMetadata(x, y, z);
        int layers = this.getLayers(metadata);
        double height = layers * 2 / 16.0;

        this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0).move(x, y, z), aabbList);

        boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
        boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
        boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
        boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);

        AABB bounds = AABB.getTemporaryBB(
                0.0 + (connectXNeg ? 0.0 : 0.375),
                0.0,
                0.0 + (connectZNeg ? 0.0 : 0.375),
                1.0 - (connectXPos ? 0.0 : 0.375),
                1.0,
                1.0 - (connectZPos ? 0.0 : 0.375)
            ).move(x, y, z);
        this.addIntersectingBoundingBox(aabb, bounds, aabbList);
    }

    public boolean shouldDrawColumn(WorldSource world, int x, int y, int z) {
        if (this.shouldDrawColumnDo(world, x, y, z)) {
           return true;
        }
        
        int offsetY = 0;
        while (true) {
            offsetY++;
            Block<?> block = world.getBlock(x, y + offsetY, z);
            
            if (block == null) {
                break;
            }

            if (!storedBlockLogic.isInstance(block.getLogic())) {
                break;
            }
        }
        offsetY -= 1;

        boolean drawColumnFromOther = false;
        while(storedBlockLogic.isInstance(world.getBlock(x, y + offsetY, z).getLogic())) {
            if (this.shouldDrawColumnDo(world, x, y + offsetY, z)) {
                drawColumnFromOther = true;
                break;
            };
            offsetY--;
        }

        return drawColumnFromOther;
    }

    private boolean shouldDrawColumnDo(WorldSource world, int x, int y, int z) {
        boolean connectNorth = this.canConnectTo(world, x + Direction.NORTH.getOffsetX(), y, z + Direction.NORTH.getOffsetZ());
        boolean connectSouth = this.canConnectTo(world, x + Direction.SOUTH.getOffsetX(), y, z + Direction.SOUTH.getOffsetZ());
        boolean connectEast = this.canConnectTo(world, x + Direction.EAST.getOffsetX(), y, z + Direction.EAST.getOffsetZ());
        boolean connectWest = this.canConnectTo(world, x + Direction.WEST.getOffsetX(), y, z + Direction.WEST.getOffsetZ());
        
        boolean hasNorthOrSouth = connectNorth || connectSouth;
        boolean hasEastOrWest = connectEast || connectWest;
        
        if (hasNorthOrSouth && hasEastOrWest) {
            return true;
        }

        boolean lineNorthSouth = connectNorth && connectSouth;
        boolean lineEastWest = connectEast && connectWest;

        if (lineNorthSouth || lineEastWest) {
            return false;
        }

        return true;
    }

    @Override
	public boolean canReplaceBlock(int id, int metadata) {
        return id == this.storedBlockID;
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return this.storedBlockID;
	}

	@Override
    public int getStoredBlockMetadata(int metadata) {
        return 0;
    }

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return 0;
	}

    @Override
    public boolean canPlaceOnSurface() {
        return true;
    }

    @Override
    public boolean isSolidRender() {
        return false;
    }
  
    @Override
    public boolean isCubeShaped() {
        return false;
    }
}
