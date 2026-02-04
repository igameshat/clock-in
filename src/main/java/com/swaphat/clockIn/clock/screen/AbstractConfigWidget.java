package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;


public class AbstractConfigWidget extends AbstractWidget {

    protected float x, y;
    protected int color;
    protected double mouseXClick = -1, mouseYClick = -1;

    private final Font font = Minecraft.getInstance().font;
    private final int lineHeight = font.lineHeight;

    // segment name -> width
    private final Map<String, Integer> segments = new LinkedHashMap<>();

    private final AbstractClockWidget clockWidget;

    public AbstractConfigWidget(float x, float y, int color, AbstractClockWidget clockWidget) {
        super((int) x, (int) y, 0, 0, Component.empty());
        this.x = x;
        this.y = y;
        this.color = color;
        this.clockWidget = clockWidget;
        updateSegments();
    }

    private void updateSegments() {
        segments.clear();

        segments.put("x", font.width("x: " + ConfigManager.getConfig().x));
        segments.put("y", font.width("y: " + ConfigManager.getConfig().y));
        segments.put("scale", font.width("scale: " + ConfigManager.getConfig().scale));
        segments.put("color", font.width(String.format("color: %08X", ConfigManager.getConfig().color)));
        segments.put("format", font.width("format: " + AbstractClockWidget.getRenderedText()));
        segments.put("background color", font.width("background color: " + ConfigManager.getConfig().backgroundColor));

        this.width = segments.values().stream().mapToInt(Integer::intValue).sum()
                + font.width(" ") * (segments.size() - 1);
        this.height = lineHeight;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        updateSegments();

        int drawX = (int) x;

        for (Map.Entry<String, Integer> entry : segments.entrySet()) {
            String name = entry.getKey();

            // segment width: use dynamic text width for "format" only
            int segWidth = segments.get(name);


            // background color per segment
            int bgColor = switch (name) {
                case "x" -> 0x8800FFFF;
                case "y" -> 0x88FF00FF;
                case "scale" -> 0x880000FF;
                case "color" -> ConfigManager.getConfig().color;
                case "format" -> 0x88FFFF00;
                case "background color" -> 0x8800FF00;
                default -> 0x88000000;
            };

            graphics.fill(drawX, (int) y, drawX + segWidth, (int) y + lineHeight, bgColor);

            String text = switch (name) {
                case "x" -> "x: " + AbstractClockWidget.x;
                case "y" -> "y: " + AbstractClockWidget.y;
                case "scale" -> "scale: " + AbstractClockWidget.scale;
                case "color" -> String.format("color: %08X", ConfigManager.getConfig().color);
                case "format" -> "format: " + AbstractClockWidget.getRenderedText();
                case "background color" -> "background color: " + AbstractClockWidget.backgroundColor;
                default -> "";
            };

            graphics.drawString(font, text, drawX, (int) y, color);

            // click detection
            if (mouseXClick >= drawX && mouseXClick <= drawX + segWidth
                    && mouseYClick >= y && mouseYClick <= y + lineHeight) {

                assert Minecraft.getInstance().screen != null;
                Minecraft.getInstance().setScreen(
                        new ConfigInputScreen(
                                Minecraft.getInstance().screen,
                                clockWidget,
                                name
                        )
                );

                mouseXClick = mouseYClick = -1;
            }

            drawX += segWidth + font.width(" ");
        }

        if (ConfigManager.getConfig().isDebug) {
            graphics.fill((int) x, (int) y, (int) x + width, (int) y + height, ConfigManager.getConfig().debugColor);
        }
    }


    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        mouseXClick = event.x();
        mouseYClick = event.y();
        if(ConfigManager.getConfig().isDebug) ConfigStorage.LOGGER.info("config click at: {}, {}", mouseXClick, mouseYClick);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}
}
