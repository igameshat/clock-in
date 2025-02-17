package com.example;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class ModConfig {
    public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            .id(Identifier.of("clock_in", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("clock_in.json"))
                    .build())
            .build();

    @SerialEntry
    private boolean enabled = true;

    @SerialEntry
    private boolean use24Hour = false;

    @SerialEntry
    private float xPos = 5.0f;

    @SerialEntry
    private float yPos = 5.0f;

    @SerialEntry
    private int color = 0xFFFFFF;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        HANDLER.save();
    }


    public boolean isUse24Hour() {
        return use24Hour;
    }

    public void setUse24Hour(boolean use24Hour) {
        this.use24Hour = use24Hour;
        HANDLER.save();
    }

    public float getXPos() {
        return xPos;
    }

    public void setXPos(float xPos) {
        this.xPos = xPos;
        HANDLER.save();
    }

    public float getYPos() {
        return yPos;
    }


    public void setYPos(float yPos) {
        this.yPos = yPos;
        HANDLER.save();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        HANDLER.save();
    }
}