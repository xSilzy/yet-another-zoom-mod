package com.silzy.yazm.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.silzy.yazm.client.util.YazmHelper.getDeltaTime;
import static com.silzy.yazm.client.util.YazmHelper.updateFov;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "getFov",
            at = @At("RETURN"),
            cancellable = true
    )
    private void getFov(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> info){
        info.setReturnValue(updateFov(info.getReturnValue(), getDeltaTime()));
    }
}