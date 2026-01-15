package com.swaphat.clockIn.mixin.hud;

import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
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
        // create the widget once
        clockWidget = new AbstractClockWidget(
                ConfigManager.getConfig().x,
                ConfigManager.getConfig().y,
                ConfigManager.getConfig().width,
                ConfigManager.getConfig().height,
                Component.literal(ConfigManager.getConfig().message),
                ConfigManager.getConfig().color
        );
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (clockWidget != null) {
            // render the widget every frame
            clockWidget.renderWidget(graphics, 0, 0, deltaTracker.getRealtimeDeltaTicks());

        }
    }
}