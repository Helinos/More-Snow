package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicCropsPumpkin;
import net.minecraft.core.block.BlockLogicCropsWheat;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.block.BlockLogicFlowerStackable;
import net.minecraft.core.block.BlockLogicSaplingBase;
import net.minecraft.core.block.BlockLogicSlab;
import net.minecraft.core.block.BlockLogicStairs;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.llamalad7.mixinextras.lib.apache.commons.ArrayUtils;

import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.util.toml.Toml;
import turniplabs.halplibe.util.BlockInitEntrypoint;

public class MSBlocks implements BlockInitEntrypoint {
	public static Block<BlockLogicSnowyPlant<?, BlockLogicFlower>> SNOWY_PLANT;
	public static ArrayList<Block<?>> SNOWY_FLOWER_STACKABLES = new ArrayList<>();
	public static Block<?> SNOWY_SLAB;
	public static Block<?> SNOWY_SLAB_PAINTED;
	public static ArrayList<Block<?>> SNOWY_STAIRS = new ArrayList<>();
	public static Block<?> SNOWY_STAIRS_PAINTED;
	public static Block<?> SNOWY_PARTIAL;
	public static Block<?> SNOWY_FENCE;
	public static Block<?> SNOWY_FENCE_PAINTED;
	public static Block<?> SNOWY_FENCE_WALLPAPER;
	public static Block<?> SNOWY_FENCE_STEEL;
	public static Block<?> SNOWY_FENCE_CHAINLINK;
	public static Block<?> SNOWY_FENCE_GATE;
	public static ArrayList<Block<?>> SNOWY_FENCE_GATES_PAINTED = new ArrayList<>();

	public static int[] transparentIds;
	public static int[] solidIds;
	public static int[] blockIds;

	private static Toml rawConfig;
	private static boolean configChanged = false;

	public void afterBlockInit() {
		MoreSnow.LOGGER.info("Initializing Blocks.");

		rawConfig = MoreSnow.CONFIG.getRawParsed();
		
		ArrayList<Integer> excludedPlantIDs = new ArrayList<>();
		for (Block<?> block : Blocks.blocksList) {
			if (
				block != null && (
					block.getLogic() instanceof BlockLogicSaplingBase ||
					block.getLogic() instanceof BlockLogicCropsPumpkin ||
					block.getLogic() instanceof BlockLogicCropsWheat ||
					block.getLogic() instanceof BlockLogicFlowerStackable
				)
			) {
				excludedPlantIDs.add(block.id());
			}
		}

		String key = "snowy_plant";
		SNOWY_PLANT = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowyPlant<>(block, BlockLogicFlower.class, excludedPlantIDs));

