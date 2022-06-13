package com.andrew121410.mc.world16economy.utils;

import com.andrew121410.mc.world16economy.World16Economy;

public class API {

    public static final String PREFIX = "[&9World1-6Economy&r]";
    public static final String DATE_OF_VERSION = "11/12/2020";
    public static final String USELESS_TAG = PREFIX + "->[&bUSELESS&r]";
    public static final String DEBUG_TAG = PREFIX + "->[&eDEBUG&r]";
    public static final String EMERGENCY_TAG = PREFIX + "->&c[EMERGENCY]&r";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong.";

    private final World16Economy plugin;

    private long defaultMoney = 100;

    public API(World16Economy plugin) {
        this.plugin = plugin;
    }

    public long getDefaultMoney() {
        return defaultMoney;
    }

    public void setDefaultMoney(long defaultMoney) {
        this.defaultMoney = defaultMoney;
    }
}
