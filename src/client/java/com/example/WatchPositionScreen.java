package com.example;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WatchPositionScreen extends Screen {
    private final Screen parent;
    private final ModConfig config;

    public WatchPositionScreen(Object parent, ModConfig config) {
        super(Text.literal("Position Watch"));
        this.parent = (Screen) parent;
        this.config = config;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            config.setXPos((float)mouseX);
            config.setYPos((float)mouseY);
            ModConfig.HANDLER.save();
            MinecraftClient.getInstance().setScreen(parent);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Draw instruction text
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("LeftClick anywhere to position the watch"),
                width / 2,
                20,
                0xFFFFFF);

        // Draw the watch at current mouse position to preview
        String timeFormat = config.isUse24Hour() ? "HH:mm" : "hh:mm a";
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
        context.drawTextWithShadow(textRenderer, currentTime, mouseX, mouseY, config.getColor());

        // Draw "ESC to cancel" text
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Press ESC to cancel"),
                width / 2,
                height - 30,
                0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}