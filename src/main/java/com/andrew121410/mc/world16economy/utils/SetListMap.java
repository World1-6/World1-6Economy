package com.andrew121410.mc.world16economy.utils;


import com.andrew121410.mc.world16economy.objects.MoneyObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetListMap {

    private Map<UUID, MoneyObject> moneyMap;

    public SetListMap() {
        this.moneyMap = new HashMap<>();
    }

    public Map<UUID, MoneyObject> getMoneyMap() {
        return moneyMap;
    }
}
