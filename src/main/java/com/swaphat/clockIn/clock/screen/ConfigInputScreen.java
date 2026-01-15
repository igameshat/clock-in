package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.Config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class ConfigInputScreen extends Screen {

    private final Screen parentScreen;
    private final AbstractClockWidget clockWidget;
    private final String segmentName;

    private EditBox inputBox;

    private int previewRGB;
    private int previewAlpha;

    public ConfigInputScreen(Screen parentScreen, AbstractClockWidget clockWidget, String segmentName) {
        super(Component.literal("Set value for " + segmentName));
        this.parentScreen = parentScreen;
        this.clockWidget = clockWidget;
        this.segmentName = segmentName;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        if ("format".equals(segmentName)) {
            // Use the pixel-width-limited EditBox for format
            int boxWidth = 200;
            inputBox = new LimitedWidthEditBox(
                    centerX - boxWidth / 2,
                    centerY,
                    boxWidth,
                    20,
                    Component.empty(),
                    boxWidth
            );
            inputBox.setValue(ConfigManager.getConfig().message);
        } else {
            inputBox = new EditBox(font, centerX - 100, centerY, 200, 20, Component.empty());
            assert Minecraft.getInstance().screen != null;
            inputBox.setMaxLength(Minecraft.getInstance().screen.width);
            inputBox.setHint(Component.literal("#RRGGBB"));

            if ("color".equals(segmentName)) {
                int currentColor = AbstractClockWidget.color;

                previewAlpha = (currentColor >>> 24) & 0xFF;
                previewRGB   = currentColor & 0x00FFFFFF;

                inputBox.setValue(String.format("#%06X", previewRGB));

                OpacitySlider opacitySlider = new OpacitySlider(centerX - 100, centerY - 35, 200, 20, previewAlpha);
                addRenderableWidget(opacitySlider);
            } else {
                inputBox.setValue(getInitialValue());
            }
        }

        addRenderableWidget(inputBox);
        setInitialFocus(inputBox);
    }

    private String getInitialValue() {
        return switch (segmentName) {
            case "x" -> String.valueOf(AbstractClockWidget.x);
            case "y" -> String.valueOf(AbstractClockWidget.y);
            case "width" -> String.valueOf(ConfigManager.getConfig().width);
            case "height" -> String.valueOf(ConfigManager.getConfig().height);
            case "format" -> ConfigManager.getConfig().message;
            default -> "";
        };
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);

        if ("color".equals(segmentName)) {
            previewRGB = parseRGB(inputBox.getValue());
        }

        int previewColor = (previewAlpha << 24) | previewRGB;

        graphics.drawCenteredString(font, title, width / 2, height / 2 - 60, 0xFFFFFFFF);

        inputBox.render(graphics, mouseX, mouseY, delta);

        if ("color".equals(segmentName)) {
            graphics.fill(width / 2 - 40, height / 2 + 25, width / 2 + 40, height / 2 + 45, previewColor);
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.input() == 257 || event.input() == 335 || event.input() == 256) { // ENTER / NUMPAD ENTER / ESC
            applyValue(inputBox.getValue());
            minecraft.setScreen(parentScreen);
            return true;
        }
        return super.keyPressed(event);
    }

    private int parseRGB(String value) {
        try {
            if (value.startsWith("#")) value = value.substring(1);
            return Integer.parseInt(value, 16) & 0x00FFFFFF;
        } catch (Exception e) {
            return 0xFF0000;
        }
    }

    private void applyValue(String value) {
        try {
            int screenW = minecraft.getWindow().getGuiScaledWidth();
            int screenH = minecraft.getWindow().getGuiScaledHeight();

            switch (segmentName) {
                case "x" -> {
                    float x = Float.parseFloat(value);
                    x = clamp(x, 0, screenW - Minecraft.getInstance().font.width(AbstractClockWidget.getRenderedText()));
                    AbstractClockWidget.x = x;
                    ConfigManager.updateX(x);
                }
                case "y" -> {
                    float y = Float.parseFloat(value);
                    y = clamp(y, 0, screenH - Minecraft.getInstance().font.lineHeight);
                    AbstractClockWidget.y = y;
                    ConfigManager.updateY(y);
                }
                case "width" -> {
                    float w = Float.parseFloat(value);
                    w = clamp(w, 1, screenW - AbstractClockWidget.x);
                    ConfigManager.updateWidth(w);
                }
                case "height" -> {
                    float h = Float.parseFloat(value);
                    h = clamp(h, 1, screenH - AbstractClockWidget.y);
                    ConfigManager.updateHeight(h);
                }
                case "format" -> {
                    ConfigManager.updateMessage(value);
                    AbstractClockWidget.message = Component.literal(value);
                }
                case "color" -> {
                    int rgb = parseRGB(value);
                    int finalColor = (previewAlpha << 24) | rgb;
                    AbstractClockWidget.color = finalColor;
                    ConfigManager.updateColor(finalColor);
                }
            }

            clockWidget.updateHitbox();
        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid value for " + segmentName + ": " + value);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /* =================================================
       INNER SLIDER CLASS — DO NOT TOUCH value AFTER INIT
       ================================================= */
    private class OpacitySlider extends AbstractSliderButton {

        public OpacitySlider(int x, int y, int w, int h, int alpha) {
            super(x, y, w, h, Component.empty(), alpha / 255.0D);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Opacity: " + (int) Math.round(value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            previewAlpha = (int) Math.round(value * 255);
        }
    }

    /* =================================================
       INNER PIXEL-WIDTH LIMITED EDITBOX
       ================================================= */
    private class LimitedWidthEditBox extends EditBox {
        private final int maxPixelWidth;

        public LimitedWidthEditBox(int x, int y, int width, int height, Component message, int maxPixelWidth) {
            super(font, x, y, width, height, message);
            this.maxPixelWidth = maxPixelWidth;
        }

        @Override
        public void insertText(@NonNull String textToAdd) {
            String newText = getValue() + textToAdd;
            if (font.width(newText) <= maxPixelWidth) {
                super.insertText(textToAdd);
            }
            // ignore input that would exceed width
        }
    }
}
