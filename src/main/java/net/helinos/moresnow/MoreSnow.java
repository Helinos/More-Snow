package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weathers;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreSnow implements ModInitializer, BlockInitEntrypoint {
	public static final String MOD_ID = "moresnow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler config = new TomlConfigHandler(MOD_ID, new Toml("BTA + NFC configuration file."), false);

	public static final List<Field> BLOCK_FIELDS = Arrays.stream(MSBlocks.class.getDeclaredFields()).filter(field -> Block.class.isAssignableFrom(field.getType())).collect(Collectors.toList());

	@Override
	public void onInitialize() {
		LOGGER.info("More Snow initialized.");
	}

	@Override
	public void afterBlockInit() {
		MSBlocks.init(1050);
	}

	public static void moreSnowChunkLoadEffect(World world, Chunk chunk) {
		for (int chunkX = 0; chunkX < 16; chunkX++) {
			for(int chunkZ = 0; chunkZ < 16; chunkZ++) {
				int worldX = chunk.xPosition * 16 + chunkX;
				int worldZ = chunk.zPosition * 16 + chunkZ;
				int y = world.findTopSolidBlock(worldX, worldZ);
				Biome biome = world.getBlockBiome(worldX, y, worldZ);

				if (ArrayUtils.contains(biome.blockedWeathers, Weathers.OVERWORLD_SNOW)) {
					continue;
				}

				if (
					y < 0 
					|| y >= world.getHeightBlocks() 
					|| chunk.getBrightness(LightLayer.Block, chunkX, y, chunkZ) >= 10
				) {
					return;
				}

				int blockID = chunk.getBlockID(chunkX, y, chunkZ);
				int blockBelowID = chunk.getBlockID(chunkX, y - 1, chunkZ);

				if (
					MSBlocks.tryMakeSnowy(chunk, blockID, chunkX, y, chunkZ)
					|| MSBlocks.tryMakeSnowy(chunk, blockBelowID, chunkX, y - 1, chunkZ)
				) {
					return;
				}

				if (
					blockID == 0
					&& Blocks.LAYER_SNOW.canPlaceBlockAt(world, worldX, y, worldZ)
					&& blockBelowID != Blocks.ICE.id()
				) {
					chunk.setBlockIDWithMetadataRaw(chunkX, y, chunkZ, Blocks.LAYER_SNOW.id(), chunk.getBlockMetadata(chunkX, y, chunkZ));
				} else if (
					blockBelowID == Blocks.FLUID_WATER_STILL.id()
					&& chunk.getBlockMetadata(chunkX, y - 1, chunkZ) == 0
				) {
					chunk.setBlockIDWithMetadataRaw(chunkX, y - 1, chunkZ, Blocks.ICE.id(), chunk.getBlockMetadata(chunkX, y - 1, chunkZ));
					if (chunk.getBlockID(chunkX, y - 2, chunkZ) == Blocks.FLUID_WATER_STILL.id()) {
						chunk.setBlockIDWithMetadataRaw(chunkX, y - 2, chunkZ, Blocks.FLUID_WATER_FLOWING.id(), chunk.getBlockMetadata(chunkX, y - 2, chunkZ));
					}
				}
			}
		}
	}
}
