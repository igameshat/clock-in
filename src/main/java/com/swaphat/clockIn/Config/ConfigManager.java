package com.swaphat.clockIn.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final String FILE_NAME = "clock-in_config.json";
    private static ConfigStorage config;

    private static File getConfigFile() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(FILE_NAME)
                .toFile();
    }

    /* ---------------- LOAD / SAVE ---------------- */

    public static ConfigStorage getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public static void loadConfig() {
        File file = getConfigFile();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, ConfigStorage.class);
                if (config == null) {
                    config = new ConfigStorage();
                }
            } catch (Exception e) {
                e.printStackTrace();
                config = new ConfigStorage();
            }
        } else {
            config = new ConfigStorage();
            saveConfig(); // create file with defaults
        }
    }

    public static void saveConfig() {
        if (config == null) {
            config = new ConfigStorage();
        }

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------- UPDATE HELPERS ---------------- */

    public static void updateX(float x) {
        getConfig().x = x;
        saveConfig();
    }

    public static void updateY(float y) {
        getConfig().y = y;
        saveConfig();
    }

    public static void updateWidth(float width) {
        getConfig().width = width;
        saveConfig();
    }

    public static void updateHeight(float height) {
        getConfig().height = height;
        saveConfig();
    }

    public static void updateColor(int color) {
        getConfig().color = color;
        saveConfig();
    }

    public static void updateMessage(String message) {
        AbstractClockWidget.setClockMessage(Component.nullToEmpty(message));
        getConfig().message = message;
        saveConfig();
    }

    public static void updateScale(float scale) {
        getConfig().scale = scale;
        saveConfig();
    }

    public static void updateShadow(boolean shouldShadow) {
        getConfig().shadow = shouldShadow;
        saveConfig();
    }
}
