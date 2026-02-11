package com.swaphat.clockIn.mixin.hud;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Gui.class)
public class IngameHudMixin {

    @Unique
    private AbstractClockWidget clockWidget;


    @Inject(method = "<init>", at = @At("TAIL"))
    private void addWidgetOnInit(CallbackInfo ci) {
        ConfigStorage config = ConfigManager.getConfig();
        if (!(Minecraft.getInstance().screen instanceof PauseScreen)) {

            clockWidget = new AbstractClockWidget(
                    config.x,
                    config.y,
                    config.width,
                    config.height,
                    Component.literal(config.message),
                    config.color
            );
            if (ConfigStorage.isDebug) ConfigStorage.LOGGER.info(config.x+"+"+ config.y);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (clockWidget != null) {
            clockWidget.renderWidget(graphics, (int) ConfigManager.getConfig().x, (int) ConfigManager.getConfig().y, deltaTracker.getRealtimeDeltaTicks());
        }
    }
}