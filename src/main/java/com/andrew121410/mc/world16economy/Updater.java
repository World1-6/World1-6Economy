package com.andrew121410.mc.world16economy;

import com.andrew121410.mc.world16economy.World16Economy;
import com.andrew121410.mc.world16utils.utils.ccutils.utils.HashBasedUpdater;

public class Updater extends HashBasedUpdater {
    private static final String JAR_URL = "https://github.com/World1-6/World1-6Economy/releases/download/latest/World1-6Economy.jar";
    private static final String HASH_URL = "https://github.com/World1-6/World1-6Economy/releases/download/latest/hash.txt";

    public Updater(World16Economy plugin) {
        super(plugin.getClass(), JAR_URL, HASH_URL);
    }
}
