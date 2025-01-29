package net.helinos.moresnow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.loader.api.FabricLoader;
import net.helinos.moresnow.BlockInitEntrypoint;
import net.minecraft.client.Minecraft;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/Blocks;init()V", shift = At.Shift.AFTER))
    public void afterBlockInitEntrypoint(CallbackInfo callbackInfo) {
        FabricLoader.getInstance().getEntrypoints("afterBlockInit", BlockInitEntrypoint.class).forEach(BlockInitEntrypoint::afterBlockInit);;
    }
}
