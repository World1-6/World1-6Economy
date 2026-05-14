# World1-6Economy — Developer & AI Context

This document describes the full architecture, design decisions, patterns, and gotchas for this codebase. It is intended for developers and AI assistants picking up this project cold.

---

## Project Overview

A Paper plugin providing a multi-currency economy system. Key systems:
- **Wallet** — per-player in-memory+YAML balance store, Vault-backed
- **Currencies** — configurable, multi-currency, YAML-stored
- **Notes** — dupe-proof physical currency items backed by SQLite
- **Mob Drops** — currency rewards on entity death
- **Bank Accounts** — tiered accounts with roles, payroll, transaction history, SQLite-stored

**Entry point:** `World16Economy.java` — standard JavaPlugin, calls `registerManagers()` → `registerListeners()` → `registerCommands()` on enable.

**Dependencies:**
- `io.papermc.paper:paper-api:26.1.2.build.63-stable`
- `com.github.World1-6.World1-6Utils` (via JitPack, hash-pinned)
- `com.github.MilkBowl:VaultAPI:1.7.1`

**Build:** Gradle + Shadow JAR. Run `./gradlew build`. Test server via `./gradlew runServer` (auto-downloads Vault and World1-6Utils).

---

## Package Structure

```
com.andrew121410.mc.world16economy
├── World16Economy.java          # Plugin main class, manager/command/listener registration
├── VaultCore.java               # Vault economy provider implementation
├── Updater.java                 # Update checker
│
├── bank/                        # Bank account system
│   ├── BankAccount.java         # Model: balances map, members map, scheduled payroll
│   ├── BankAccountType.java     # Enum: PERSONAL, BUSINESS
│   ├── BankManager.java         # In-memory manager, payroll scheduler, CRUD operations
│   ├── BankMember.java          # Model: playerUUID, role, wage, payrollDestinationAccountUUID
│   ├── BankRole.java            # Enum: OWNER, MANAGER, EMPLOYEE, VIEWER + permission methods
│   ├── BankScheduledPayroll.java# Model: intervalMs, lastRunMs, currencyUUID, onlineOnly
│   ├── BankStorage.java         # SQLite persistence via EasySQL (6 tables)
│   ├── BankTier.java            # Model: level, displayName, creationCost, balanceLimit, transactionLimit
│   └── BankTransaction.java     # Model + Type enum: DEPOSIT, WITHDRAWAL, TRANSFER_IN/OUT, PAYROLL_IN/OUT
│
├── commands/
│   ├── bal.java                 # /bal [player] — wallet GUI
│   ├── bank.java                # /bank [account name] — bank list GUI, tab completes account names
│   ├── eco.java                 # /eco [give|take|set|reset <player> <amount> [currency]]
│   └── withdraw.java            # /withdraw <amount> [currency] — creates note item
│
├── currency/
│   ├── Currency.java            # Model: uuid, name, symbol, singular/plural, color, material, defaultBalance, mobDropManager
│   ├── MobDropEntry.java        # Model: min, max, chance, entityTypes, dropMode (WALLET/NOTE)
│   └── MobDropManager.java      # List<MobDropEntry> per currency
│
├── gui/
│   ├── CurrencyListGUI.java     # /eco main screen — list + create
│   ├── CurrencyEditGUI.java     # Edit all currency fields, set default, delete (smart)
│   ├── CurrencyWalletGUI.java   # Per-currency wallet view with withdraw-to-note button
│   ├── MobDropListGUI.java      # List mob drop entries for a currency
│   ├── MobDropEntryEditGUI.java # Edit a single mob drop entry
│   └── bank/
│       ├── BankListGUI.java           # All accounts the player is a member of
│       ├── BankCreateGUI.java         # Name input + tier 1 cost display, deducts from wallet
│       ├── BankAccountGUI.java        # Main account view: balances, actions, role-gated buttons
│       ├── BankDepositWithdrawGUI.java# 2-screen flow: currency picker → amount picker (+/- buttons + type exact)
│       ├── BankTransferGUI.java       # 2-screen flow: pick destination account → amount input
│       ├── BankMembersGUI.java        # Paginated member list, invite button
│       ├── BankMemberEditGUI.java     # Role cycle, wage input, kick
│       ├── BankPayrollGUI.java        # Payroll config: interval, currency, online-only, run now, remove
│       ├── BankPayrollDestinationGUI.java # Employee self-service: pick where their wage goes
│       ├── BankTransactionHistoryGUI.java # Paginated transaction list, newest first
│       └── BankUpgradeGUI.java        # Current tier info, next tier cost, upgrade button
│
├── listeners/
│   ├── OnEntityDeathListener.java  # Mob drop rewards (wallet credit or note drop)
│   ├── OnNoteRedeemListener.java   # Right-click note item → redeem via NoteManager
│   ├── OnPlayerJoinEvent.java      # Load wallet from disk into cache
│   └── OnPlayerQuitEvent.java      # Save wallet to disk, remove from cache
│
├── managers/
│   ├── CurrenciesManager.java   # In-memory map of UUID→Currency, default currency UUID
│   └── WalletManager.java       # In-memory map of UUID→Wallet
│
├── storage/
│   ├── StorageManager.java      # YAML load/save for currencies and wallets
│   ├── NoteManager.java         # SQLite CRUD for currency notes (CurrencyNotes.db)
│   └── serializers/             # Configurate type serializers for Currency, Wallet, CurrencyWallet, MobDropManager, MobDropEntry
│
└── user/
    ├── Wallet.java              # Map<UUID, CurrencyWallet> keyed by currency UUID
    └── CurrencyWallet.java      # Holds balanceExact (double), add/subtract/setBalanceExact methods
```

