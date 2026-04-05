package com.swaphat.clockIn.clock.screen;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

import static com.swaphat.clockIn.clock.screen.AbstractClockWidget.*;


public class ConfigInputScreen extends Screen {

    private final Screen parentScreen;
    private final AbstractClockWidget clockWidget;
    private final String segmentName;

    private EditBox inputBox;
    private EditBox colorInputBox;
    private EditBox paddingXBox;
    private EditBox paddingYBox;
    private PositionSliderWidget positionSliderWidget;

    private int previewRGB;
    private int previewAlpha;

    private final ConfigStorage config = ConfigManager.getConfig();

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
            // Opacity slider (at the top)
            int currentColor = AbstractClockWidget.color;
            previewAlpha = (currentColor >>> 24) & 0xFF;
            previewRGB = currentColor & 0x00FFFFFF;

            OpacitySlider opacitySlider = new OpacitySlider(centerX - 100, centerY - 75, 200, 20, previewAlpha);
            addRenderableWidget(opacitySlider);

            // Both buttons share the same width (Shadow text is wider, so use that for both)
            // Layout: [ Shadow ] [ inputBox ] [ 12H ]
            // The entire group is centered as one unit.
            int gap = 5;
            int boxWidth = 100;
            int btnWidth = font.width("Shadow") + 6; // shared width for both buttons
            int groupWidth = btnWidth + gap + boxWidth + gap + btnWidth;
            int groupStartX = centerX - groupWidth / 2;

            int shadowButtonX  = groupStartX;
            int inputBoxX      = groupStartX + btnWidth + gap;
            int twelveHrButtonX = inputBoxX + boxWidth + gap;

