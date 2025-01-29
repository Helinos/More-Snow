package net.helinos.moresnow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.helinos.moresnow.interfaces.mixin.IWorld;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.weather.Weathers;

@Mixin(value = World.class, remap = false)
public abstract class WorldMixin implements IWorld, WorldSource {   
    @Unique
    private boolean hasSnowed = false;

    @Override
    public boolean getHasSnowed() {
        return this.hasSnowed;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void setHasSnowed(CallbackInfo callbackInfo) {
        if (((World) (Object) this).weatherManager.getCurrentWeather() == Weathers.OVERWORLD_SNOW) {
            this.hasSnowed = true;
        }
        
        if (((World) (Object) this).seasonManager.getCurrentSeason() != Seasons.OVERWORLD_WINTER) {
            this.hasSnowed = false;
        }
    }
}
