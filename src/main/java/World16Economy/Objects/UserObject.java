package World16Economy.Objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("UserObject")
public class UserObject implements ConfigurationSerializable {

    private UUID uuid;
    private long balance;

    public UserObject(UUID uuid, long balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalanceExact() {
        return balance;
    }

    public long getBalance() {
        return balance;
    }

    public String getBalanceFancy() {
        return "$" + balance;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addBalance(long number) {
        this.balance += number;
    }

    public void subtractBalance(long number) {
        this.balance -= number;
    }

    public void multipleBalance(long number) {
        this.balance *= number;
    }

    public boolean hasEnough(long number) {
        return number <= this.balance;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("UUID", uuid.toString());
        map.put("Balance", this.balance);
        return map;
    }

    public static UserObject deserialize(Map<String, Object> map) {
        return new UserObject(UUID.fromString((String) map.get("UUID")), (long) map.get("Balance"));
    }
}
