/*
 * This file is part of the YAZM distribution (https://github.com/xSilzy/yet-another-zoom-mod).
 * Copyright (c) 2026 xSilzy.
 * Licensed under GNU GPLv3. See LICENSE for details.
 */

package com.silzy.yazm.client.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import static com.silzy.yazm.client.core.Yazm.MOD_ID;

public class KeybindUtils {

    // Abstract to enums later?
    private static KeyBinding.Category YAZM_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID.toLowerCase(), "yazm"));
    public static KeyBinding newKeyBind(InputUtil.Type keyType, int keyBind, CharSequence modName, CharSequence keyName) {
        // Preparing the strings
        modName = modName.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");
        keyName = keyName.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");
//        keyCategory = keyCategory.toString().toLowerCase().replaceAll("[^a-z0-9/._-]","-");

        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                String.join(".", "key",modName,keyName),
                keyType,
                keyBind,
                YAZM_CATEGORY
        ));
    }
}
