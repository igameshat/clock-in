package com.swaphat.clockIn.clock.screen;

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

    protected static float x;
    protected static float y;

    public static void setClockMessage(Component message) {
        AbstractClockWidget.message = message;
    }

    protected static Component message;
    protected static int color;
    protected static float width;
    protected static float height;

    public boolean isInHUD = false;

    public AbstractClockWidget(float x, float y, float width, float height, Component message, int color) {
        // if clock out of bounds: fix it
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if(x > Minecraft.getInstance().getWindow().getGuiScaledWidth()) x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - Minecraft.getInstance().font.width(message.getString().replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        if(y > Minecraft.getInstance().getWindow().getScreenHeight()) y = Minecraft.getInstance().getWindow().getScreenHeight() - Minecraft.getInstance().font.lineHeight;

        super((int) x, (int) y, (int) width, (int) height, Component.nullToEmpty(message.getString().replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        AbstractClockWidget.x = x;
        AbstractClockWidget.y = y;
        AbstractClockWidget.message = message;
        AbstractClockWidget.color = color;
        AbstractClockWidget.width = width;
        AbstractClockWidget.height = height;

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
        boundsCheckAndFix();
        updateHitbox();


        int textWidth = Minecraft.getInstance().font.width(getRenderedText());
        int textHeight = Minecraft.getInstance().font.lineHeight;

        graphics.fill(
                (int) x - 2,
                (int) y - 2,
                (int) x + textWidth + 4,
                (int) y + textHeight + 4,
                0x88000000
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                getRenderedText(),
                (int) x + 2,
                (int) y + 2,
                color
        );
    }

    public static void boundsCheckAndFix() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if(x > Minecraft.getInstance().getWindow().getScreenWidth()) x = Minecraft.getInstance().getWindow().getScreenWidth() - Minecraft.getInstance().font.width(getRenderedText());
        if(y > Minecraft.getInstance().getWindow().getScreenHeight()) y = Minecraft.getInstance().getWindow().getScreenHeight() - Minecraft.getInstance().font.lineHeight;
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

        // width
        int w = Minecraft.getInstance().font.width(getRenderedText()) + 4;
        // height
        int h = Minecraft.getInstance().font.lineHeight + 4;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + w > screenWidth) x = screenWidth - w;
        if (y + h > screenHeight) y = screenHeight - h;

        updateHitbox();
    }


    /** MUST BE CALLED WHENEVER TEXT OR POS CHANGES */
    void updateHitbox() {

        int w = Minecraft.getInstance().font.width(getRenderedText()) + 4;
        int h = Minecraft.getInstance().font.lineHeight + 4;

        this.setPosition((int) x, (int) y);
        this.setWidth(w);
        this.setHeight(h);

    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}

    @Override
    public void onClick(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if(isInHUD) return;
        Minecraft.getInstance().setScreen(new ClockMovingScreen());
    }
}
