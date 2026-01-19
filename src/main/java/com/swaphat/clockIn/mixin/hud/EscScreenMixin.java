package com.swaphat.clockIn.mixin.hud;



import com.swaphat.clockIn.Config.ConfigManager;
import com.swaphat.clockIn.clock.screen.AbstractClockWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PauseScreen.class)
public class EscScreenMixin extends Screen {

    @Unique
    private AbstractClockWidget clockWidget;

    protected EscScreenMixin(Component title) {
        super(title);
    }


    @Inject(method = "init", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        clockWidget = new AbstractClockWidget(
                ConfigManager.getConfig().x,
                ConfigManager.getConfig().y,
                ConfigManager.getConfig().width,
                ConfigManager.getConfig().height,
                Component.literal(ConfigManager.getConfig().message),
                ConfigManager.getConfig().color
        );
        clockWidget.setX((int) ConfigManager.getConfig().x);
        clockWidget.setY((int) ConfigManager.getConfig().y);
        this.addRenderableWidget(clockWidget);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (clockWidget != null) {
            // render the widget every frame
            clockWidget.render(graphics, mouseX, mouseY, a);

        }
    }
}
