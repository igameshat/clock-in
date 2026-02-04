package com.swaphat.clockIn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigStorage {

    public static float x = (float) 0;
    public static float y = (float) 0;

    public float width = 0f;
    public float height = 0f;

    public float scale = 1.5f;

    public boolean shadow = true;

    public int color = 0xFFFFFFFF;
    public String message = "clock %time%";

    public int backgroundColor = 0x88000000;

    public boolean isDebug = false;
    public int debugColor = 0x8800FF00;
    public static final Logger LOGGER = LoggerFactory.getLogger("clock-in");

}
