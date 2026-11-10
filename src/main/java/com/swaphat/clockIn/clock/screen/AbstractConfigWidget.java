package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;


public class AbstractConfigWidget extends AbstractWidget {

    protected final float x, y;
    protected final int color;
    protected  double mouseXClick = -1, mouseYClick = -1;

    private final Font font = Minecraft.getInstance().font;
    private final int lineHeight = font.lineHeight;

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

    private static String formatCord(float v) {
        // Always show exactly 2 decimal places for stable width
        return String.format("%.2f", v);
    }

    private void updateSegments() {
        segments.clear();

        String xLabel = "X: " + formatCord(AbstractClockWidget.x);
        String yLabel = "Y: " + formatCord(AbstractClockWidget.y);
        String scaleLabel = "Scale: " + ConfigManager.getConfig().scale;
        String formatLabel = "Format: " + AbstractClockWidget.getRenderedText();
        String bgLabel = "Background";

        segments.put("X",font.width(xLabel));
        segments.put("Y",font.width(yLabel));
        segments.put("Scale",font.width(scaleLabel));
        segments.put("Format",font.width(formatLabel));
        segments.put("Background", font.width(bgLabel));

        this.width = segments.values().stream().mapToInt(Integer::intValue).sum()
                + font.width(" ") * (segments.size() - 1);
        this.height = lineHeight;
    }


    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics) {
        updateSegments();

        int drawX = (int) x;
        int drawY = (int) y;
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int space = font.width(" ");

        for (Map.Entry<String, Integer> entry : segments.entrySet()) {
            String name = entry.getKey();
            int segWidth = entry.getValue();

            // Wrap to next line if segment doesn't fit
            if (drawX + segWidth > screenWidth) {
                drawX = (int) x;
                drawY += lineHeight;
            }

            // Background color per segment
            int bgColor = switch (name) {
                case "X" -> 0x8800FFFF;
                case "Y" -> 0x88FF00FF;
                case "Scale" -> 0x880000FF;
                case "Format" -> 0x88FFAA00;
                case "Background" -> ConfigManager.getConfig().backgroundColor;
                default -> 0x88000000;
            };

            graphics.fill(drawX, drawY, drawX + segWidth, drawY + lineHeight, bgColor);

            // Render text with special handling for "Format"
            if ("Format".equals(name)) {
                String prefix = "Format: ";
                String textPart = AbstractClockWidget.getRenderedText();

                int textPartX = drawX + font.width(prefix);

                // Draw prefix in normal color
                graphics.text(font, prefix, drawX, drawY, color);

                // Draw text in the actual text color
                int textColor = ConfigManager.getConfig().color;
                graphics.text(font, textPart, textPartX, drawY, textColor);

            } else {
                String text = switch (name) {
                    case "X" -> "X: " + formatCord(AbstractClockWidget.x);
                    case "Y" -> "Y: " + formatCord(AbstractClockWidget.y);
                    case "Scale" -> "Scale: " + AbstractClockWidget.scale;
                    case "Background" -> "Background";
                    default -> "";
                };

                graphics.text(font, text, drawX, drawY, color);
            }

            // Click detection (uses wrapped coordinates)
            if (mouseXClick >= drawX && mouseXClick <= drawX + segWidth
                    && mouseYClick >= drawY && mouseYClick <= drawY + lineHeight) {

                assert Minecraft.getInstance().gui.screen() != null;
                Minecraft.getInstance().setScreenAndShow(
                        new ConfigInputScreen(
                                Minecraft.getInstance().gui.screen(),
                                clockWidget,
                                name
                        )
                );

                mouseXClick = mouseYClick = -1;
            }

            // Advance cursor
            drawX += segWidth + space;
        }

        // Update widget size to match wrapped layout
        this.width = screenWidth;
        this.height = (drawY - (int) y) + lineHeight;

    }

    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        mouseXClick = event.x();
        mouseYClick = event.y();
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}
}