---

## Storage Architecture

### YAML (Configurate)
Used for currencies and wallets. Managed by `StorageManager`. Custom type serializers in `storage/serializers/`.

- `currencies.yml` — all currency definitions + mob drop config + default currency UUID
- `wallets.yml` — all player wallet balances

### SQLite (EasySQL / CCUtilsJava)
Used for notes and bank data. `EasySQL` wraps `MultiTableEasySQL` wraps `SQLite`.

**Critical pattern — "get all" queries:**
- `easySQL.get(query)` generates `WHERE (key=?)` — requires at least one field or it produces invalid SQL `WHERE ()`
- `easySQL.getEverything()` generates `SELECT * FROM table` — use this for loading all rows with no filter
- Delete-then-save pattern is used everywhere (no upsert support)

**Bank tables (BankAccounts.db):**
| Table | Primary key(s) | Notes |
|---|---|---|
| Accounts | AccountUUID | Name, OwnerUUID, Type, TierLevel |
| Balances | AccountUUID + CurrencyUUID | One row per currency per account |
| Members | AccountUUID + PlayerUUID | Role, Wage, PayrollDestination (empty string = personal wallet) |
| Transactions | TransactionUUID | Append-only, sorted newest-first on load |
| Tiers | TierLevel | Seeded with 3 defaults if empty on startup |
| Payroll | AccountUUID | One row per account, includes OnlineOnly flag |

---

## GUI Framework (World1-6Utils)

All GUIs use a custom framework from World1-6Utils. Key classes:

- `GUIWindow` — fixed-size inventory GUI. Override `onCreate(Player)`, call `this.update(buttons, title, size)`.
- `MiddleGUIWindow` — auto-sizes based on button count. Pass `null` for size in `update()`.
- `GUIMultipageListWindow` — paginated list. Takes `List<CloneableGUIButton>`. Use `getCustomBottomButtons()` to add fixed bottom-row buttons.

**Button types:**
- `NoEventButton(slot, itemStack)` — display only
- `ClickEventButton(slot, itemStack, event -> {...})` — click handler
- `ChatResponseButton(slot, itemStack, null, null, (player, input) -> {...})` — closes GUI, waits for chat, re-opens GUI in callback

**Animation:**
- `.animate(Supplier<ItemStack>)` — fluent method on any button type, makes it animated
- `Animation.wave(text, color1, color2, ...)` — sweeping gradient across characters
- `Animation.fading(text, color1, color2, ...)` — full-text color ping-pong
- Frame selection: `(System.currentTimeMillis() / 50) % frameCount` — purely time-based, stateless
- Animation loop: `runTaskTimer` every 2L ticks in `AbstractGUIWindow`, calls `animationTick()` in `GUIWindow`

**Slot numbering:** Standard Bukkit inventory slots (0 = top-left, row by row). For `MiddleGUIWindow` and `GUIMultipageListWindow`, slot numbers in buttons are ignored — items are placed sequentially.

---

## Wallet Save Rules

**All wallets are loaded into memory on startup** via `StorageManager.loadAllWallets()`. There is no longer a distinction between online and offline players — every wallet is always in `WalletManager.getWallets()`.

**Wallets must be saved immediately after any mutation.** Every place that calls `addAmount`, `subtractAmount`, or `setBalanceExact` on a `CurrencyWallet` must follow up with:

```java
plugin.getStorageManager().saveWallet(wallet);
```

`OnPlayerJoinEvent` only creates a new wallet for players who have never played before (not in the map). `OnPlayerQuitEvent` saves the wallet to disk but does not remove it from memory. `onDisable` saves all wallets as a final safety net.

For players who have genuinely never joined (e.g. `/eco give` targeting an unknown name), use `walletManager.newUser(uuid, true)` as the fallback — this creates, caches, and saves the wallet.

---

## Currency System

`CurrenciesManager` holds `Map<UUID, Currency>` in memory. Currencies are loaded from YAML on startup and saved back on shutdown (and after each admin edit via `saveAllCurrencies()`).

**Default currency:** One currency is designated as the Vault currency. UUID stored separately in `currencies.yml`. If the default currency is deleted, the plugin automatically promotes another currency. Deleting the only currency is blocked.

