package com.swaphat.clockIn.mixin.hud;

import com.swaphat.clockIn.config.ConfigManager;
import com.swaphat.clockIn.config.ConfigStorage;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class EscScreenMixin extends Screen {

    protected EscScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWidgetOnTAIL(CallbackInfo ci) {
        ConfigStorage config = ConfigManager.getConfig();

        AbstractClockWidget clockWidget = new AbstractClockWidget(
                config.x,
                config.y,
                config.width,
                config.height,
                Component.literal(config.message),
                config.color
        );

        this.addRenderableWidget(clockWidget);
    }
}