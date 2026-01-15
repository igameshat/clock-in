package com.swaphat.clockIn.Config;

import com.swaphat.clockIn.clock.screen.ClockMovingScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.literal("Clock In");

    public ConfigScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        Button buttonWidget = Button.builder(Component.nullToEmpty("Hello World"), (_) -> {
            // When the button is clicked, we can display a toast to the screen.
            this.minecraft.getToastManager().addToast(
                    SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.nullToEmpty("Hello World!"), Component.nullToEmpty("This is a toast."))
            );
        }).bounds(40, 40, 120, 20).build();
        // x, y, width, height
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button
        // textures.

        // Register the button widget.

        Button buttonWidget2 = Button.builder(Component.nullToEmpty("move clock"), (_) -> {
            this.minecraft.setScreen(new ClockMovingScreen());
            // When the button is clicked, we can display a toast to the screen.

        }).bounds(0, 0, 20, 20).build();
        this.addRenderableWidget(buttonWidget);
        this.addRenderableWidget(buttonWidget2);

    }
}
