package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.Config.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.swaphat.clockIn.clock.screen.AbstractClockWidget.color;
import static com.swaphat.clockIn.clock.screen.AbstractClockWidget.message;

public class ClockMovingScreen extends Screen {
    private static final Component TITLE = Component.literal("Clock Moving Screen");

    public ClockMovingScreen() {
        super(TITLE);
    }

    @Override
    public void renderBackground(final @NonNull GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        if (this.minecraft.level == null) {
            this.renderPanorama(graphics, a);
        }

        this.renderBlurredBackground(graphics);
        this.renderMenuBackground(graphics);

        this.minecraft.gui.renderDeferredSubtitles();
    }

    // Create clock widget
    AbstractClockWidget clockWidget = new AbstractClockWidget(
            ConfigManager.getConfig().x,
            ConfigManager.getConfig().y,
            ConfigManager.getConfig().width,
            ConfigManager.getConfig().height,
            Component.literal(ConfigManager.getConfig().message.replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))),
            ConfigManager.getConfig().color
    );

    // Pass clock widget as 4th argument
    AbstractConfigWidget configWidget = new AbstractConfigWidget(
            0, 0,
            0xFFFFFFFF,
            clockWidget
    );

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(clockWidget);
        this.addRenderableWidget(configWidget);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.setScreen(null);
        ConfigManager.updateX(AbstractClockWidget.x);
        ConfigManager.updateY(AbstractClockWidget.y);
        ConfigManager.updateMessage(message.getString());
        ConfigManager.updateColor(color);
    }
}
