package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
}
