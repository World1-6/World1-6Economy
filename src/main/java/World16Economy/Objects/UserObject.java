package World16Economy.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UserObject {

    private UUID uuid;
    private long balance;

    public UserObject(UUID uuid, long balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalance() {
        return balance;
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

    public boolean ifHasEnough(long number) {
        return number < this.balance;
    }

    public void addBalance(long number) {
        this.balance += number;
    }

    public void subtractBalance(long number) {
        this.balance -= number;
    }

    public void multiplicationBalance(long number) {
        this.balance *= number;
    }

    public boolean checkIfHaveEnough(long number) {
        return number <= this.balance;
    }
}