/*
 * This file is part of the YAZM distribution (https://github.com/xSilzy/yet-another-zoom-mod).
 * Copyright (c) 2026 xSilzy.
 * Licensed under GNU GPLv3. See LICENSE for details.
 */

package com.silzy.yazm.client;

import com.silzy.yazm.client.util.ConfigFile;

import static com.silzy.yazm.client.util.YazmHelper.LOGGER;
import static com.silzy.yazm.client.util.YazmHelper.MOD_ID;

public class YazmConfig extends ConfigFile {
    public YazmConfig() {
        super(MOD_ID);
    }

    public General general = new General(this::saveConfig);
    public ScrollZooming scrollZooming = new ScrollZooming(this::saveConfig);



    // Categories(classes) that make out the config
    public static class General{
        private transient Runnable save;
        public General(Runnable save){
            this.save = save;
        }

        float initZoom = 5f;
        float zoomInSpeed = 1f; // in seconds
        float zoomOutSpeed = 0.5f; // in seconds
        boolean toggleZoom = false;

        boolean hideHud = false;
        boolean cinematicCam = false;
        boolean changeMouseSens = true;
        boolean resetZoom = true;



        boolean enableResetZoomReminder = true;

        public float getInitZoom() {
            return initZoom;
        }
        public void setInitZoom(float initZoom) {
            this.initZoom = initZoom;
            if(save != null) save.run();
            LOGGER.info("Set Init Zoom to: {}, if its not saved the runnable didnt run", initZoom);
        }
        public float getZoomInSpeed() {
            return zoomInSpeed;
        }
        public void setZoomInSpeed(float zoomInSpeed) {
            this.zoomInSpeed = zoomInSpeed;
            if(save != null) save.run();
        }
        public float getZoomOutSpeed() {
            return zoomOutSpeed;
        }
        public void setZoomOutSpeed(float zoomOutSpeed) {
            this.zoomOutSpeed = zoomOutSpeed;
            if(save != null) save.run();
        }
        public boolean isToggleZoom() {
            return toggleZoom;
        }
        public void setToggleZoom(boolean toggleZoom) {
            this.toggleZoom = toggleZoom;
            if(save != null) save.run();
        }
        public boolean isHideHud() {
            return hideHud;
        }
        public void setHideHud(boolean hideHud) {
            this.hideHud = hideHud;
            if(save != null) save.run();
        }
        public boolean isCinematicCam() {
            return cinematicCam;
        }
        public void setCinematicCam(boolean cinematicCam) {
            this.cinematicCam = cinematicCam;
            if(save != null) save.run();
        }
        public boolean isChangeMouseSens() {
            return changeMouseSens;
        }
        public void setChangeMouseSens(boolean changeMouseSens) {
            this.changeMouseSens = changeMouseSens;
            if(save != null) save.run();
        }
        public boolean isResetZoom() {
            return resetZoom;
        }
        public void setResetZoom(boolean resetZoom) {
            this.resetZoom = resetZoom;
            if(save != null) save.run();
        }
        public boolean isEnableResetZoomReminder() {
            return enableResetZoomReminder;
        }
        public void setEnableResetZoomReminder(boolean enableResetZoomReminder) {
            this.enableResetZoomReminder = enableResetZoomReminder;
            if(save != null) save.run();

        }
    }

    public static class ScrollZooming{
        private transient Runnable save;
        public ScrollZooming(Runnable save){
            this.save = save;
        }

        boolean scrollZoom = true;
        float scrollZoomSteps = 0.25f;
        boolean limitZoom = false;
        float zoomLimit = 100f;

        public smoothScrolling smoothScrolling = new smoothScrolling(save);

        public static class smoothScrolling{
            private transient Runnable save;
            public smoothScrolling(Runnable save){
                this.save = save;
            }


            boolean smoothScroll = true;
            float scrollSmoothness = 10f;

            public boolean isSmoothScroll() {
                return smoothScroll;
            }
            public void setSmoothScroll(boolean smoothScroll) {
                this.smoothScroll = smoothScroll;
                if(save != null) save.run();
            }
            public float getScrollSmoothness() {
                return scrollSmoothness;
            }
            public void setScrollSmoothness(float scrollSmoothness) {
                this.scrollSmoothness = scrollSmoothness;
                save.run();
            }
        }

        public boolean isScrollZoom()    {
            return scrollZoom;
        }
        public void setScrollZoom(boolean scrollZoom) {
            this.scrollZoom = scrollZoom;
            save.run();
        }
        public float getScrollZoomSteps() {
            return scrollZoomSteps;
        }
        public void setScrollZoomSteps(float scrollZoomSteps) {
            this.scrollZoomSteps = scrollZoomSteps;
            save.run();
        }
        public boolean isLimitZoom() {
            return limitZoom;
        }
        public void setLimitZoom(boolean limitZoom) {
            this.limitZoom = limitZoom;
            save.run();
        }
        public float getZoomLimit() {
            return zoomLimit;
        }
        public void setZoomLimit(float zoomLimit) {
            this.zoomLimit = zoomLimit;
            save.run();
        }
    }
}
