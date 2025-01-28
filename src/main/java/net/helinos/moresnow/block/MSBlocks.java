package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.block.model.BlockModelSnowyFence;
import net.helinos.moresnow.block.model.BlockModelSnowyPlant;
import net.helinos.moresnow.block.model.BlockModelSnowySlab;
import net.helinos.moresnow.block.model.BlockModelSnowyStairs;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicCropsPumpkin;
import net.minecraft.core.block.BlockLogicCropsWheat;
import net.minecraft.core.block.BlockLogicFence;
import net.minecraft.core.block.BlockLogicFencePainted;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.BlockLogicSaplingBase;
import net.minecraft.core.block.BlockLogicSlab;
import net.minecraft.core.block.BlockLogicSlabPainted;
import net.minecraft.core.block.BlockLogicStairs;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	public static Block<?> SNOWY_PLANT;
	public static Block<?> SNOWY_SLAB;
	public static Block<?> SNOWY_SLAB_PAINTED;
	public static Block<?> SNOWY_STAIRS;
	public static Block<?> SNOWY_STAIRS_2;
	public static Block<?> SNOWY_STAIRS_PAINTED;
	public static Block<?> SNOWY_PARTIAL;
	public static Block<?> SNOWY_FENCE;
	public static Block<?> SNOWY_FENCE_PAINTED;

	public static int[] transparentIds;
	public static int[] solidIds;
	public static int[] blockIds;

	public static void init(int minimumID) {
		MoreSnow.LOGGER.info("Initializing Blocks.");

		ArrayList<Integer> excludedPlantIDs = new ArrayList<>();
		for (Block<?> block : Blocks.blocksList) {
			if (block != null && (block.getLogic() instanceof BlockLogicSaplingBase || block.getLogic() instanceof BlockLogicCropsPumpkin || block.getLogic() instanceof BlockLogicCropsWheat)) {
				excludedPlantIDs.add(block.id());
			}
		}
		if (excludedPlantIDs.isEmpty()) {
			MoreSnow.LOGGER.warn("excludedPlantIDs was empty! This is a bug!");
		}
		
		SNOWY_PLANT = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setTextures("minecraft:block/block_snow")
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(0)
				.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyPlant<>(block))
				.build("snowy_plant", minimumID++, block -> new BlockLogicSnowyPlant<>(block, BlockLogicFlower.class, excludedPlantIDs));

		SNOWY_SLAB = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(1)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowySlab<>(block))
				.build("snowy_slab", minimumID++, block -> new BlockLogicSnowySlab<>(block, BlockLogicSlab.class, new int[] { Blocks.SLAB_PLANKS_PAINTED.id() }));

		SNOWY_SLAB_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(1)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowySlab<>(block))
				.build("snowy_slab_painted", minimumID++, block -> new BlockLogicSnowySlabPainted<>(block, BlockLogicSlabPainted.class, new int[0]));

		SNOWY_STAIRS = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(15)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyStairs<>(block))
				.build("snowy_stairs", minimumID++, block -> new BlockLogicSnowyStairs<>(block, BlockLogicStairs.class, new int[] { Blocks.STAIRS_PLANKS_PAINTED.id() }));

		SNOWY_STAIRS_2 = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(15)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyStairs<>(block))
				.build("snowy_stairs_2", minimumID++, block -> new BlockLogicSnowyStairs<>(block, BlockLogicStairs.class, ArrayUtils.add(((BlockLogicSnowyStairs<?>) SNOWY_STAIRS.getLogic()).USED_IDS, Blocks.STAIRS_PLANKS_PAINTED.id())));

		SNOWY_STAIRS_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(15)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyStairs<>(block))
				.build("snowy.stairs.painted", minimumID++, block -> new BlockLogicSnowyStairsPainted<>(block, BlockLogicStairs.class, ArrayUtils.addAll(((BlockLogicSnowyStairs<?>) SNOWY_STAIRS.getLogic()).USED_IDS, ((BlockLogicSnowyStairs<?>) SNOWY_STAIRS.getLogic()).USED_IDS)));

		SNOWY_PARTIAL = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setTextures("minecraft:block/block_snow")
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(0)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU)
				.build("snowy.partial", minimumID++, block -> new BlockLogicSnowyPartial<>(block));

		SNOWY_FENCE = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(0)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyFence<>(block))
				.build("snowy.fence", minimumID++, block -> new BlockLogicSnowyFence<>(block, BlockLogicFence.class, new int[] { Blocks.FENCE_PLANKS_OAK_PAINTED.id() }));

		SNOWY_FENCE_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(0)
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.setBlockModel(block -> new BlockModelSnowyFence<>(block))
				.build("snowy.fence.painted", minimumID++, block -> new BlockLogicSnowyFencePainted<>(block, BlockLogicFencePainted.class, new int[0]));

		transparentIds = new int[] { 
			SNOWY_PLANT.id(), 
			SNOWY_PARTIAL.id() 
		};
		solidIds = new int[] { 
			SNOWY_SLAB.id(), 
			SNOWY_SLAB_PAINTED.id(), 
			SNOWY_STAIRS.id(), 
			SNOWY_STAIRS_2.id(), 
			SNOWY_STAIRS_PAINTED.id(),
			SNOWY_FENCE.id(), 
			SNOWY_FENCE_PAINTED.id()
		};
		blockIds = ArrayUtils.addAll(transparentIds, solidIds);

		MoreSnow.LOGGER.info("Initialized Blocks.");
	}

	public static BlockLogicSnowy<?> whichCanReplace(int id, int metadata) {
		for (int whichId : transparentIds) {
			BlockLogicSnowy<?> blockLogic = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			if (blockLogic.canReplaceBlock(id, metadata))
				return blockLogic;
		}
		return whichCanReplaceSolid(id, metadata);
	}

	public static BlockLogicSnowy<?> whichCanReplaceSolid(int id, int metadata) {
		for (int whichId : solidIds) {
			BlockLogicSnowy<?> block = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			if (block.canReplaceBlock(id, metadata))
				return block;
		}
		return null;
	}

	public static boolean tryMakeSnowy(World world, int id, int x, int y, int z) {
		boolean placed = tryMakeSnowyTransparent(world, id, x, y, z);
		placed |= tryMakeSnowySolid(world, id, x, y, z);
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : transparentIds) {
			BlockLogicSnowy<?> block = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockLogicSnowy<?> block = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : transparentIds) {
			BlockLogicSnowy<?> blockLogic = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = blockLogic.tryMakeSnowy(chunk, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockLogicSnowy<?> blockLogic = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = blockLogic.tryMakeSnowy(chunk, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}
}
