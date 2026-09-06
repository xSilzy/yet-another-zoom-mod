/*
 * This file is part of the YAZM distribution (https://github.com/xSilzy/yet-another-zoom-mod).
 * Copyright (c) 2026 xSilzy.
 * Licensed under GNU GPLv3. See LICENSE for details.
 */

package com.silzy.yazm.client.zoom;

import com.silzy.yazm.client.core.YazmEvents;
import com.silzy.yazm.client.math.EasingFunction;
import com.silzy.yazm.client.math.EasingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.silzy.yazm.client.core.Yazm.*;
import static com.silzy.yazm.client.util.ClientUtils.getDeltaTime;
import static com.silzy.yazm.client.util.ClientUtils.sendActionBarMessage;
import static com.silzy.yazm.client.util.KeybindUtils.newKeyBind;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.lerp;

public class ZoomHandler {
    private static float interpolant;
    private static boolean isZooming;
    private static boolean isToggle;
    private static boolean preHidden;
    private static boolean preCinematic;
    private static float zoomFactor;
    private static float targetZoomFactor;
    private static boolean isUsingSpyglass;


    public static void initZoom(){
        float initZoom = config.initZoom();
        zoomFactor = initZoom;
        targetZoomFactor = initZoom;

        zoomKey = newKeyBind(InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_Z, MOD_ID, "zoom");

        YazmEvents.ON_ZOOM.register(ZoomHandler::onZoom);
        YazmEvents.ON_DEACTIVATE.register(ZoomHandler::onDeactivate);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) isUsingSpyglass = client.player.isUsingSpyglass();

            boolean prevZooming = isZooming;
            isZooming = canZoom();

            if (isZooming != prevZooming) {
                if (isZooming) YazmEvents.ON_ZOOM.invoker().onZoom();
                else YazmEvents.ON_DEACTIVATE.invoker().OnDeactivate();
            }
        });
    }

    private static boolean canZoom() {
        boolean isPlaying = client != null
                && client.world != null
                && client.player != null
                && client.player.isAlive()
                && client.currentScreen == null;

        return isPlaying && zoomKey.isPressed() || isPlaying && isUsingSpyglass;
    }

    public static float updateFov(float fov){
        if (!isZooming && interpolant == 0) return fov;
        return fov / getZoomFactor();
    }

    private static float getZoomFactor(){
        float deltaTime = getDeltaTime();
        float zoomDirection = (isZooming || isToggle ? 1 : -1);
        float zoomSpeed = (isZooming ? config.zoomInSpeed() : config.zoomOutSpeed());
        EasingFunction easingFunc = EasingHelper.getFunc(config.easingFunction());

        interpolant += (deltaTime  * (1 / zoomSpeed)) * zoomDirection;
        interpolant = clamp(interpolant, 0, 1);

        if (zoomFactor != targetZoomFactor && config.smoothScroll()){
            zoomFactor = lerp(clamp(config.scrollSmoothness() * deltaTime, 0 ,1), zoomFactor, targetZoomFactor);
            if (config.showZoomLevel()) showZoomLevel();
        }

        return lerp(easingFunc.ease(interpolant), 1, zoomFactor);
    }

    public static boolean scrollZoom(float vertical){
        if ((isZooming || isToggle) && config.scrollZooming()) {
            targetZoomFactor *= 1 + (config.scrollZoomSteps() * vertical);
            if (targetZoomFactor < 1) targetZoomFactor = 1;
            if (config.limitZoom()) targetZoomFactor = clamp(targetZoomFactor, 1, config.zoomLimit());

            if (!config.smoothScroll()){
                zoomFactor = targetZoomFactor;
            }
            return true;
        }
        return false;
    }

    public static float getMouseScaling(){
        if (!config.changeMouseSens()) return 1;
        float currentFov = zoomFactor;

        EasingFunction easingFunc = EasingHelper.getFunc(config.easingFunction());
        return lerp(easingFunc.ease(interpolant), 1, currentFov);
    }

    private static void onZoom(){
        float initZoom = config.initZoom();
        boolean resetZoom = config.resetZoom();

        if (!isToggle) {
            if (zoomFactor != initZoom && resetZoom) zoomFactor = initZoom; interpolant = 0;
            if (isUsingSpyglass && resetZoom) zoomFactor = 1;
            targetZoomFactor = zoomFactor;

            if (config.showZoomLevel()) showZoomLevel();
            if(zoomFactor <= 1.5 && !resetZoom && config.enableResetReminder() && !isUsingSpyglass) {
                Text warnMessage = Text.literal("Zoom level < 1.5").formatted(Formatting.RED);
                sendActionBarMessage(warnMessage);
            }

            preHidden = client.options.hudHidden;
            if (config.hideHud() && !preHidden) {
                preHidden = false;
                client.options.hudHidden = true;
            }
            preCinematic = client.options.smoothCameraEnabled;
            if (config.cinematicCam() && !preCinematic) {
                preCinematic = false;
                client.options.smoothCameraEnabled = true;
            }
        }

        isToggle = config.toggleZoom() && !isUsingSpyglass ?  !isToggle : false;
    }

    private static void onDeactivate(){
        if (!isToggle) {
            if (!preHidden && config.hideHud()) {
                client.options.hudHidden = false;
            }
            if (!preCinematic && config.cinematicCam()){
                client.options.smoothCameraEnabled = false;
            }
        }
    }       

    private static void showZoomLevel(){
        sendActionBarMessage(Text.literal("Zoom: " + (float) Math.round(zoomFactor * 10)/10));
    }
}