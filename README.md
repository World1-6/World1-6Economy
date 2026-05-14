# World1-6Economy

A multi-currency economy plugin for Paper servers. Supports multiple currencies, physical currency notes, mob drop rewards, Vault integration, and a full bank account system with payroll, tiers, and transaction history.

## Requirements

- Paper 1.19+
- [Vault](https://github.com/MilkBowl/Vault/releases)
- [World1-6Utils](https://github.com/World1-6/World1-6Utils)

## Commands

| Command | Description | Permission |
|---|---|---|
| `/bal` | Open your wallet GUI | `world16.bal` |
| `/bal <player>` | View another player's wallet | `world16.bal.other` |
| `/money` | Alias for `/bal` | `world16.bal` |
| `/withdraw <amount> [currency]` | Convert wallet balance into a physical note item | `world16.withdraw` |
| `/eco` | Open the currency admin GUI | `world16.eco` |
| `/eco give <player> <amount> [currency]` | Give a player currency | `world16.eco.give` |
| `/eco take <player> <amount> [currency]` | Remove currency from a player | `world16.eco.take` |
| `/eco set <player> <amount> [currency]` | Set a player's balance | `world16.eco.set` |
| `/eco reset <player> [currency]` | Zero out a player's balance | `world16.eco.reset` |
| `/bank` | Open your bank account list | `world16.bank` |
| `/bank <account name>` | Jump directly to a bank account | `world16.bank` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `world16.bal` | Use `/bal` | true |
| `world16.bal.other` | View other players' wallets | true |
| `world16.withdraw` | Withdraw currency as a note item | true |
| `world16.bank` | Access bank accounts | true |
| `world16.eco` | Open the currency admin GUI | op |
| `world16.eco.give` | Give currency to players | op |
| `world16.eco.take` | Take currency from players | op |
| `world16.eco.set` | Set player balances | op |
| `world16.eco.reset` | Reset player balances | op |
| `world16.eco.op` | All eco admin permissions | op |

## Features

### Wallet
Every player automatically has a personal wallet that holds balances for each currency. The wallet is what Vault reads from. Players can view their balances with `/bal` and withdraw currency as a physical note item using `/withdraw`.

### Physical Currency Notes
`/withdraw <amount> [currency]` converts wallet balance into a physical item. Right-clicking the item redeems it back to the wallet. Notes are dupe-proof — each note has a unique ID stored in the database. The redemption amount is always read server-side, never from the item itself.

### Currencies
Admins can create and manage multiple currencies via `/eco`. Each currency has:
- Name, symbol, singular/plural display names
- Color (for chat and GUI formatting)
- Icon material (the item used in GUIs and note drops)
- Default balance for new players
- Configurable mob drops
- One currency is marked as the **default** — this is what Vault uses

### Mob Drops
Each currency can have mob drop entries configured via `/eco` → select currency → Mob Drops. Each entry has a min/max amount, drop chance, and optionally a list of entity types. Drop mode options:
- **Wallet** — credits the killer's wallet directly with an action bar notification
- **Note** — drops a physical note item at the mob's location

### Bank Accounts
Players open `/bank` to create and manage bank accounts. Two types:
- **Personal** — single owner
- **Business** — supports multiple members with roles

#### Bank Roles
| Role | Deposit | Withdraw | View History | Manage Members | Payroll | Upgrade | Delete |
|---|---|---|---|---|---|---|---|
| Owner | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Manager | ✓ | ✓ | ✓ | ✓ | | | |
| Employee | ✓ | | | | | | |
| Viewer | | | ✓ | | | | |

#### Bank Tiers
Accounts start at Tier 1 and can be upgraded. Default tiers:

| Tier | Cost | Balance Limit | Transaction Limit |
|---|---|---|---|
| 1 — Basic | $500 | $10,000 | $1,000 |
| 2 — Standard | $2,000 | $100,000 | $10,000 |
| 3 — Premium | $10,000 | Unlimited | Unlimited |

#### Payroll (Business only)
Owners can schedule automatic payroll that runs at a real-time interval. Each employee has:
- A configurable wage
- A payroll destination (personal wallet or any bank account they belong to — set by the employee themselves)

Payroll options: interval, currency, online-only mode, run now.

#### Transfers
Funds can be transferred directly between bank accounts without touching the wallet.

#### Transaction History
All deposits, withdrawals, transfers, and payroll events are recorded and viewable in a paginated GUI.

## Storage

| Data | Format | Location |
|---|---|---|
| Currencies | YAML | `plugins/World1-6Economy/currencies.yml` |
| Wallets | YAML | `plugins/World1-6Economy/wallets.yml` |
| Currency notes | SQLite | `plugins/World1-6Economy/CurrencyNotes.db` |
| Bank accounts | SQLite | `plugins/World1-6Economy/BankAccounts.db` |

## Vault

World1-6Economy registers as a Vault economy provider. The default currency is used for all Vault operations. Other plugins that use Vault (shops, jobs, etc.) interact with each player's default currency wallet balance.
