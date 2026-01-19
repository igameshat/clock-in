package com.swaphat.clockIn.mixin.hud;

import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.Config.ConfigStorage;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(net.minecraft.client.gui.Gui.class)
public class IngameHudMixin {

    @Unique
    private AbstractClockWidget clockWidget;


    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!(Minecraft.getInstance().screen instanceof PauseScreen)) {
            ConfigStorage configStorage = ConfigManager.getConfig();
            clockWidget = new AbstractClockWidget(
                    configStorage.x,
                    configStorage.y,
                    configStorage.width,
                    configStorage.height,
                    Component.literal(configStorage.message),
                    configStorage.color
            );
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (clockWidget != null) {
            // render the widget every frame
            clockWidget.renderWidget(graphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());

        }
    }
}