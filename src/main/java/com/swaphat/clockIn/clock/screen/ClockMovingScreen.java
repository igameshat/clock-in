package com.swaphat.clockIn.clock.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.swaphat.clockIn.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;



public class ClockMovingScreen extends Screen {
    private static final Component TITLE = Component.literal("Clock Moving Screen");

    public ClockMovingScreen() {
        super(TITLE);
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
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.centeredText(
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
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(configWidget);
        this.addRenderableWidget(clockWidget);
        this.addRenderableWidget(textWidget);
        this.setFocused(clockWidget);
        AbstractClockWidget.isInHUD = true;
    }

    @Override
    public boolean keyPressed(final @NonNull KeyEvent event) {
        if (AbstractClockWidget.isInHUD) {
            float moveSpeed = 2;
            switch(event.key()) {
                case InputConstants.KEY_LEFT, InputConstants.KEY_A -> AbstractClockWidget.x -= moveSpeed;
                case InputConstants.KEY_DOWN, InputConstants.KEY_S -> AbstractClockWidget.y += moveSpeed;
                case InputConstants.KEY_RIGHT, InputConstants.KEY_D -> AbstractClockWidget.x += moveSpeed;
                case InputConstants.KEY_UP, InputConstants.KEY_W -> AbstractClockWidget.y -= moveSpeed;
            }
        }
        ConfigManager.updateX(AbstractClockWidget.x);
        ConfigManager.updateY(AbstractClockWidget.y);
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        AbstractClockWidget.isInHUD = false;
        ConfigManager.updateX(AbstractClockWidget.x);
        ConfigManager.updateY(AbstractClockWidget.y);
        ConfigManager.updateWidth(AbstractClockWidget.width);
        ConfigManager.updateHeight(AbstractClockWidget.height);
        ConfigManager.updateScale(AbstractClockWidget.scale);
        ConfigManager.updateColor(AbstractClockWidget.color);
        super.onClose();
    }
}
