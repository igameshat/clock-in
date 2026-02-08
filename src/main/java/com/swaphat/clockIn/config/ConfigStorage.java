package com.swaphat.clockIn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigStorage {

    public float x = 100;
    public float y = 100;

    public float width = 100f;
    public float height = 100f;

    public float scale = 1.5f;

    public boolean shadow = true;

    public int color = 0xFFFFFFFF;
    public String message = "%time%";

    public int backgroundColor = 0x88000000;
    public float backgroundPaddingX = 4;
    public float backgroundPaddingY = 2;

    public static boolean isDebug = false;
    public static int debugColor = 0x8800FF00;
    public static final Logger LOGGER = LoggerFactory.getLogger("clock-in");

}
