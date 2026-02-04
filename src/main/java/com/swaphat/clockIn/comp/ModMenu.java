package com.swaphat.clockIn.comp;

import com.swaphat.clockIn.clock.screen.ClockMovingScreen;
import com.swaphat.clockIn.config.ConfigStorage;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        ConfigStorage.LOGGER.info("opening ModMenu config");

        return _ -> new ClockMovingScreen();
    }
}
