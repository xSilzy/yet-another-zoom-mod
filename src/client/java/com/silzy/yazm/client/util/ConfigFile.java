/*
 * This file is part of the YAZM distribution (https://github.com/xSilzy/yet-another-zoom-mod).
 * Copyright (c) 2026 xSilzy.
 * Licensed under GNU GPLv3. See LICENSE for details.
 */

package com.silzy.yazm.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.silzy.yazm.client.util.YazmHelper.LOGGER;

public abstract class ConfigFile {
    private final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final transient String fileName;
    private final transient Path configPath;

    public ConfigFile(String fileName){
        LOGGER.info("Loading config file {}", fileName);
        this.fileName = fileName;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve(String.join(".", fileName,"json"));
    }

    public void initConfig(){
        if (Files.exists(configPath)) {
            LOGGER.info("Config File found! Loading config file.");
            loadConfig();
        } else {
            LOGGER.info("No Config File found! Creating config file at: '{}'",  configPath);
            saveConfig();
        }
    }

    public void saveConfig(){
        try (BufferedWriter writer = Files.newBufferedWriter(this.configPath)) {
            gson.toJson(this, writer);
        } catch (IOException e){
            e.printStackTrace();
        }
        LOGGER.info("Saved {} config to: {}!", fileName, configPath);
    }

    public void loadConfig(){
        if (!Files.exists(configPath)) return;

        try (BufferedReader reader = Files.newBufferedReader(this.configPath)){
            Object data = gson.fromJson(reader, this.getClass());
            if (data != null) {
                copyFields(data, this);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void copyFields(Object src, Object dest) throws IllegalAccessException {
        for (Field field : src.getClass().getDeclaredFields()){
            if (Modifier.isTransient(field.getModifiers())) continue;

            field.setAccessible(true);
            Object srcVal = field.get(src);
            if (srcVal == null) continue;

            Object destVal = field.get(dest);
            if (destVal != null && !isPrimitiveOrWrapper(field.getType())){
                copyFields(srcVal, destVal);
            } else {
                field.set(dest, srcVal);
            }
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Float.class ||
                type == Double.class ||
                type == Integer.class ||
                type == Boolean.class ||
                type == Long.class ||
                type == String.class;
    }
}




