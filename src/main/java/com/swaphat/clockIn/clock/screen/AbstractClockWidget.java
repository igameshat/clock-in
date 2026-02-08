package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AbstractClockWidget extends AbstractWidget {

    protected static int backgroundColor;
    protected static float x;
    protected static float y;
    protected static float width;
    protected static float height;
    protected static float scale;
    protected static Component message;
    protected static int color;
    protected static ConfigStorage config = ConfigManager.getConfig();
    protected static boolean shadow = config.shadow;
    protected static float backgroundPaddingX = config.backgroundPaddingX;
    protected static float backgroundPaddingY = config.backgroundPaddingY;



    public boolean isInHUD = false;


    public AbstractClockWidget(float x, float y, float width, float height, Component message, int color) {
        super((int) x, (int) y, (int) width, (int) height, Component.nullToEmpty(message.getString().replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        AbstractClockWidget.x = x;
        AbstractClockWidget.y = y;
        AbstractClockWidget.message = message;
        AbstractClockWidget.color = color;
        AbstractClockWidget.width = width;
        AbstractClockWidget.height = height;
        AbstractClockWidget.scale = config.scale;
        AbstractClockWidget.backgroundColor = config.backgroundColor;
        AbstractClockWidget.backgroundPaddingX = config.backgroundPaddingX;
        AbstractClockWidget.backgroundPaddingY = config.backgroundPaddingY;

        // if clock out of bounds by default: fix it
        updateHitbox();
    }

    public static String getRenderedText() {
        return message.getString()
                .replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @Override
    public void renderWidget(
            @NonNull GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTicks
    ) {
        // Get fresh config values each render
        config = ConfigManager.getConfig();
        backgroundPaddingX = config.backgroundPaddingX;
        backgroundPaddingY = config.backgroundPaddingY;
        backgroundColor = config.backgroundColor;
        shadow = config.shadow;

        boundsCheckAndFix();
        updateHitbox();

        if (((backgroundColor >>> 24) & 0xFF) != 0x00) {
            graphics.fill(
                    (int)(x),
                    (int)(y),
                    (int)(x + Minecraft.getInstance().font.width(getRenderedText()) * scale + backgroundPaddingX * scale * 2),
                    (int)(y + Minecraft.getInstance().font.lineHeight * scale + backgroundPaddingY * scale * 2),
                    backgroundColor
            );
        }


        graphics.pose().pushMatrix();
        // Translate to position + padding offset, then scale
        graphics.pose().translate(
                x + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingX * scale : 0),
                y + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingY * scale : 0)
        );
        graphics.pose().scale(scale, scale);
        graphics.drawString(
                Minecraft.getInstance().font,
                getRenderedText(),
                0,
                0,
                color,
                shadow
        );
        graphics.pose().popMatrix();
    }

    public static void boundsCheckAndFix() {
        int screenW = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenH = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int boxW = (int) width;
        int boxH = (int) height;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x + boxW > screenW) x = screenW - boxW;
        if (y + boxH > screenH) y = screenH - boxH;
    }




    public static void setClockMessage(Component message) {
        AbstractClockWidget.message = message;
    }

    @Override
    public void onDrag(@NonNull MouseButtonEvent event, double dx, double dy) {
        if(!isInHUD) return;

        // move widget by drag delta
        x += (float) dx;
        y += (float) dy;

        // get screen size
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int width = (int) ((Minecraft.getInstance().font.width(getRenderedText()) + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingX * 2 : 0)) * scale);
        int height = (int) ((Minecraft.getInstance().font.lineHeight + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingY * 2 : 0)) * scale);

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > screenWidth) x = screenWidth - width;
        if (y + height > screenHeight) y = screenHeight - height;

        updateHitbox();
    }


    /** MUST BE CALLED WHENEVER TEXT OR POS OR SCALE CHANGES */
    void updateHitbox() {
        width = (Minecraft.getInstance().font.width(getRenderedText()) + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingX * 2 : 0)) * scale;
        height = (Minecraft.getInstance().font.lineHeight + (((backgroundColor >>> 24) & 0xFF) != 0x00 ? backgroundPaddingY * 2 : 0)) * scale;

        this.setPosition((int) x, (int) y);
        this.setWidth((int) width);
        this.setHeight((int) height);
    }


    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

    @Override
    public void onClick(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if(!isInHUD){
            Minecraft.getInstance().setScreen(new ClockMovingScreen());
        }
    }
}