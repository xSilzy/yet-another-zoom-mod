package com.silzy.yazm.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;

import static com.silzy.yazm.client.util.YazmHelper.initYazm;


public class YazmClient implements ClientModInitializer {



    public static KeyBinding zoomKey;


    @Override
    public void onInitializeClient() {
        initYazm();
    }
}
