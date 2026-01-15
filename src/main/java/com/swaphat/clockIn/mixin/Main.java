package com.swaphat.clockIn.mixin;

import com.swaphat.clockIn.Config.ConfigStorage;
import net.minecraft.client.Minecraft;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.swaphat.clockIn.Config.ConfigManager.loadConfig;
import static com.swaphat.clockIn.Config.ConfigManager.saveConfig;


@Mixin(Minecraft.class)
public class Main {

    @Inject(at = @At("HEAD"), method = "run")
    private void init(CallbackInfo info) {
        ConfigStorage.LOGGER.info("Initializing clock-in");
        // This code is injected into the start of Minecraft.run()V
        loadConfig();

    }

    @Inject(at = @At("TAIL"), method = "run")
    private void oppositeOfInit(CallbackInfo info) {
        //forgot what the opposite of init and cant search it up without internet(didnt have it while naming & please stop judging me)
        saveConfig();


    }

}