            // Shadow toggle button — LEFT of input box
            this.addRenderableWidget(new Button(
                    shadowButtonX,
                    centerY - 30,
                    btnWidth,
                    20,
                    Component.literal("Shadow"),
                    _ -> {
                        shadow = !shadow;
                        ConfigManager.updateShadow(shadow);
                    },
                    new Button.CreateNarration() {
                        @Override
                        public @NonNull MutableComponent createNarrationMessage(@NonNull Supplier<MutableComponent> defaultNarrationSupplier) {
                            return Component.literal("");
                        }
                    })
            {
                @Override
                protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(shadowButtonX, centerY - 30, shadowButtonX + btnWidth, centerY - 10,
                            shadow ? 0x8800FF00 : 0x88FF0000);
                    graphics.text(font, "Shadow",
                            shadowButtonX + btnWidth / 2 - font.width("Shadow") / 2,
                            centerY - 24, 0xFFFFFFFF, shadow);
                }
            });

            // Text format input box — center of the group
            inputBox = new LimitedWidthEditBox(
                    inputBoxX,
                    centerY - 30,
                    boxWidth,
                    20,
                    Component.empty(),
                    boxWidth
            );
            inputBox.setValue(message.getString());

            // 12H toggle button — RIGHT of input box, same size as Shadow
            this.addRenderableWidget(new Button(
                    twelveHrButtonX,
                    centerY - 30,
                    btnWidth,
                    20,
                    Component.literal("12H"),
                    _ -> ConfigManager.updateFormat12Hour(!ConfigStorage.format12Hour),
                    new Button.CreateNarration() {
                        @Override
                        public @NonNull MutableComponent createNarrationMessage(@NonNull Supplier<MutableComponent> defaultNarrationSupplier) {
                            return Component.literal("");
                        }
                    })
            {
                @Override
                protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                    graphics.fill(twelveHrButtonX, centerY - 30, twelveHrButtonX + btnWidth, centerY - 10,
                            ConfigStorage.format12Hour ? 0x8800FF00 : 0x88FF0000);
                    graphics.text(font, "12H",
                            twelveHrButtonX + btnWidth / 2 - font.width("12H") / 2,
                            centerY - 24, 0xFFFFFFFF, false);
                }
            });

            // Color input (below text format with padding)
            colorInputBox = new EditBox(font, centerX - 100, centerY + 10, 200, 20, Component.empty());
            assert Minecraft.getInstance().screen != null;
            colorInputBox.setMaxLength(Minecraft.getInstance().screen.width);
            colorInputBox.setHint(Component.literal("#RRGGBB"));

            colorInputBox.setValue(String.format("#%06X", previewRGB));

            addRenderableWidget(inputBox);
            addRenderableWidget(colorInputBox);
            setInitialFocus(inputBox);

        } else if ("background".equals(segmentName)) {
            // Background color input with opacity slider
            int currentColor = AbstractClockWidget.backgroundColor;
            previewAlpha = (currentColor >>> 24) & 0xFF;
            previewRGB = currentColor & 0x00FFFFFF;

            // Opacity slider
            OpacitySlider opacitySlider = new OpacitySlider(centerX - 100, centerY - 90, 200, 20, previewAlpha);
            addRenderableWidget(opacitySlider);

            // Color input
            inputBox = new EditBox(font, centerX - 100, centerY - 55, 200, 20, Component.empty());
            assert Minecraft.getInstance().screen != null;
            inputBox.setMaxLength(Minecraft.getInstance().screen.width);
            inputBox.setHint(Component.literal("#RRGGBB"));
            inputBox.setValue(String.format("#%06X", previewRGB));

            // Padding X input
            paddingXBox = new EditBox(font, centerX - 100, centerY - 10, 95, 20, Component.empty());
            paddingXBox.setValue(String.valueOf(config.backgroundPaddingX));
            paddingXBox.setHint(Component.literal("Padding X"));

            // Padding Y input
            paddingYBox = new EditBox(font, centerX + 5, centerY - 10, 95, 20, Component.empty());
            paddingYBox.setValue(String.valueOf(config.backgroundPaddingY));
            paddingYBox.setHint(Component.literal("Padding Y"));

            addRenderableWidget(inputBox);
            addRenderableWidget(paddingXBox);
            addRenderableWidget(paddingYBox);
            setInitialFocus(inputBox);

        } else if ("x".equals(segmentName)) {
            int screenW = minecraft.getWindow().getGuiScaledWidth();
            int maxX = screenW - (int)(Minecraft.getInstance().font.width(AbstractClockWidget.getRenderedText()) * AbstractClockWidget.scale);
            positionSliderWidget = new PositionSliderWidget(centerX - 100, centerY, 200, 20, "x", AbstractClockWidget.x, 0, maxX);
            addRenderableWidget(positionSliderWidget);

        } else if ("y".equals(segmentName)) {
            int screenH = minecraft.getWindow().getGuiScaledHeight();
            int maxY = screenH - (int)(Minecraft.getInstance().font.lineHeight * AbstractClockWidget.scale);
            positionSliderWidget = new PositionSliderWidget(centerX - 100, centerY, 200, 20, "y", AbstractClockWidget.y, 0, maxY);
            addRenderableWidget(positionSliderWidget);

        } else {
            inputBox = new EditBox(font, centerX - 100, centerY, 200, 20, Component.empty());
            inputBox.setValue(getInitialValue());
            addRenderableWidget(inputBox);
            setInitialFocus(inputBox);
        }
    }

    private String getInitialValue() {
        return switch (segmentName) {
            case "x" -> String.valueOf(AbstractClockWidget.x);
            case "y" -> String.valueOf(AbstractClockWidget.y);
            case "width" -> String.valueOf(config.width);
            case "height" -> String.valueOf(config.height);
            case "scale" -> String.valueOf(config.scale);
            default -> "";
        };
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);

        if ("format".equals(segmentName)) {
            previewRGB = parseRGB(colorInputBox.getValue());
            int previewColor = (previewAlpha << 24) | previewRGB;

            // Title
            graphics.centeredText(font, title, width / 2, height / 2 - 130, 0xFFFFFFFF);

            // Hint text at top
            graphics.centeredText(
                    font,
                    Component.literal("use %time% to insert current time"),
                    width / 2,
                    height / 2 - 112,
                    0xAAFFFFFF
            );

            // Label for opacity slider
            graphics.centeredText(font, Component.literal("Text Opacity"), width / 2, height / 2 - 90, 0xFFFFFFFF);

            // Label for text format input (with more padding from slider)
            graphics.centeredText(font, Component.literal("Text Format"), width / 2, height / 2 - 45, 0xFFFFFFFF);

            // Label for color input (with padding from text format)
            graphics.centeredText(font, Component.literal("Text Color"), width / 2, height / 2 - 5, 0xFFFFFFFF);

            // Preview box with colored text
            String previewText = inputBox.getValue().isEmpty() ? message.getString() : inputBox.getValue();
            graphics.fill(width / 2 - 100, height / 2 + 45, width / 2 + 100, height / 2 + 75, config.backgroundColor);

            graphics.text(font, previewText, width / 2 - font.width(previewText) / 2, height / 2 + 55, previewColor, shadow);

        } else if ("background".equals(segmentName)) {
            previewRGB = parseRGB(inputBox.getValue());
            int previewColor = (previewAlpha << 24) | previewRGB;

            // Title
            graphics.centeredText(font, title, width / 2, height / 2 - 120, 0xFFFFFFFF);

            // Label for opacity slider
            graphics.centeredText(font, Component.literal("Background Opacity"), width / 2, height / 2 - 105, 0xFFFFFFFF);

            // Label for color input
            graphics.centeredText(font, Component.literal("Background Color"), width / 2, height / 2 - 70, 0xFFFFFFFF);

            // Label for padding inputs
            graphics.centeredText(font, Component.literal("Background Padding"), width / 2, height / 2 - 25, 0xFFFFFFFF);

            float paddingX = paddingXBox != null ? parsePadding(paddingXBox.getValue()) : config.backgroundPaddingX;
            float paddingY = paddingYBox != null ? parsePadding(paddingYBox.getValue()) : config.backgroundPaddingY;

            int textWidth = font.width(config.message);
            int textHeight = font.lineHeight;

            // Calculate box dimensions
            int boxWidth = (int)(textWidth + paddingX * 2);
            int boxHeight = (int)(textHeight + paddingY * 2);

            // Center the entire box
            int boxX = width / 2 - boxWidth / 2;
            int boxY = height / 2 + 20;

            // Draw preview background
            graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, previewColor);

            // Draw sample text centered within the padding
            graphics.text(font, config.message, boxX + (int)paddingX, boxY + (int)paddingY, 0xFFFFFFFF);

        } else {
            graphics.centeredText(font, title, width / 2, height / 2 - 30, 0xFFFFFFFF);
        }

        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.input();
        boolean isEnterOrEsc = key == 257 || key == 335 || key == 256;

        // If the position slider is in text-edit mode, let the widget handle
        // the key first so it can commit/cancel before we close the screen.
        if (positionSliderWidget != null && positionSliderWidget.isEditMode()) {
            super.keyPressed(event); // routes to widget
            // After the widget handled it: if still in edit mode (e.g. ESC just
            // exited edit mode but shouldn't close), stay on screen.
            if (positionSliderWidget.isEditMode()) return true;
            // Widget exited edit mode — if Enter was pressed, close now.
            if (key == 257 || key == 335) {
                minecraft.setScreen(parentScreen);
            }
            return true;
        }

        if (isEnterOrEsc) {
            if ("format".equals(segmentName)) {
                applyTextInfo(inputBox.getValue(), colorInputBox.getValue());
            } else if ("background".equals(segmentName)) {
                applyBackground(inputBox.getValue(), paddingXBox.getValue(), paddingYBox.getValue());
            } else if (inputBox != null) {
                applyValue(inputBox.getValue());
            }
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

    private float parsePadding(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0f;
        }
    }

    private void applyTextInfo(String textValue, String colorValue) {
        try {
            // Update text format
            ConfigManager.updateMessage(textValue);
            if(ConfigStorage.isDebug) ConfigStorage.LOGGER.info("changed message to {}", textValue);

            // Update color
            int rgb = parseRGB(colorValue);
            int finalColor = (previewAlpha << 24) | rgb;
            AbstractClockWidget.color = finalColor;
            ConfigManager.updateColor(finalColor);

            clockWidget.updateHitbox();
        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid text info values: text={}, color={}", textValue, colorValue);
        }
    }

    private void applyBackground(String colorValue, String paddingXValue, String paddingYValue) {
        try {
            // Update background color
            int rgb = parseRGB(colorValue);
            int finalColor = (previewAlpha << 24) | rgb;
            AbstractClockWidget.backgroundColor = finalColor;
            ConfigManager.updateBackgroundColor(finalColor);

            // Update padding X
            float paddingX = Float.parseFloat(paddingXValue);
            paddingX = clamp(paddingX, 0, 50);
            ConfigManager.updateBackgroundPaddingX(paddingX);

            // Update padding Y
            float paddingY = Float.parseFloat(paddingYValue);
            paddingY = clamp(paddingY, 0, 50);
            ConfigManager.updateBackgroundPaddingY(paddingY);

            if (((finalColor >>> 24) & 0xFF) == 0x00) {
                ConfigStorage.LOGGER.info("opacity is set to 0 -> disabling background opacity");
            }

            clockWidget.updateHitbox();
        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid background values: color={}, paddingX={}, paddingY={}",
                    colorValue, paddingXValue, paddingYValue);
        }
    }

    private void applyValue(String value) {
        try {
            int screenW = minecraft.getWindow().getGuiScaledWidth();
            int screenH = minecraft.getWindow().getGuiScaledHeight();

            switch (segmentName) {
                case "x" -> {
                    int x = Integer.parseInt(value);
                    x = (int) clamp(x, 0, screenW - Minecraft.getInstance().font.width(AbstractClockWidget.getRenderedText()));
                    ConfigManager.updateX(x);
                    AbstractClockWidget.x = x;
                }
                case "y" -> {
                    int y = Integer.parseInt(value);
                    y = (int) clamp(y, 0, screenH - Minecraft.getInstance().font.lineHeight);
                    AbstractClockWidget.y = y;
                    ConfigManager.updateY(y);
                }
                case "scale" -> {
                    float scale = Float.parseFloat(value);
                    scale = clamp(scale, 1, 10);
                    AbstractClockWidget.scale = scale;
                    ConfigManager.updateScale(scale);
                }
            }

            clockWidget.updateHitbox();
        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid value for {}: {}", segmentName, value);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }


    private class OpacitySlider extends AbstractSliderButton {

        public OpacitySlider(int x, int y, int w, int h, int alpha) {
            super(x, y, w, h, Component.literal("Opacity: " + alpha), alpha / 255.0D);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Opacity: " + (int) Math.round(value * 100) + "%"));
        }

        @Override
        protected void applyValue() {
            // Convert slider value (0.0–1.0) into alpha (0–255)
            previewAlpha = (int) Math.round(value * 255);

            // Combine alpha + RGB into final ARGB color
            int finalColor = (previewAlpha << 24) | previewRGB;

            // Apply to the correct target depending on segment
            if ("format".equals(segmentName)) {
                AbstractClockWidget.color = finalColor;
                ConfigManager.updateColor(finalColor);
            }
            else if ("background".equals(segmentName)) {
                if (((finalColor >>> 24) & 0xFF) == 0x00) ConfigStorage.LOGGER.info("opacity is set to 0 -> disabling background opacity");
                clockWidget.updateHitbox();
                AbstractClockWidget.backgroundColor = finalColor;
                ConfigManager.updateBackgroundColor(finalColor);
            }

            // Update widget hitbox/rendering
            clockWidget.updateHitbox();
        }

    }


    private class PositionSliderWidget extends AbstractWidget {

        private final String axis;
        private final float minVal;
        private final float maxVal;

        private float sliderValue;
        private boolean dragging = false;

        private boolean editMode = false;
        private final StringBuilder textBuf = new StringBuilder();
        private boolean cursorVisible = true;
        private long lastCursorBlink = System.currentTimeMillis();

        public PositionSliderWidget(int x, int y, int w, int h, String axis, float current, float minVal, float maxVal) {
            super(x, y, w, h, Component.empty());
            this.axis = axis;
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.sliderValue = maxVal > minVal ? (current - minVal) / (maxVal - minVal) : 0f;
        }

        private float currentValue() {
            return minVal + sliderValue * (maxVal - minVal);
        }

        private void commit(float v) {
            v = clamp(v, minVal, maxVal);
            if ("x".equals(axis)) {
                AbstractClockWidget.x = v;
                ConfigManager.updateX(v);
            } else {
                AbstractClockWidget.y = v;
                ConfigManager.updateY(v);
            }
            clockWidget.updateHitbox();
        }

        @Override
        protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            int wx = getX(), wy = getY(), ww = getWidth(), wh = getHeight();

            if (editMode) {
                // Text-box background
                graphics.fill(wx, wy, wx + ww, wy + wh, 0xFF000000);
                graphics.fill(wx + 1, wy + 1, wx + ww - 1, wy + wh - 1, 0xFF202020);

                // Blinking cursor
                long now = System.currentTimeMillis();
                if (now - lastCursorBlink > 500) { cursorVisible = !cursorVisible; lastCursorBlink = now; }

                String label = axis.toUpperCase() + ": ";
                String display = textBuf.toString() + (cursorVisible ? "|" : " ");
                String full = label + display;
                int textX = wx + ww / 2 - font.width(full) / 2;
                int textY = wy + wh / 2 - font.lineHeight / 2;
                graphics.text(font, label, textX, textY, 0xFFAAAAAA, false);
                graphics.text(font, display, textX + font.width(label), textY, 0xFFFFFFFF, false);

            } else {
                // Slider track
                graphics.fill(wx, wy, wx + ww, wy + wh, 0xFF000000);
                graphics.fill(wx + 1, wy + 1, wx + ww - 1, wy + wh - 1, 0xFF555555);

                int fillW = (int)((ww - 2) * sliderValue);
                if (fillW > 0) graphics.fill(wx + 1, wy + 1, wx + 1 + fillW, wy + wh - 1, 0xFF0088FF);

                String label = axis.toUpperCase() + ": " + String.format("%.2f", currentValue());
                graphics.text(font, label,
                        wx + ww / 2 - font.width(label) / 2,
                        wy + wh / 2 - font.lineHeight / 2,
                        0xFFFFFFFF, false);
            }
        }

        @Override
        public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (doubleClick) {
                editMode = true;
                dragging = false;
                textBuf.setLength(0);
                textBuf.append((int) currentValue());
            } else if (!editMode) {
                dragging = true;
                updateSliderFromMouse((float) event.x());
            }
        }

        @Override
        public void onDrag(@NonNull MouseButtonEvent event, double dx, double dy) {
            if (!editMode && dragging) {
                updateSliderFromMouse((float) event.x());
            }
        }

        @Override
        public void onRelease(@NonNull MouseButtonEvent event) {
            dragging = false;
        }

        private void updateSliderFromMouse(float mouseX) {
            float ratio = (mouseX - (getX() + 1)) / (float)(getWidth() - 2);
            sliderValue = clamp(ratio, 0f, 1f);
            commit(currentValue());
        }

        public boolean isEditMode() { return editMode; }

        @Override
        public boolean keyPressed(@NonNull KeyEvent event) {
            if (!editMode) return false;
            int key = event.input();
            if (key == 256) { editMode = false; return true; } // ESC — exit edit mode, screen stays open
            if (key == 257 || key == 335) { commitTextInput(); return true; } // ENTER — commit, screen will close
            if (key == 259) { // BACKSPACE
                if (!textBuf.isEmpty()) textBuf.deleteCharAt(textBuf.length() - 1);
                return true;
            }
            // Digit keys: 48–57 = row 0–9, 320–329 = numpad 0–9
            if ((key >= 48 && key <= 57) || (key >= 320 && key <= 329)) {
                int digit = (key >= 320) ? key - 320 : key - 48;
                textBuf.append((char)('0' + digit));
                return true;
            }
            return false;
        }

        private void commitTextInput() {
            try {
                float v = Float.parseFloat(textBuf.toString());
                v = clamp(v, minVal, maxVal);
                sliderValue = (maxVal > minVal) ? (v - minVal) / (maxVal - minVal) : 0f;
                commit(v);
            } catch (NumberFormatException ignored) {}
            editMode = false;
        }

        @Override
        protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {}
    }


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

    @Override
    public void onClose() {
        super.onClose();

        this.minecraft.setScreen(parentScreen);
        ConfigManager.updateX(AbstractClockWidget.x);
        ConfigManager.updateY(AbstractClockWidget.y);
        ConfigManager.updateWidth(AbstractClockWidget.width);
        ConfigManager.updateHeight(AbstractClockWidget.height);
        ConfigManager.updateScale(AbstractClockWidget.scale);

        ConfigManager.updateColor(AbstractClockWidget.color);
        ConfigManager.updateBackgroundColor(AbstractClockWidget.backgroundColor);
        ConfigManager.updateBackgroundPaddingX(config.backgroundPaddingX);
        ConfigManager.updateBackgroundPaddingY(config.backgroundPaddingY);

        ConfigManager.updateShadow(shadow);
        ConfigManager.updateMessage(message.getString());
    }
}