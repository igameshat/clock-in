package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.Config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;



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

    AbstractClockWidget clockWidget = new AbstractClockWidget(
            ConfigManager.getConfig().x,
            ConfigManager.getConfig().y,
            ConfigManager.getConfig().width,
            ConfigManager.getConfig().height,
            Component.literal(ConfigManager.getConfig().message),
            ConfigManager.getConfig().color
    );

    AbstractConfigWidget configWidget = new AbstractConfigWidget(
            0, 0,
            0xFFFFFFFF,
            clockWidget
    );

    AbstractWidget textWidget = new AbstractWidget(
            Minecraft.getInstance().getWindow().getScreenWidth()/2, Minecraft.getInstance().getWindow().getGuiScaledHeight()/2,
            Minecraft.getInstance().font.width(Component.literal("press esc to close")),
            Minecraft.getInstance().font.lineHeight,
            Component.literal("press esc to close")


    ) {
        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float a) {
            graphics.drawCenteredString(
                    Minecraft.getInstance().font,
                    "press esc to close",
                    Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2,
                    Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2,
                    0xFFFFFFFF
            );
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {

        }
    };

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(clockWidget);
        ConfigStorage.LOGGER.info(ConfigManager.getConfig().x+ " "
                +ConfigManager.getConfig().y+" "+
                ConfigManager.getConfig().width+" "+
                ConfigManager.getConfig().height+" "+
                Component.literal(ConfigManager.getConfig().message)+" "+
                ConfigManager.getConfig().color);
        this.addRenderableWidget(configWidget);
        this.addRenderableWidget(textWidget);
        clockWidget.isInHUD = true;
    }

    @Override
    public void onClose() {
        clockWidget.isInHUD = false;
        ConfigManager.updateX(AbstractClockWidget.x);
        ConfigManager.updateY(AbstractClockWidget.y);
        ConfigManager.updateWidth(AbstractClockWidget.width);
        ConfigManager.updateHeight(AbstractClockWidget.height);
        ConfigManager.updateScale(AbstractClockWidget.scale);
        ConfigManager.updateColor(AbstractClockWidget.color);
        super.onClose();
        this.minecraft.setScreen(null);
    }
}
