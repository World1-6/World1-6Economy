package World16Economy.Utils;

import World16Economy.Objects.UserObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetListMap {

    private Map<UUID, UserObject> moneyMap;

    public SetListMap() {
        this.moneyMap = new HashMap<>();
    }

    public Map<UUID, UserObject> getMoneyMap() {
        return moneyMap;
    }
}
