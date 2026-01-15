package com.swaphat.clockIn.mixin;

import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.layouts.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mixin(net.minecraft.client.gui.Gui.class)
public class IngameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {

        graphics.textRendererForWidget(
                new AbstractClockWidget(
                    ConfigManager.getConfig().x,
                    ConfigManager.getConfig().y,
                    ConfigManager.getConfig().width,
                    ConfigManager.getConfig().height,
                    Component.literal(ConfigManager.getConfig().message.replace("%time%", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))),
                    ConfigManager.getConfig().color
                ),
                GuiGraphics.HoveredTextEffects.NONE
        );

    }
}