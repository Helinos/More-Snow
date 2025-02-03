package net.helinos.moresnow.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.loader.api.FabricLoader;
import net.helinos.moresnow.BlockInitEntrypoint;
import net.minecraft.server.MinecraftServer;

@Mixin(value = MinecraftServer.class, remap = false)
public class MinecraftServerMixin {
    @Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/Blocks;init()V", shift = At.Shift.AFTER))
    public void afterBlockInitEntrypoint(CallbackInfoReturnable<?> callbackInfoReturnable) {
        FabricLoader.getInstance().getEntrypoints("afterBlockInit", BlockInitEntrypoint.class).forEach(BlockInitEntrypoint::afterBlockInit);;
    }
}