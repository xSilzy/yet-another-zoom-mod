package com.silzy.yazm.client.util;


import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.lerp;


public class YazmHelper {
    public final static String MOD_ID = "YAZM";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Keybinds
    public static KeyBinding zoomKey;


    // Interpolation vars
    private static float interpolant; // position between interpolations
    private static float zoomSpeed = 1; // in seconds
    private static float zoomMultiplier = 5; // x times zoom
    private static float oldFov;
    private static double oldMouseSensitivity;

    // State tracking
    public static boolean isZooming;


    private static MinecraftClient client;



    public static void initYazm() {
        zoomKey = newKeyBind(
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_Z,
                MOD_ID,
                "zoom",
                "zoom");

        LOGGER.info("{} initialized successfully!", MOD_ID);

    }

    private static boolean canZoom() {
        updateClient();

        boolean isPlaying = client != null
                && client.world != null
                && client.player != null
                && client.player.isAlive()
                && client.currentScreen == null;

        return zoomKey.isPressed() && isPlaying;
    }

    public static float updateFov(float fov, float deltaTime){
        isZooming = canZoom();
        if (!isZooming && interpolant == 0) return fov;
        if (isZooming && interpolant == 0) oldFov = client.options.getFov().getValue();

        if (isZooming) {
            interpolant += deltaTime  * (1/zoomSpeed);
        } else {
            interpolant -= deltaTime * (1/zoomSpeed);
        }

        interpolant = clamp(interpolant, 0, 1);

        float currentZoomFactor = lerp(easeInOutQuart(interpolant), 1, zoomMultiplier);

        return fov / currentZoomFactor;
    }


    private static float easeInOutCubic(float i){
        return i < 0.5 ? 4 * i * i * i : (float) (1 - Math.pow(-2 * i + 2, 3) / 2); // Ease in out cubic
    }
    private static float easeInOutQuad(float i){
        return i < 0.5 ? 2 * i * i : (float) (1 - Math.pow(-2 * i + 2, 2) / 2); // Ease in out Quadratic
    }
    private static float easeInOutQuart(float i){
        return i < 0.5 ? 8 * i * i * i * i : (float) (1 - Math.pow(-2 * i + 2, 4) / 2); // Ease in out Quartic
    }
    private static float easeOutElastic(float i){
        float c4 = (float) ((2 * Math.PI) / 3);
        return (float) (Math.pow(2, -10 * i) * Math.sin((i * 10 - 0.75) * c4) + 1); // Ease out Elastic
    }



    public static float getDeltaTime() {
        updateClient();
        return client.getRenderTickCounter().getFixedDeltaTicks() / 20f;
    }

    private static MinecraftClient updateClient() {
        if (client == null){LOGGER.warn("Client is null! Update failed.");}
        client = MinecraftClient.getInstance();
        return client;
    }

    private static KeyBinding newKeyBind(InputUtil.Type keyType, int keyBind, CharSequence modName , CharSequence keyName, CharSequence keyCategory) {
        // Preparing the strings
        modName = modName.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");
        keyName = keyName.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");
        keyCategory = keyCategory.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");

        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                String.join(".", "key",modName,keyName),
                keyType,
                keyBind,
                KeyBinding.Category.create(Identifier.of((String) modName, (String) keyCategory))
        ));
    }

}
