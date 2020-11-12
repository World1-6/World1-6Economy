package com.andrew121410.mc.world16economy.utils;

import com.andrew121410.mc.world16economy.World16Economy;

public class API {

    private World16Economy plugin;

    //Finals
    public static final Integer VERSION = 3;
    public static final String DATE_OF_VERSION = "4/13/2020";
    public static final String PREFIX = "[&9World1-6Economy&r]";
    public static final String USELESS_TAG = PREFIX + "->[&bUSELESS&r]";
    public static final String DEBUG_TAG = PREFIX + "->[&eDEBUG&r]";
    public static final String EMERGENCY_TAG = PREFIX + "->&c[EMERGENCY]&r";
    public static final String TOO_DAMN_OLD = "Your mc version is too damn old 1.11 up too 1.14.3 please.";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong.";

    //Config stuff
    private long DEFAULT_MONEY = 100;
    private boolean DEBUG = false;

    public API(World16Economy plugin) {
        this.plugin = plugin;
    }

    public static boolean isInteger(String numberMaybe) {
        try {
            Integer.parseInt(numberMaybe);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isLong(String longMaybe) {
        try {
            Long.parseLong(longMaybe);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Getters

    public long getDEFAULT_MONEY() {
        return DEFAULT_MONEY;
    }

    public void setDEFAULT_MONEY(long DEFAULT_MONEY) {
        this.DEFAULT_MONEY = DEFAULT_MONEY;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }
}
