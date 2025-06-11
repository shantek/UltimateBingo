# ![Shantek Banner](/.github/assets/Banner.png)

---


[![Modrinth](https://img.shields.io/badge/Modrinth-Download-green?logo=modrinth)](https://modrinth.com/plugin/ultimatebingo)
[![Discord](https://img.shields.io/discord/628396916639793152.svg?color=%237289da&label=discord)](https://shantek.co/discord)
[![CodeFactor](https://www.codefactor.io/repository/github/shantek/ultimatebingo/badge)](https://www.codefactor.io/repository/github/shantek/ultimatebingo)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/shantek/UltimateBingo)
![GitHub Created At](https://img.shields.io/github/created-at/shantek/UltimateBingo)



<img src="https://cdn.modrinth.com/data/cached_images/84531613476ecfe43f1395c2bc048ad116089561.png" alt="Ultimate Bingo Banner" />

# 🎯 Ultimate Bingo

**The ultimate Minecraft bingo experience for Bukkit servers!**  
Configure endless combinations of bingo games with full control over difficulty, size, mode, win conditions, and more — all from a sleek GUI or interactive signs.

> ⚠️ Looking for the latest builds? [Get dev builds here →](https://shantek.dev/job/UltimateBingo/)

---

## 🧩 Game Modes

| Mode | Description |
|------|-------------|
| **Speed Run** | Restore health, hunger, and rockets on each item ticked. |
| **Traditional** | Classic bingo with no special effects. |
| **Brew Dash** | Apply negative potion effects to opponents on each tick. |
| **Teams** | Up to 3 teams (yellow, red, blue) with shared cards. |
| **Group** | Everyone shares one card and works together. |
| **Random** | Let the plugin pick a mode for you. |

---

## 🎲 Card Options

### Difficulty & Size
Choose from Easy, Normal, or Hard difficulties and 3x3, 4x4, or 5x5 card sizes — or let the plugin randomize them.

**Card item distribution:**
- Easy: 15 easy, 15 normal
- Medium: 5 easy, 10 normal, 10 hard, 5 extreme
- Hard: 5 normal, 10 hard, 10 extreme, 5 impossible

### Card Type
- **Unique:** Everyone gets their own randomized card
- **Identical:** All players get the same card
- **Random:** Chooses either unique or identical

### Win Condition
- **Full Card:** Complete every item
- **Single Row:** Vertical, horizontal, or diagonal line
- **Random:** You guessed it!

---

## 🔍 Reveal Cards

Toggle between showing what item was ticked off or keeping it a mystery. With reveal mode enabled, players can view each other's cards via a GUI.

---

## ⏱️ Time Limits

Choose from:
- Timed games (5–60 minutes)
- Unlimited mode with automatic speed boosts at 20, 40, and 60 minutes

---

## 🎒 Loadouts

| Kit | Description |
|-----|-------------|
| **Naked Kit** | Empty inventory for raw survival challenge. |
| **Starter Kit** | Wooden tools and basic items. |
| **Boat Kit** | Iron gear, boat, bed, shield — ready to explore. |
| **Flying Kit** | Max gear + Elytra and rockets (replenish in Speed Run mode). |

---

## ⚙️ Setup with Signs & Buttons

Use `/bingo set` while looking at a sign or button to configure it. Place 8 signs and 1 button for full in-world control.

| Type | Function |
|------|----------|
| `CardSize` | Toggle 3x3 / 4x4 / 5x5 |
| `CardType` | Unique / Identical |
| `Difficulty` | Easy / Normal / Hard |
| `GameMode` | Switches game mode |
| `Loadout` | Select starting kit |
| `RevealCards` | Toggle item reveal |
| `TimeLimit` | Set timer duration |
| `WinCondition` | Row / Full Card |
| `StartButton` | Begins the game |

> **Note:** Use `/bingo remove <SignType>` to remove signs. Case-sensitive!

---

## 🧭 Commands

| Command | Description |
|---------|-------------|
| `/bingo` | Opens GUI for players to join or get a card. |
| `/bingo gui` | Opens setup GUI for admins. |
| `/bingo reload` | Reloads config file. |
| `/bingo info` | Shows current game configuration. |
| `/bingo set <type>` | Sets a game config sign/button. |
| `/bingo remove <type>` | Removes a config sign/button. |

---

## 🔐 Permissions

| Node | Description |
|------|-------------|
| `shantek.ultimatebingo.start` | Start/configure bingo games. |
| `shantek.ultimatebingo.stop` | Stop active games. |
| `shantek.ultimatebingo.settings` | Add/remove bingo items. |
| `shantek.ultimatebingo.signs` | Use signs and buttons to configure games. |

---

## 🏆 Leaderboard & PlaceholderAPI

Easily create leaderboard holograms using these placeholders:

| Placeholder | Output |
|-------------|--------|
| `%ultimatebingo_overall_1_name%` | Name of 1st place |
| `%ultimatebingo_overall_1_score%` | Score of 1st place |

> Replace `1` with any rank (1–10).

---

## 🌐 External Links

- 💬 [Join Discord](https://shantek.co/discord)
- 🛠️ [GitHub Repo](https://github.com/shantek/UltimateBingo)
- ✍️ [Bug Reports & Suggestions](https://github.com/shantek/UltimateBingo/issues)
- ❤️ [Support on Patreon](https://shantek.co/patreon)
- ☕ [Support via PayPal](https://shantek.co/bingo-donate)

---

## 📄 License

[![License: GPL](https://img.shields.io/badge/license-GPL-blue.svg)](LICENSE)

Distributed under the **GNU General Public License v3.0**  
_This project is based on [Mega Bingo by Elmer Lion](https://github.com/ElmerLion/megabingo)_

---

![Plugin Usage Stats](https://bstats.org/signatures/bukkit/Ultimate%20Bingo.svg)