package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;
import net.helinos.moresnow.block.MSBlocks;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreSnow implements ModInitializer, BlockInitEntrypoint {
	public static final String MOD_ID = "moresnow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG = new TomlConfigHandler(MOD_ID, new Toml("More Snow configuration file."), false);

	static {
		File configFile = CONFIG.getConfigFile();
		if (configFile.exists()) {
			CONFIG.loadConfig();
            CONFIG.setDefaults(CONFIG.getRawParsed());
		} else {
			Toml defaultConfig = new Toml("More Snow configuration file.");
			defaultConfig.addCategory("BlockIDs");

			CONFIG.setDefaults(defaultConfig);

			try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                CONFIG.writeConfig();
                CONFIG.loadConfig();
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate configuration file!", e);
            }
		}
	}

	@Override
	public void onInitialize() {
	}

	@Override
	public void afterBlockInit() {
		MSBlocks.init();
	}
}
