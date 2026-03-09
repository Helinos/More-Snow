package net.helinos.moresnow.impl;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.helinos.moresnow.MoreSnow;

public class ModMenuApiImpl implements ModMenuApi {

    public String getModId() {
        return MoreSnow.MOD_ID;
    }

    // @Override
    // public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    //     Function<Screen, ? extends Screen> screen = parent -> new ScreenModOptions(parent);
    //     return screen;
    // }
}
