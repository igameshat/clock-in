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

        segments.put("x", font.width(("x: " + ConfigManager.getConfig().x)));
        segments.put("y", font.width(("y: " + ConfigManager.getConfig().y)));
        segments.put("scale", font.width("scale: " + ConfigManager.getConfig().scale));

        // Merged text info segment with brackets around text
        String textInfoLabel = "text info: " + AbstractClockWidget.getRenderedText();
        segments.put("text info", font.width(textInfoLabel));

        // Merged background segment
        segments.put("background", font.width("background"));

        this.width = segments.values().stream().mapToInt(Integer::intValue).sum()
                + font.width(" ") * (segments.size() - 1);
        this.height = lineHeight;
    }

    @Override
    protected void renderWidget(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
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
                case "x" -> 0x8800FFFF;
                case "y" -> 0x88FF00FF;
                case "scale" -> 0x880000FF;
                case "text info" -> 0x88FFAA00; // Orange background for merged segment
                case "background" -> ConfigManager.getConfig().backgroundColor; // Use actual background color
                default -> 0x88000000;
            };

            graphics.fill(drawX, drawY, drawX + segWidth, drawY + lineHeight, bgColor);

            // Render text with special handling for "text info"
            if ("text info".equals(name)) {
                String prefix = "text info: ";
                String textPart = AbstractClockWidget.getRenderedText();

                int prefixX = drawX;
                int textPartX = drawX + font.width(prefix);

                // Draw prefix in normal color
                graphics.drawString(font, prefix, prefixX, drawY, color);

                // Draw text in brackets with the actual text color
                int textColor = ConfigManager.getConfig().color;
                graphics.drawString(font, textPart, textPartX, drawY, textColor);

            } else {
                String text = switch (name) {
                    case "x" -> ("x: " + AbstractClockWidget.x);
                    case "y" -> ("y: " + AbstractClockWidget.y);
                    case "scale" -> "scale: " + AbstractClockWidget.scale;
                    case "background" -> "background";
                    default -> "";
                };

                graphics.drawString(font, text, drawX, drawY, color);
            }

            // Click detection (uses wrapped coordinates)
            if (mouseXClick >= drawX && mouseXClick <= drawX + segWidth
                    && mouseYClick >= drawY && mouseYClick <= drawY + lineHeight) {

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

            // Advance cursor
            drawX += segWidth + space;
        }

        // Update widget size to match wrapped layout
        this.width = screenWidth;
        this.height = (drawY - (int) y) + lineHeight;

        // Debug overlay
        if (ConfigStorage.isDebug) {
            graphics.fill((int) x, (int) y, (int) x + width, (int) y + height,
                    ConfigManager.getConfig().debugColor);
        }
    }


    @Override
    public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
        mouseXClick = event.x();
        mouseYClick = event.y();
        if(ConfigStorage.isDebug) ConfigStorage.LOGGER.info("config click at: {}, {}", mouseXClick, mouseYClick);
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}
}