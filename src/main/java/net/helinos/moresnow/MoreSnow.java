package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.options.components.IntegerOptionComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.OptionInteger;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreSnow implements ClientStartEntrypoint {
	public static final String MOD_ID = "moresnow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final TomlConfigHandler CONFIG = new TomlConfigHandler(MOD_ID, new Toml("More Snow configuration file."), false);
	public ModSettings modSettings;

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

	public static OptionsPage MOD_OPTIONS;

	@Override
	public void beforeClientStart() {
		this.modSettings = new ModSettings();

		MOD_OPTIONS = new OptionsPage(null, null)
			.withComponent(
				new OptionsCategory("gui.moresnow.options.category.ids")
					.withComponent(new IntegerOptionComponent(this.modSettings.test))
			)
		;
	}

	@Override
	public void afterClientStart() {
	}
}
