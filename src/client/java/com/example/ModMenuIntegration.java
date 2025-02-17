package com.example;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.awt.Color;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ModConfig.HANDLER.instance();

            return YetAnotherConfigLib.createBuilder()
                    .title(Text.literal("Clock HUD Settings"))
                    .category(ConfigCategory.createBuilder()
                            .name(Text.literal("General"))
                            .group(OptionGroup.createBuilder()
                                    .name(Text.literal("Display"))
                                    .option(Option.createBuilder(boolean.class)
                                            .name(Text.literal("Enabled"))
                                            .binding(
                                                    config.isEnabled(),
                                                    () -> config.isEnabled(),
                                                    value -> config.setEnabled(value)
                                            )
                                            .controller(TickBoxControllerBuilderImpl::new)
                                            .build())
                                    .option(Option.createBuilder(boolean.class)
                                            .name(Text.literal("24-Hour Format"))
                                            .binding(
                                                    config.isUse24Hour(),
                                                    () -> config.isUse24Hour(),
                                                    value -> config.setUse24Hour(value)
                                            )
                                            .controller(TickBoxControllerBuilderImpl::new)
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Text.literal("Position"))
                                    .option(Option.<Integer>createBuilder()
                                            .name(Text.literal("X Position"))
                                            .binding(
                                                    (int)config.getXPos(),
                                                    () -> (int)config.getXPos(),
                                                    value -> config.setXPos(value)
                                            )
                                            .controller(opt -> new IntegerFieldControllerBuilderImpl(opt))
                                            .build())
                                    .option(Option.<Integer>createBuilder()
                                            .name(Text.literal("Y Position"))
                                            .binding(
                                                    (int)config.getYPos(),
                                                    () -> (int)config.getYPos(),
                                                    value -> config.setYPos(value)
                                            )
                                            .controller(opt -> new IntegerFieldControllerBuilderImpl(opt))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Text.literal("Appearance"))
                                    .option(Option.<Color>createBuilder()
                                            .name(Text.literal("Color"))
                                            .binding(
                                                    new Color(config.getColor()),
                                                    () -> new Color(config.getColor()),
                                                    value -> config.setColor(value.getRGB())
                                            )
                                            .controller(opt -> new ColorControllerBuilderImpl(opt))
                                            .build())
                                    .build())
                            .build())
                    .save(() -> ModConfig.HANDLER.save())
                    .build()
                    .generateScreen(parent);
        };
    }
}