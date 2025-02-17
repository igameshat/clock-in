package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockinClient implements ClientModInitializer {
    private static boolean positioningMode = false;
    private static KeyBinding positionKey;

    @Override
    public void onInitializeClient() {
        ModConfig.HANDLER.load();

        // Register the key binding
        positionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.clockin.position", // Translation key
                GLFW.GLFW_KEY_P,       // Default to 'P' key
                "category.clockin.general" // The keybinding category
        ));

        // Your existing HUD render code
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ModConfig config = ModConfig.HANDLER.instance();

            // If in positioning mode, highlight the current position
            if (positioningMode) {
                MinecraftClient client = MinecraftClient.getInstance();
                double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();

                // Draw a message indicating positioning mode
                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        Text.literal("Click to position the clock"),
                        10,
                        10,
                        0xFFFFFF
                );

                // Draw preview at mouse position
                if (client.currentScreen == null) {
                    String timeFormat = config.isUse24Hour() ? "HH:mm" : "hh:mm a";
                    String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
                    drawContext.drawTextWithShadow(
                            client.textRenderer,
                            currentTime,
                            (int)mouseX,
                            (int)mouseY,
                            config.getColor()
                    );
                }
            }

            // Normal clock rendering
            if (!positioningMode && !MinecraftClient.getInstance().options.hudHidden) {
                if (!config.isEnabled()) return;

                MinecraftClient client = MinecraftClient.getInstance();
                String timeFormat = config.isUse24Hour() ? "HH:mm" : "hh:mm a";
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern(timeFormat));

                drawContext.drawTextWithShadow(
                        client.textRenderer,
                        currentTime,
                        (int)config.getXPos(),
                        (int)config.getYPos(),
                        config.getColor()
                );
            }
        });

        // Handle mouse clicks for positioning
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (positioningMode && client.currentScreen == null) {
                if (client.mouse.wasLeftButtonClicked()) {
                    double mouseX = client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth();
                    double mouseY = client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight();

                    ModConfig config = ModConfig.HANDLER.instance();
                    config.setXPos((float)mouseX);
                    config.setYPos((float)mouseY);
                    ModConfig.HANDLER.save();

                    positioningMode = false;
                    if (client.player != null) {
                        client.player.sendMessage(Text.literal("Clock position set!"), true);
                    }
                }
            }
        });

        // Toggle positioning mode when the key is pressed
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (positionKey.wasPressed()) {
                positioningMode = !positioningMode;
                String message = positioningMode ? "Positioning mode enabled - click to place the clock" : "Positioning mode disabled";
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(message), true);
                }
            }
        });
    }
}