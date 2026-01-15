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
    protected static Component message;
    protected static int color;
    protected static float width;
    protected static float height;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public AbstractClockWidget(float x, float y, float width, float height, Component message, int color) {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if(x > Minecraft.getInstance().getWindow().getGuiScaledWidth()) x = Minecraft.getInstance().getWindow().getGuiScaledWidth() - Minecraft.getInstance().font.width(message.getString().replace("%time%", LocalTime.now().format(FORMAT)));
        if(y > Minecraft.getInstance().getWindow().getScreenHeight()) y = Minecraft.getInstance().getWindow().getScreenHeight() - Minecraft.getInstance().font.lineHeight;
        super((int) x, (int) y, (int) width, (int) height, message);
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
                .replace("%time%", LocalTime.now().format(FORMAT));
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

        String text = getRenderedText();
        int textWidth = Minecraft.getInstance().font.width(text);
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
                text,
                (int) x + 2,
                (int) y + 2,
                color
        );

        graphics.drawString(
                Minecraft.getInstance().font,
                "press esc to close",
                Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - Minecraft.getInstance().font.width(Component.literal("press esc to close")) / 2,
                Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + Minecraft.getInstance().font.lineHeight / 2,
                0xFFFFFFFF
        );
    }

    public static void boundsCheckAndFix() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if(x > Minecraft.getInstance().getWindow().getScreenWidth()) x = Minecraft.getInstance().getWindow().getScreenWidth() - Minecraft.getInstance().font.width(message.getString().replace("%time%", LocalTime.now().format(FORMAT)));
        if(y > Minecraft.getInstance().getWindow().getScreenHeight()) y = Minecraft.getInstance().getWindow().getScreenHeight() - Minecraft.getInstance().font.lineHeight;
    }

    @Override
    public void onDrag(@NonNull MouseButtonEvent event, double dx, double dy) {
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
        String text = getRenderedText();

        int w = Minecraft.getInstance().font.width(text) + 4;
        int h = Minecraft.getInstance().font.lineHeight + 4;

        this.setPosition((int) x, (int) y);
        this.setWidth(w);
        this.setHeight(h);

    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}
}
