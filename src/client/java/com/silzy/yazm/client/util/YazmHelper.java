package com.silzy.yazm.client.util;

import com.silzy.yazm.client.YazmConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

    public static KeyBinding zoomKey;

    private static MinecraftClient client;
    private static final YazmConfig config =  new YazmConfig();

    // State tracking
    private static float interpolant;
    private static boolean isZooming;
    private static boolean isToggle;
    private static boolean preHidden;
    private static boolean preCinematic;
    private static float maxZoom;
    private static float targetZoom;


    public static void initYazm() {
        client = MinecraftClient.getInstance();
        if (client == null) {LOGGER.error("Couldn't Initialize Client!");}

        config.initConfig();
        initZoom();


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean prevIsZooming = isZooming;
            isZooming = canZoom();

            if (isZooming != prevIsZooming) {
                if (isZooming) {onZoom(); return;}
                onDeactivate();
            }
        });
    }

    public static void initZoom(){
        float initZoom = config.general.getInitZoom();
        maxZoom = initZoom;
        targetZoom = initZoom;

        zoomKey = newKeyBind(InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_Z, MOD_ID, "zoom", "zoom");
    }

    private static boolean canZoom() {
        boolean isPlaying = client != null
                && client.world != null
                && client.player != null
                && client.player.isAlive()
                && client.currentScreen == null;

        return zoomKey.isPressed() && isPlaying;
    }

    public static float updateFov(float fov){
        if (!isZooming && interpolant == 0) return fov;
        return fov / getFovScaling(fov);
    }

    public static float getFovScaling(float fov){
        // getting the values from the config
        float zoomInSpeed = config.general.getZoomInSpeed();
        float zoomOutSpeed = config.general.getZoomOutSpeed();

        boolean smoothScroll = config.scrollZooming.smoothScrolling.isSmoothScroll();
        float scrollSmoothness = config.scrollZooming.smoothScrolling.getScrollSmoothness();

        float deltaTime = getDeltaTime();

        float zoomDirection = (isZooming || isToggle ? 1 : -1);
        float zoomSpeed = (isZooming ? zoomInSpeed : zoomOutSpeed);
        interpolant += (deltaTime  * (1 / zoomSpeed)) * zoomDirection;
        interpolant = clamp(interpolant, 0, 1);


        if (maxZoom != targetZoom && smoothScroll){
            maxZoom = lerp(clamp(scrollSmoothness * deltaTime, 0 ,1), maxZoom, targetZoom);
        }

        return lerp(easeInOutQuart(interpolant), 1, maxZoom); // adapt this to be interchangeable
    }

    public static float getMouseScaling(){
        boolean changeMouseSens = config.general.isChangeMouseSens();
        if (!changeMouseSens) return 1;

        // 1:1 mouseSens:ZoomLevel
        return lerp(easeInOutQuart(interpolant), 1, maxZoom);
    }

    public static boolean scrollZoom(float vertical){
        boolean scrollZoom = config.scrollZooming.isScrollZoom();
        float scrollZoomSteps = config.scrollZooming.getScrollZoomSteps();
        boolean limitZoom = config.scrollZooming.isLimitZoom();
        float zoomLimit = config.scrollZooming.getZoomLimit();
        boolean smoothScroll = config.scrollZooming.smoothScrolling.isSmoothScroll();

        if ((isZooming && scrollZoom) || (isToggle && scrollZoom)) {

            targetZoom *= 1 + (scrollZoomSteps * vertical);
            if (targetZoom < 1) targetZoom = 1;
            if (limitZoom) targetZoom = clamp(targetZoom, 1, zoomLimit);

            if (!smoothScroll){
                maxZoom = targetZoom;
            }
            return true;
        }
        return false;
    }

    public static void onZoom(){
        float initZoom = config.general.getInitZoom();
        boolean resetZoom = config.general.isResetZoom();
        boolean shouldRemind = config.general.isEnableResetZoomReminder();
        boolean toggleZoom = config.general.isToggleZoom();
        boolean hideHud = config.general.isHideHud();
        boolean cinematicCam = config.general.isCinematicCam();

        if (maxZoom != initZoom && resetZoom) {maxZoom = initZoom; interpolant = 0;}

        if(maxZoom <= 1.1 && !resetZoom && shouldRemind) {
            // add ingame warning/reminder message
            LOGGER.info("resetZoom is off!");
        }
        targetZoom = maxZoom;



        if (!isToggle) {
            preHidden = client.options.hudHidden;
            if (hideHud && !preHidden) {
                preHidden = false;
                client.options.hudHidden = true;
            }

            preCinematic = client.options.smoothCameraEnabled;
            if (cinematicCam && !preCinematic) {
                preCinematic = false;
                client.options.smoothCameraEnabled = true;
            }
        }

        isToggle = toggleZoom ? !isToggle : false;
    }

    public static void onDeactivate(){
        float initZoom = config.general.getInitZoom();
        boolean resetZoom = config.general.isResetZoom();
        boolean hideHud = config.general.isHideHud();
        boolean cinematicCam = config.general.isCinematicCam();

        if (!isToggle) {
            if (!preHidden && hideHud) {
                client.options.hudHidden = false;
            }

            if (!preCinematic && cinematicCam){
                client.options.smoothCameraEnabled = false;
            }

        }

    }

// abstract into dynamic easing selection
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
        return client.getRenderTickCounter().getFixedDeltaTicks() / 20f;
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
