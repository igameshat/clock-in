package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockHudMod implements ClientModInitializer {
    private static final String MOD_ID = "clock_in";

    @Override
    public void onInitializeClient() {
        // Load the config
        ModConfig.HANDLER.load();

        // Register HUD renderer with the correct method signature
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ModConfig config = ModConfig.HANDLER.instance();
            if (!config.isEnabled() || MinecraftClient.getInstance().options.hudHidden) {
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            String timeFormat = config.isUse24Hour() ? "HH:mm" : "hh:mm a";
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern(timeFormat));

            // Use the configured position and color
            drawContext.drawTextWithShadow(
                    client.textRenderer,
                    currentTime,
                    (int)config.getXPos(),
                    (int)config.getYPos(),
                    config.getColor()
            );
        });

        // Register command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("clockin")
                    .then(ClientCommandManager.literal("toggle")
                            .executes(context -> {
                                ModConfig config = ModConfig.HANDLER.instance();
                                config.setEnabled(!config.isEnabled());
                                context.getSource().sendFeedback(Text.literal("Clock HUD " +
                                        (config.isEnabled() ? "enabled" : "disabled")));
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("format")
                            .executes(context -> {
                                ModConfig config = ModConfig.HANDLER.instance();
                                config.setUse24Hour(!config.isUse24Hour());
                                context.getSource().sendFeedback(Text.literal("Clock format set to " +
                                        (config.isUse24Hour() ? "24-hour" : "12-hour")));
                                return 1;
                            }))
            );
        });
    }
}