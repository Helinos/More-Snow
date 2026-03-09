package net.helinos.moresnow.block;

import java.util.ArrayList;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicSnowyFenceGate<T extends BlockLogic> extends BlockLogicSnowy<T> {
    private final boolean isPainted;
    private final DyeColor color;

    public BlockLogicSnowyFenceGate(Block<T> block, @Nullable DyeColor color) {
        super(block, 8, 0, false);
        this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

        this.isPainted = color != null;
        this.color = color;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
        int metadata = world.getBlockMetadata(x, y, z);
        int layers = this.getLayers(metadata);
        double height = layers * 2 / 16.0;

        this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, height, 1.0).move(x, y, z), aabbList);

        if (!this.isOpen(metadata)) {
            if (this.getDirection(metadata) != 3 && this.getDirection(metadata) != 1) {
                this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.25, 0.375, 1.0, 1.50, 0.625).move(x, y, z), aabbList);
            } else {
                this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.375, 0.25, 0.0, 0.625, 1.50, 1.0).move(x, y, z), aabbList);
            }
        }
    }

    public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
        if (!Item.hasTag(player.getCurrentEquippedItem(), ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS)) {
            this.onBlockRightClicked(world, x, y, z, player, null, 0.0, 0.0);
        }
    }

    public boolean onBlockRightClicked(World world, int x, int y, int z, @Nullable Player player, Side side, double xPlaced, double yPlaced) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (isOpen(metadata)) {
            world.setBlockMetadataWithNotify(x, y, z, metadata & 0b11011111);
        } else {
            int newDirection = 0;
            if (player != null) {
                newDirection = (MathHelper.floor(player.yRot * 4.0 / 360.0 + 0.5) & 0b11) % 4;
            }

            int currentDirection = getDirection(metadata);
            if (currentDirection == ((newDirection + 2) & 0b11)) {
                metadata = (metadata & 0b00000111) | (newDirection << 3);
            }

            world.setBlockMetadataWithNotify(x, y, z, metadata | 0b100000);
        }

        if (Math.random() < 0.5) {
            world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x, (double)y, (double)z, "random.door_open", 1.0F, 1.0F);
        } else {
            world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, (double)x, (double)y, (double)z, "random.door_close", 1.0F, 1.0F);
        }

        return true;
    }

    public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
        this.onBlockRightClicked(world, x, y, z, null, direction.getSide(), 0.5, 0.5);
    }

    @Override
    public int getStoredBlockMetadata(int metadata) {
        int gateMeta = (metadata >> 3 & 0b111);

        if (this.isPainted) {
            return (this.color.blockMeta << 4) | gateMeta;
        }

        return gateMeta;
    }

    @Override
    public int getStoredBlockId(int metadata) {
        if (this.isPainted && DyeColor.colorFromBlockMeta(metadata >> 4) == this.color ) {
            return Blocks.FENCE_GATE_PLANKS_OAK_PAINTED.id();
        }

        return Blocks.FENCE_GATE_PLANKS_OAK.id();
    }

    @Override
    protected int blockToMetadata(int blockId, int metadata) {
        return (metadata & 0b111) << 3;
    }

    @Override
    public boolean isSolidRender() {
        return false;
    }

    @Override
    public boolean isCubeShaped() {
        return false;
    }

    public boolean isOpen(int metadata) {
        return ((metadata >> 5) & 1) == 1;
    }

    public int getDirection(int metadata) {
        return (metadata >> 3) & 0b11;
    }
}