**Currency fields on `Currency`:**
- `uuid` — random UUID assigned at creation, used as the stable key everywhere
- `name` — internal identifier
- `symbol`, `currencyNameSingular`, `currencyNamePlural` — display strings
- `color` — MiniMessage tag string e.g. `"<gold>"`
- `itemMaterial` — `Material` enum value, used as GUI icon and note item
- `defaultBalance` — given to new players on wallet creation
- `mobDropManager` — `MobDropManager` containing `List<MobDropEntry>`

### Currency deletion rules
Deletion is blocked (with a specific message per reason) if any of the following are true:
1. It is the **default currency** — admin must set a different currency as default first
2. It is the **only currency** — cannot leave the system with zero currencies
3. Any **player wallet** has a non-zero balance in this currency
4. Any **bank account** holds a non-zero balance in this currency
5. Any **outstanding notes** exist for this currency (unredeemed physical items)
6. Any **payroll** is configured to pay in this currency

These checks live in `CurrencyEditGUI`. The count helpers are on `WalletManager.countWalletsWithBalance()`, `BankManager.countAccountsWithBalance()`, `BankManager.countPayrollsUsing()`, and `NoteManager.countNotesByCurrency()`.

---

## Bank System

### BankManager
- Loaded at startup: loads all accounts, then for each account loads balances, members, payroll
- Tiers seeded on first run (3 defaults) if `loadAllTiers()` returns empty
- Payroll ticker: `runTaskTimerAsynchronously` every 600L ticks (30s), then `runTask` to sync — checks `payroll.isDue()` for each account
- `getAccountsForPlayer(UUID)` — returns all accounts where the player is a member

### BankRole permission methods
```java
canDeposit()      // OWNER, MANAGER, EMPLOYEE
canWithdraw()     // OWNER, MANAGER
canViewHistory()  // OWNER, MANAGER, VIEWER
canManageMembers()// OWNER, MANAGER
canManagePayroll()// OWNER only
canUpgrade()      // OWNER only
canDelete()       // OWNER only
```

### BankAccount balances
`Map<UUID, Double>` keyed by currency UUID. An account can hold multiple currencies simultaneously. Balance entries only exist once a deposit has been made — `getBalance(uuid)` returns 0.0 for missing keys.

### Payroll destination
`BankMember.payrollDestinationAccountUUID` — null means personal wallet. Employees set this themselves via `BankPayrollDestinationGUI`. If the destination account is deleted, the employee is skipped at payroll time and both owner and employee are notified.

### BankStorage save patterns
All saves use delete-then-insert. Example:
```java
SQLDataStore del = new SQLDataStore();
del.put("AccountUUID", uuid.toString());
accounts.delete(del);
SQLDataStore store = new SQLDataStore();
store.put("AccountUUID", uuid.toString());
// ... fill fields
accounts.save(store);
```

---

## VaultCore

Implements `net.milkbowl.vault.economy.Economy`. Reads/writes the default currency wallet only. `withdrawPlayer` and `depositPlayer` save the wallet immediately after mutation. `getCurrencyWallet(UUID)` looks up the player's wallet from `WalletManager` cache.

---

## MiniMessage / Text Formatting

All user-facing text uses `Translate.miniMessage(String)` which calls `MiniMessage.miniMessage().deserialize(input)`. Color codes use MiniMessage tags: `<red>`, `<gold>`, `<green>`, etc. Currency colors are stored as MiniMessage tag strings on `Currency`.

Do not use legacy `&` color codes in new code — some older code still has `Translate.color()` calls but those are in pre-existing code.

---

## Known Patterns & Gotchas

- **`BankAccountGUI` old handlers** — `handleDeposit` and `handleWithdraw` methods still exist but are unused (replaced by `BankDepositWithdrawGUI`). Marked `@SuppressWarnings("unused")`.
- **Members map includes owner** — `BankAccount.members` always contains the owner with `BankRole.OWNER`. GUIs that list non-owner members must filter: `member.getRole() == BankRole.OWNER`.
- **Payroll skips OWNER and VIEWER** — only MANAGER and EMPLOYEE receive wages.
- **`PayrollDestination` stored as empty string** in DB, not NULL — `"".isEmpty()` check used on load to set `null`.
- **WSL filesystem I/O errors** — occasional `java.io.IOException: Input/output error` from Gradle daemon on WSL. Run `./gradlew --stop` then retry.
- **Tab completion** — `eco`, `bank`, and `bal` (via existing Bukkit behavior) have tab completion. `withdraw` does not.
- **`BankAccountGUI` slot layout (54 slots):**
  - 10–16: balance displays per currency
  - 19: deposit, 21: withdraw, 23: transaction history, 25: members
  - 37: payroll, 39: upgrade, 41: rename, 43: delete
  - 45: transfer, 47: payroll destination (employee/manager only)
  - 49: back
