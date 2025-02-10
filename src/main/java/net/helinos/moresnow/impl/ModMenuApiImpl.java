package net.helinos.moresnow.impl;

import java.util.function.Function;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.gui.ScreenModOptions;
import net.minecraft.client.gui.Screen;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public String getModId() {
        return MoreSnow.MOD_ID;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        Function<Screen, ? extends Screen> screen = parent -> new ScreenModOptions(parent);
        return screen;
    }
}