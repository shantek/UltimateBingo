

[![License: GPL](https://img.shields.io/badge/license-GPL-blue.svg)](LICENSE)
[![Discord](https://img.shields.io/discord/628396916639793152.svg?color=%237289da&label=discord)](https://shantek.co/discord) [![CodeFactor](https://www.codefactor.io/repository/github/shantek/ultimatebingo/badge)](https://www.codefactor.io/repository/github/shantek/ultimatebingo)

![Ultimate Bingo](https://cdn.modrinth.com/data/cached_images/84531613476ecfe43f1395c2bc048ad116089561.png)

#### Looking for the latest dev builds? You can find them [here!](https://shantek.dev/job/UltimateBingo/)



![Bingo Configuration GUI](https://cdn.modrinth.com/data/cached_images/8f401dc509c050112644e16cfc325969f94441e0.png)

Ultimate Bingo offers endless entertainment for your Minecraft community with a whole variety of game configuration on offer to encourage exploration and test your survival skills.
With an easy-to-use GUI or in game config signs, setting up and starting games a breeze for your players! Players can choose from the following options:

## Game Modes
>#### Speed Run
>Every time you tick off an item on your card, your hunger, health, saturation and rockets (if the flying kit is enabled) are restored allowing you to continue speed running your bingo card.

>#### Traditional
>Old school bingo, tick off items to complete the game. You need to watch your hunger and health in this mode!

>#### Brew Dash
>Every time you tick an item off of your bingo card, your opponents are dealt a negative effect. This can range from things like hunger, poison, slow falling etc. The duration of this effect depends on the card difficulty; 20 seconds for each, 40 seconds for normal and 60 seconds for hard. Clear your negative effect by ticking off an item.

>#### Teams
> Split your players in up to 3 teams; yellow, red and blue. Each team has a shared bingo card and races to beat the other teams. Players can select their desired team by standing on the matching colour wool - set up your bingo spawn to offer these colour wool blocks for players to stand on to choose their team. Anyone not on those colour blocks will be randomly assigned a team colour.

>#### Group
> All players share a single bingo card and work together to tick off items. How quick can your group get bingo?

>#### Random
>Can't decide what to play? Why not use the random mode to let the plugin choose for you.

## Card Difficulty & Size
Easy, Normal or Hard modes change the distribution used to generate your bingo cards. Choose between a Small (3x3), Medium (4x4) or Large (5x5) card to battle out your epic bingo games!

Both difficulty and card size also come with a Random option to help mix up your bingo games.

Item difficulty is split into 5 categories: Easy, normal, hard, extreme and impossible. When selecting the card difficulty, this determines how many items are pulled from each group. The plugin currently pulls the following:

Easy card: 15 easy, 15 normal Medium card: 5 easy, 10 normal, 10 hard, 5 extreme Hard card: 5 normal, 10 hard, 10 extreme, 5 impossible

The items are then shuffled and then the items are taken in order to fill up a card.

## Card Type
>#### Unique Card
>All players are given a unique card. The plugin pulls a list of items slightly larger than the selected card size, shuffles them and generates a unique card for each player. This is done to ensure players have similar cards but the order is completley different for every player.

>#### Identical Card
>All players receive the exact same card. No excuses for losing in this mode!

>#### Random
>As with the other settings, you can also roll with a random card type. Seeing a bit of a trend here?

## Win Condition
With single row and full card options, the battles are sure to be epic. And you guessed it, this also comes with a random mode. When playing single row, this can be in a horizontal, vertical or diagonal line.

## Reveal Cards
>With this mode enabled, every time a player ticks an item off all the other players are told what the item is. Without it enabled, it uses a more cryptic "ticked off an item" message instead. Enabling this will also allow players to open their bingo card, click the spyglass and see the bingo card of anyone playing the current bingo game.

## Time Limit
>Want to ensure you have some insane and fast fun? Why not try a timed game. Select from 5, 10, 15, 20, 30, 40, 50 or 60 minute games. If no one completes the win condition in this time, the game simply ends. Or why not try the Unlimited Time games, where players receive a speed boost after 20, 40 and 60 minutes of game play.

## Player Load-out
Game kits help make your games more epic and cater to a wide variety of players. Select a kit to equip to all players at the start of the game.

>#### Naked Kit
>![Naked Kit](https://cdn.modrinth.com/data/cached_images/ee2fb553c2bd1e69c3709b590bef07f333c4c67e.png)
>This kit as the name implies gives your players an entirely empty inventory. Perfect for those players who enjoy punching that first tree and want to grind their gear (and bingo card).

>#### Starter Kit
>![Starter Kit](https://cdn.modrinth.com/data/cached_images/c63250f69bef34e6fdbfe0e576b6e44caf02f6a9.png)
>Not a fan of punching trees? This kit will give your players the essentials to get up and running a little quicker - although, they're all wood. Yuck.

>#### Boat Kit
>![Boat Kit](https://cdn.modrinth.com/data/cached_images/e2dfbda6a22366838c9d78638257e26c9e7d386e.png)
>A little more high end, equipped with low level enchanted iron gear, shield, boat, bed and crafting cupboard - you're ready to jump straight in to exploring!

>#### Flying Kit
>![Flying Kit](https://cdn.modrinth.com/data/cached_images/a03074d612937330316294f03f507336f4745c25.png)
>The ultimate gear for those speed runs. Max enchanted Netherite gear, elytra and level 3 rockets - How fast can you get bingo? When using the flight kit in speed run mode, your rockets are topped back up to a full stack every time you tick an item off your card.

## Installing and using the plugin

As the team mode uses yellow, red and blue wool blocks - It's recommended to place large sections of each team colour on the ground to allow your players to easily select their team colour at the start of the game.

The game also comes with the ability to configure and start games using signs and a button. This needs to be set up by an OP. To allow a game to be fully configurable, please down 8 signs and a button - these can be any signs and any button.
While looking at the sign, use the **/bingo set** command to set each sign, followed by the sign type. For example, to set up a game mode sign, look at a sign and type **/bingo set GameMode**. You can configure the following signs and buttons:

Please note: If you want to allow your players to configure games using the signs and button, be sure to give them the shantek.ultimatebingo.signs permission node.

>**CardSize:**
>Toggles between small, medium and large cards

>**CardType:**
>Toggles between unique or identical cards

>**Difficulty:**
>Toggles between easy, normal or hard difficulty

>**GameMode**:
>Toggles between the included game modes

>**Loadout:**
>Changes the initial kit loadout for players

>**RevealCards:**
>Toggles items being revealed as they're ticked off

>**TimeLimit:**
>Sets the time limit for the game

>**WinCondition:**
>Toggles between full card or single row

>**StartButton:**
>Pressing this button will start a game with the selected settings

Want to remove or relocate a sign? You can do so by using the /bingo remove command, followed by the name of the sign/button to remove. Please note, these are case sensitive.

## Commands

>**/bingo** - GUI for all players to join an existing game or get a replacement card.

>**/bingo gui** - Used by an admin/mod to set up and start a new bingo game.

>**/bingo reload** - Used by an op to reload the config file.

>**/bingo info** - Accessible to all players. Gives you a rundown of the current bingo config.

>**/bingo set** * - Used to activate a game config sign/button in your world.

>**/bingo remove** * - Used to remove a game config sign/button in your world.

## Permissions

>**shantek.ultimatebingo.start** - Allows players to configure and start Bingo games.

>**shantek.ultimatebingo.stop** - Grant access to stop an active bingo game.

>**shantek.ultimatebingo.settings** - Ability to add/remove items from the bingo card config

>**shantek.ultimatebingo.signs** - Allow players to interact with signs and buttons to configure and start games


## External Links

>[Support via Patreon](https://shantek.co/patreon)

>[Support via PayPal](https://shantek.co/bingo-donate)

>[Discord](https://shantek.co/discord)

>[GitHub Source Code](https://github.com/shantek/UltimateBingo)

>[Report bugs/make suggestions](https://github.com/shantek/UltimateBingo/issues)




## License
Distributed under the GNU General Public License v3.0.

_This project is based on [Mega Bingo by Elmer Lion](https://github.com/ElmerLion/megabingo)_