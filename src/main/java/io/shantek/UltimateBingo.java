// This project is based on Mega Bingo by Elmer Lion
// You can find the original project here https://github.com/ElmerLion/megabingo

// Distributed under the GNU General Public License v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.*;
import io.shantek.tools.MaterialList;
import io.shantek.tools.BingoFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UltimateBingo extends JavaPlugin {
    public BingoManager bingoManager;
    private MaterialList materialList;
    public BingoFunctions bingoFunctions;
    public BingoGameGUIManager bingoGameGUIManager;
    public BingoPlayerGUIManager bingoPlayerGUIManager;
    public BingoPlayerGUIListener bingoPlayerGUIListener;
    public BingoCommand bingoCommand;
    public Location bingoSpawnLocation;
    public ConfigFile configFile;
    public int gameTime = 0;
    private YamlConfiguration gameConfig;
    public CardTypes cardTypes;
    public boolean consoleLogs = true;
    public boolean bingoCardActive = false;
    public boolean respawnTeleport = true;
    public boolean bingoStarted = false;
    public Material bingoCardMaterial = Material.COMPASS;
    public long gameStartTime;
    public boolean playedSinceReboot = false;
    public Metrics metrics;

    private SettingsManager settingsManager;
    public InGameConfigManager inGameConfigManager;

    // Add Leaderboard field
    private Leaderboard leaderboard;
    // Saved config for setting up games
    public String fullCard = "full card";
    public String difficulty;
    public String cardSize;
    public String uniqueCard;
    public String gameMode = "traditional";
    public String revealCards = "enabled";
    public int loadoutType = 1;
    public String bingoWorld = "default";
    public boolean multiWorldServer = false;
    public boolean countSoloGames = false;

    // Current game configuration - Implemented to allow
    // random assignment of game setup
    public boolean currentFullCard = false;
    public String currentDifficulty;
    public String currentCardSize;
    public boolean currentUniqueCard;
    public String currentGameMode = "traditional";
    public boolean currentRevealCards = true;
    public int currentLoadoutType = 1;

    public boolean bingoButtonActive = true;

    // Inventory used for group game mode
    public Inventory groupInventory;

    // Inventories used for teams mode
    public Inventory blueTeamInventory;
    public Inventory redTeamInventory;
    public Inventory yellowTeamInventory;


    // Very important this is never set to an item you have included in your bingo cards
    // as this will break the functionality of your game!
    public Material tickedItemMaterial = Material.LIME_CONCRETE;

    public static UltimateBingo instance;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        // Initialize managers in the correct order
        settingsManager = new SettingsManager(this);

        // Initialize BingoManager first without BingoCommand
        bingoManager = new BingoManager(this, null); // Temporarily set null for BingoCommand

        // Now initialize BingoCommand and pass the actual bingoManager reference
        bingoCommand = new BingoCommand(this, settingsManager, bingoManager, inGameConfigManager);

        // Set the BingoCommand reference in BingoManager
        bingoManager.setBingoCommand(bingoCommand);

        // Continue with other managers
        materialList = new MaterialList(this);
        bingoGameGUIManager = new BingoGameGUIManager(this);
        bingoPlayerGUIManager = new BingoPlayerGUIManager(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);
        configFile = new ConfigFile(this);
        leaderboard = new Leaderboard(this);
        inGameConfigManager = new InGameConfigManager(this);

        // Register commands
        getCommand("bingo").setExecutor(bingoCommand);
        getCommand("bingo").setTabCompleter(new BingoCompleter());

        // Check if PlaceholderAPI is installed and register placeholders
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BingoPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI detected, registering placeholders.");
        } else {
            getLogger().info("PlaceholderAPI not found, skipping placeholder registration.");
        }

        registerEventListeners();

        // Ensure game settings exist
        configFile.checkforDataFolder();
        configFile.reloadConfigFile();

        // Register bStats
        int pluginId = 21982;
        Metrics metrics = new Metrics(this, pluginId);

        // Set signs to the correct values
        bingoFunctions.updateAllSigns();
    }

    private void registerEventListeners() {
        // Register each listener with the Bukkit plugin manager
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(this), this);
        SettingsManager settingsManager = new SettingsManager(this);
        Bukkit.getPluginManager().registerEvents(new SettingsListener(materialList, settingsManager, bingoGameGUIManager, this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerGUIListener(materialList, bingoPlayerGUIManager, this), this);
        Bukkit.getPluginManager().registerEvents(new BingoSignListener(this, inGameConfigManager), this);
    }

    public BingoManager getBingoManager() {
        return bingoManager;
    }

    public MaterialList getMaterialList(){
        return materialList;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public BingoFunctions getBingoFunctions(){
        return bingoFunctions;
    }

    @Override
    public void onDisable() {
        if (bingoManager != null) {
            bingoManager.clearData();
        }
        bingoStarted = false;
        instance = null;
    }

    public static UltimateBingo getInstance() {
        return instance;
    }

}
