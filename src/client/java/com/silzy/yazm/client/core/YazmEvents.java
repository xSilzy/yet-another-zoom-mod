/*
 * This file is part of the YAZM distribution (https://github.com/xSilzy/yet-another-zoom-mod).
 * Copyright (c) 2026 xSilzy.
 * Licensed under GNU GPLv3. See LICENSE for details.
 */

package com.silzy.yazm.client.core;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class YazmEvents {

    public static final Event<OnZoom> ON_ZOOM = EventFactory.createArrayBacked(OnZoom.class,
            (listeners) -> () -> {
                for (OnZoom listener : listeners) {
                    listener.onZoom();
                }
            }
        );

    public static final Event<OnDeactivate> ON_DEACTIVATE = EventFactory.createArrayBacked(OnDeactivate.class,
            (listeners) -> () -> {
                for (OnDeactivate listener : listeners) {
                    listener.OnDeactivate();
                }
            }
        );

    @FunctionalInterface
    public interface OnZoom {
        void onZoom();
    }

    @FunctionalInterface
    public interface OnDeactivate {
        void OnDeactivate();
    }
}