		for (Block<?> flower : Blocks.blocksList) {
			if (flower != null && flower.getLogic() instanceof BlockLogicFlowerStackable) {
				String[] flowerKeys = flower.getKey().split("\\.");
				key = "snowy_flower_stackable_" + flowerKeys[flowerKeys.length - 1];
				Block<?> snowyFlowerStackable = new BlockBuilder(MoreSnow.MOD_ID)
					.setBlockSound(BlockSounds.CLOTH)
					.setHardness(0.1f)
					.setUseInternalLight()
					.setVisualUpdateOnMetadata()
					.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.NOT_IN_CREATIVE_MENU)
					.build(key, getID(key), block -> new BlockLogicSnowyFlowerStackable<>(block, flower.id()));
				SNOWY_FLOWER_STACKABLES.add(snowyFlowerStackable);
			}	
		}

		key = "snowy_slab";
		SNOWY_SLAB = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(1)
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowySlab<>(block, BlockLogicSlab.class, Collections.singletonList(Blocks.SLAB_PLANKS_PAINTED.id())));

		key = "snowy_slab_painted";
		SNOWY_SLAB_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(1)
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowySlabPainted<>(block));
		
		List<Integer> usedStairIDs = new ArrayList<>();
		usedStairIDs.add(Blocks.STAIRS_PLANKS_PAINTED.id());
		for(int index = 1; true; index++) {
			key = "snowy_stairs_" + index;
			Block<?> snowyStairs = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(15)
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowyStairsMultiple<>(block, BlockLogicStairs.class, usedStairIDs));
			SNOWY_STAIRS.add(snowyStairs);
			
			if (((BlockLogicSnowyStairsMultiple<?, ?>) snowyStairs.getLogic()).USED_IDS.size() >= 16) {
				usedStairIDs.addAll(((BlockLogicSnowyStairsMultiple<?, ?>) snowyStairs.getLogic()).USED_IDS);
			} else {
				break;
			}
		}

		key = "snowy_stairs_painted";
		SNOWY_STAIRS_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setLightOpacity(15)
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowyStairsPainted<>(block));

		key = "snowy_partial";
		SNOWY_PARTIAL = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyPartial::new);

		key = "snowy_fence";
		SNOWY_FENCE = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyFence::new);

		key = "snowy_fence_painted";
		SNOWY_FENCE_PAINTED = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyFencePainted::new);

		key = "snowy_fence_wallpaper";
		SNOWY_FENCE_WALLPAPER = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyFenceWallPaper::new);

		key = "snowy_fence_steel";
		SNOWY_FENCE_STEEL = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyFenceSteel::new);

		key = "snowy_fence_chainlink";
		SNOWY_FENCE_CHAINLINK = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), BlockLogicSnowyFenceChainlink::new);

		key = "snowy_fence_gate";
		SNOWY_FENCE_GATE = new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setVisualUpdateOnMetadata()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
			.build(key, getID(key), block -> new BlockLogicSnowyFenceGate<>(block, null));

		for (DyeColor color : DyeColor.blockOrderedColors()) {
			key = "snowy_fence_gate_" + color.colorID;
			Block<?> snowyFenceGatePainted = new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setVisualUpdateOnMetadata()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT, BlockTags.NOT_IN_CREATIVE_MENU)
				.build(key, getID(key), block -> new BlockLogicSnowyFenceGate<>(block, color));
			SNOWY_FENCE_GATES_PAINTED.add(snowyFenceGatePainted);
		}

		blockIds = new int[] {
			SNOWY_PLANT.id(),
			SNOWY_SLAB.id(), 
			SNOWY_SLAB_PAINTED.id(),
			SNOWY_STAIRS_PAINTED.id(),
			SNOWY_FENCE.id(), 
			SNOWY_FENCE_PAINTED.id(),
			SNOWY_FENCE_WALLPAPER.id(),
			SNOWY_FENCE_STEEL.id(),
			SNOWY_FENCE_CHAINLINK.id(),
			SNOWY_FENCE_GATE.id()
		};
		blockIds = ArrayUtils.addAll(
			blockIds,
			SNOWY_FLOWER_STACKABLES.stream().mapToInt(block -> block.id()).toArray()
		);
		blockIds = ArrayUtils.addAll(
			blockIds,
			SNOWY_STAIRS.stream().mapToInt(block -> block.id()).toArray()
		);
		blockIds = ArrayUtils.addAll(
			blockIds, 
			SNOWY_FENCE_GATES_PAINTED.stream().mapToInt(block -> block.id()).toArray()
		);

		if (configChanged) {
			MoreSnow.CONFIG.setDefaults(rawConfig);
			MoreSnow.CONFIG.writeConfig();
			MoreSnow.CONFIG.loadConfig();
		}

		MoreSnow.LOGGER.info("Initialized Blocks.");
	}

	public static BlockLogicSnowy<?> whichCanReplace(int id, int metadata) {
		for (int whichId : blockIds) {
			BlockLogicSnowy<?> blockLogic = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			if (blockLogic.canReplaceBlock(id, metadata))
				return blockLogic;
		}
		return null;
	}

	public static boolean tryMakeSnowy(World world, int id, int x, int y, int z) {
		if (Blocks.getBlock(id) == null) {
			return false;
		}

		boolean placed = false;
		for (int whichId : blockIds) {
			BlockLogicSnowy<?> block = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowy(Chunk chunk, int id, int x, int y, int z) {
		if (Blocks.getBlock(id) == null) {
			return false;
		}
		
		boolean placed = false;
		for (int whichId : blockIds) {
			BlockLogicSnowy<?> blockLogic = (BlockLogicSnowy<?>) Blocks.getBlock(whichId).getLogic();
			placed = blockLogic.tryMakeSnowy(chunk, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	private static int nextBlockID = 0;

	private static int getID(String key) {
		boolean containsBlock;
		String category = "BlockIDs.";
		try {
			containsBlock = rawConfig.contains(category + key);
		} catch (NullPointerException e) {
			containsBlock = false;
		}
		
		if (containsBlock) {
			return MoreSnow.CONFIG.getInt(category + key);
		}

		while (Blocks.blocksList[++nextBlockID] != null) {}
		
		rawConfig.addEntry(category + key, nextBlockID);
		configChanged = true;

		return nextBlockID;
	}
}
