package com.swaphat.clockIn.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigStorage {

    public float x = 100f;
    public float y = 100f;

    public float width = 20f;
    public float height = 20f;

    public float scale = 5f;

    public boolean shadow = true;

    public int color = 0xFFFFFFFF;
    public String message = "clock %time%";

    public boolean isDebug = false;
    public int debugColor = 0x8800FF00;
    public static final Logger LOGGER = LoggerFactory.getLogger("clock-in");

}
