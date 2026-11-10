package com.swaphat.clockIn.clock.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import static com.swaphat.clockIn.clock.screen.AbstractClockWidget.*;

public class ConfigInputScreen extends Screen {

    private final Screen parentScreen;
    private final AbstractClockWidget clockWidget;
    private final String segmentName;

    private EditBox inputBox;
    private EditBox colorInputBox;
    private EditBox paddingXBox;
    private EditBox paddingYBox;

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

        switch (segmentName) {
            case "Format" -> {
                int currentColor = AbstractClockWidget.color;
                previewAlpha = (currentColor >>> 24) & 0xFF;
                previewRGB = currentColor & 0x00FFFFFF;

                OpacitySlider opacitySlider = new OpacitySlider(centerX - 100, centerY - 75, 200, 20, previewAlpha);
                addRenderableWidget(opacitySlider);

                int gap = 5;
                int boxWidth = 100;
                int btnWidth = font.width("Shadow: OFF") + 10; // shared width for both vanilla toggles

                int groupWidth = btnWidth + gap + boxWidth + gap + btnWidth;
                int groupStartX = centerX - groupWidth / 2;

                int inputBoxX = groupStartX + btnWidth + gap;
                int twelveHrButtonX = inputBoxX + boxWidth + gap;


                this.addRenderableWidget(
                        CycleButton.onOffBuilder(shadow).create(
                                groupStartX,
                                centerY - 30,
                                btnWidth,
                                20,
                                Component.literal("Shadow"),
                                (_, value) -> {
                                    shadow = value;
                                    ConfigManager.updateShadow(shadow);
                                }
                        )
                );

                // Text format input box — center of the group
                inputBox = new EditBox(
                        font,
                        inputBoxX,
                        centerY - 30,
                        boxWidth,
                        20,
                        Component.empty()
                );
                inputBox.setValue(message.getString());

                // 12H toggle button — RIGHT of input box, same size as Shadow
                this.addRenderableWidget(
                        CycleButton.onOffBuilder(ConfigStorage.format12Hour).create(
                                twelveHrButtonX,
                                centerY - 30,
                                btnWidth,
                                20,
                                Component.literal("12H"),
                                (_, value) -> ConfigManager.updateFormat12Hour(value)
                        )
                );

                // Color input (below text format with padding)
                colorInputBox = new EditBox(font, centerX - 100, centerY + 10, 200, 20, Component.empty());
                assert Minecraft.getInstance().gui.screen() != null;
                colorInputBox.setMaxLength(Minecraft.getInstance().gui.screen().width);
                colorInputBox.setHint(Component.literal("#RRGGBB"));

                colorInputBox.setValue(String.format("#%06X", previewRGB));

                inputBox.setResponder(_ -> applyTextInfo(inputBox.getValue(), colorInputBox.getValue()));
                colorInputBox.setResponder(_ -> applyTextInfo(inputBox.getValue(), colorInputBox.getValue()));

                addRenderableWidget(inputBox);
                addRenderableWidget(colorInputBox);
                setInitialFocus(inputBox);

            }
            case "Background" -> {
                // Background color input with opacity slider
                int currentColor = AbstractClockWidget.backgroundColor;
                previewAlpha = (currentColor >>> 24) & 0xFF;
                previewRGB = currentColor & 0x00FFFFFF;

                // Opacity slider
                OpacitySlider opacitySlider = new OpacitySlider(centerX - 100, centerY - 90, 200, 20, previewAlpha);
                addRenderableWidget(opacitySlider);

                // Color input
                inputBox = new EditBox(font, centerX - 100, centerY - 55, 200, 20, Component.empty());
                assert Minecraft.getInstance().gui.screen() != null;
                inputBox.setMaxLength(Minecraft.getInstance().gui.screen().width);
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

                inputBox.setResponder(_ -> applyBackground(inputBox.getValue(), paddingXBox.getValue(), paddingYBox.getValue()));
                paddingXBox.setResponder(_ -> applyBackground(inputBox.getValue(), paddingXBox.getValue(), paddingYBox.getValue()));
                paddingYBox.setResponder(_ -> applyBackground(inputBox.getValue(), paddingXBox.getValue(), paddingYBox.getValue()));

                addRenderableWidget(inputBox);
                addRenderableWidget(paddingXBox);
                addRenderableWidget(paddingYBox);
                setInitialFocus(inputBox);

            }
            case "X" -> {
                int screenW = minecraft.getWindow().getGuiScaledWidth();
                int maxX = screenW - (int) (Minecraft.getInstance().font.width(AbstractClockWidget.getRenderedText()) * AbstractClockWidget.scale);
                addRenderableWidget(createValueSlider(centerX - 100, centerY, 200, 20, "X", AbstractClockWidget.x, 0, maxX));
            }
            case "Y" -> {
                int screenH = minecraft.getWindow().getGuiScaledHeight();
                int maxY = screenH - (int) (Minecraft.getInstance().font.lineHeight * AbstractClockWidget.scale);
                addRenderableWidget(createValueSlider(centerX - 100, centerY, 200, 20, "Y", AbstractClockWidget.y, 0, maxY));
            }
            case "Scale" -> {
                addRenderableWidget(createValueSlider(centerX - 100, centerY, 200, 20, "Scale", AbstractClockWidget.scale, 1.0f, 10.0f));
            }
            case null, default -> {
                inputBox = new EditBox(font, centerX - 100, centerY, 200, 20, Component.empty());
                inputBox.setValue(getInitialValue());

                inputBox.setResponder(this::applyValue);

                addRenderableWidget(inputBox);
                setInitialFocus(inputBox);
            }
        }
    }

    private AbstractSliderButton createValueSlider(int x, int y, int w, int h, String axis, float current, float minVal, float maxVal) {
        double initialRatio = maxVal > minVal ? Math.clamp((double) (current - minVal) / (maxVal - minVal), 0.0D, 1.0D) : 0.0D;
        return new AbstractSliderButton(x, y, w, h, Component.empty(), initialRatio) {
            {
                updateMessage();
            }

            private float getTargetValue() {
                return minVal + (float) value * (maxVal - minVal);
            }

            @Override
            protected void updateMessage() {
                setMessage(Component.literal(axis.toUpperCase() + ": " + String.format("%.2f", getTargetValue())));
            }

            @Override
            protected void applyValue() {
                float v = Math.clamp(getTargetValue(), minVal, maxVal);
                switch (axis) {
                    case "X" -> {
                        AbstractClockWidget.x = v;
                        ConfigManager.updateX(v);
                    }
                    case "Background" -> {
                        AbstractClockWidget.y = v;
                        ConfigManager.updateY(v);
                    }
                    case "Scale" -> {
                        AbstractClockWidget.scale = v;
                        ConfigManager.updateScale(v);
                    }
                }
                clockWidget.updateHitbox();
            }
        };
    }

    private String getInitialValue() {
        return switch (segmentName) {
            case "X" -> String.valueOf(AbstractClockWidget.x);
            case "Background" -> String.valueOf(AbstractClockWidget.y);
            case "width" -> String.valueOf(config.width);
            case "height" -> String.valueOf(config.height);
            case "Scale" -> String.valueOf(config.scale);
            default -> "";
        };
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics) {
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);

        if ("Format".equals(segmentName)) {
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

        } else if ("Background".equals(segmentName)) {
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
            int boxWidth = (int) (textWidth + paddingX * 2);
            int boxHeight = (int) (textHeight + paddingY * 2);

            // Center the entire box
            int boxX = width / 2 - boxWidth / 2;
            int boxY = height / 2 + 20;

            // Draw preview background
            graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, previewColor);

            // Draw sample text centered within the padding
            graphics.text(font, config.message, boxX + (int) paddingX, boxY + (int) paddingY, 0xFFFFFFFF);

        } else {
            graphics.centeredText(font, title, width / 2, height / 2 - 30, 0xFFFFFFFF);
        }

        super.extractRenderState(graphics);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.input();
        boolean isEnterOrEsc = key == InputConstants.KEY_ESCAPE || key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER;

        if (isEnterOrEsc) {
            if ("Format".equals(segmentName)) {
                applyTextInfo(inputBox.getValue(), colorInputBox.getValue());
            } else if ("Background".equals(segmentName)) {
                applyBackground(inputBox.getValue(), paddingXBox.getValue(), paddingYBox.getValue());
            } else if (inputBox != null) {
                applyValue(inputBox.getValue());
            }
            minecraft.setScreenAndShow(parentScreen);
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
            paddingX = Math.clamp(paddingX, 0, 50);
            ConfigManager.updateBackgroundPaddingX(paddingX);

            // Update padding Y
            float paddingY = Float.parseFloat(paddingYValue);
            paddingY = Math.clamp(paddingY, 0, 50);
            ConfigManager.updateBackgroundPaddingY(paddingY);

            checkOpacityAndWarn(finalColor);

        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid background values: color={}, paddingX={}, paddingY={}",
                    colorValue, paddingXValue, paddingYValue);
        }
    }

    private void checkOpacityAndWarn(int finalColor) {
        if (((finalColor >>> 24) & 0xFF) == 0x00) ConfigStorage.LOGGER.info("opacity is set to 0 -> disabling background opacity");
        clockWidget.updateHitbox();
    }

    private void applyValue(String value) {
        try {
            int screenW = minecraft.getWindow().getGuiScaledWidth();
            int screenH = minecraft.getWindow().getGuiScaledHeight();

            switch (segmentName) {
                case "X" -> {
                    int x = Integer.parseInt(value);
                    x = Math.clamp(x, 0, screenW - Minecraft.getInstance().font.width(AbstractClockWidget.getRenderedText()));
                    ConfigManager.updateX(x);
                    AbstractClockWidget.x = x;
                }
                case "Y" -> {
                    int y = Integer.parseInt(value);
                    y = Math.clamp(y, 0, screenH - Minecraft.getInstance().font.lineHeight);
                    AbstractClockWidget.y = y;
                    ConfigManager.updateY(y);
                }
                case "Scale" -> {
                    float scale = Float.parseFloat(value);
                    scale = Math.clamp(scale, 1, 10);
                    AbstractClockWidget.scale = scale;
                    ConfigManager.updateScale(scale);
                }
                case "width" -> {
                    int w = Integer.parseInt(value);
                    AbstractClockWidget.width = w;
                    ConfigManager.updateWidth(w);
                }
                case "height" -> {
                    int h = Integer.parseInt(value);
                    AbstractClockWidget.height = h;
                    ConfigManager.updateHeight(h);
                }
            }

            clockWidget.updateHitbox();
        } catch (Exception e) {
            ConfigStorage.LOGGER.warn("Invalid value for {}: {}", segmentName, value);
        }
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
            if ("Format".equals(segmentName)) {
                AbstractClockWidget.color = finalColor;
                ConfigManager.updateColor(finalColor);
            } else if ("Background".equals(segmentName)) {
                checkOpacityAndWarn(finalColor);
                AbstractClockWidget.backgroundColor = finalColor;
                ConfigManager.updateBackgroundColor(finalColor);
            }

            // Update widget hitbox/rendering
            clockWidget.updateHitbox();
        }

    }

    @Override
    public void onClose() {
        super.onClose();

        this.minecraft.setScreenAndShow(parentScreen);
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