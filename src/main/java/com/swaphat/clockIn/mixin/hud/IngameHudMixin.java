package com.swaphat.clockIn.mixin.hud;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
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
    private void addWidgetOnInit(final Minecraft minecraft, final Hud hud, final GuiRenderState guiRenderState, CallbackInfo ci) {
        ConfigStorage config = ConfigManager.getConfig();
        clockWidget = new AbstractClockWidget(
                config.x,
                config.y,
                config.width,
                config.height,
                Component.literal(config.message),
                config.color
        );
        if (ConfigStorage.isDebug) ConfigStorage.LOGGER.info(config.x + "+" + config.y);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, @Local GuiGraphicsExtractor graphics) {
        if (clockWidget != null) {
            if (shouldRenderLevel && !(Minecraft.getInstance().gui.screen() instanceof PauseScreen)) {
                clockWidget.extractRenderState(graphics, (int) ConfigManager.getConfig().x, (int) ConfigManager.getConfig().y, deltaTracker.getRealtimeDeltaTicks());
            }

        }
    }
}