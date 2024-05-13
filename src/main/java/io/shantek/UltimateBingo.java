// This project is based on Mega Bingo by Elmer Lion
// You can find the original project here https://github.com/ElmerLion/megabingo

// Distributed under the GNU General Public License v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import io.shantek.managers.ConfigFile;
import io.shantek.managers.BingoGameGUIManager;
import io.shantek.managers.BingoPlayerGUIManager;
import io.shantek.tools.MaterialList;
import io.shantek.tools.BingoFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.shantek.managers.CardTypes;

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


    // Saved config for setting up games
    public String fullCard = "full card";
    public String difficulty;
    public String cardSize;
    public String uniqueCard;
    public String gameMode = "traditional";
    public String revealCards = "enabled";
    public int loadoutType = 1;

    // Current game configuration - Implemented to allow
    // random assignment of game setup
    public boolean currentFullCard = false;
    public String currentDifficulty;
    public String currentCardSize;
    public boolean currentUniqueCard;
    public String currentGameMode = "traditional";
    public boolean currentRevealCards = true;
    public int currentLoadoutType = 1;


    // Very important this is never set to an item you have included in your bingo cards
    // as this will break the functionality of your game!
    public Material tickedItemMaterial = Material.LIME_CONCRETE;

    public static UltimateBingo instance;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        SettingsManager settingsManager = new SettingsManager(this);
        bingoManager = new BingoManager(this, new BingoCommand(this, settingsManager, bingoManager));
        bingoCommand = new BingoCommand(this, settingsManager, bingoManager);
        materialList = new MaterialList(this);
        bingoGameGUIManager = new BingoGameGUIManager(this);
        bingoPlayerGUIManager = new BingoPlayerGUIManager(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);
        configFile = new ConfigFile(this);

        getCommand("bingo").setExecutor(new BingoCommand(this, settingsManager, bingoManager));
        getCommand("bingo").setTabCompleter(new BingoCompleter());

        // Register event listeners
        registerEventListeners();
        materialList.createMaterials();

        // Check if the data folder already exists, create if it doesn't
        configFile.checkforDataFolder();

        // Load the game configuration
        configFile.reloadConfigFile();
    }

    private void registerEventListeners() {
        // Register each listener with the Bukkit plugin manager
        Bukkit.getPluginManager().registerEvents(new RespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(this), this);
        SettingsManager settingsManager = new SettingsManager(this);
        Bukkit.getPluginManager().registerEvents(new SettingsListener(materialList, settingsManager, bingoGameGUIManager, this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPlayerGUIListener(materialList, bingoPlayerGUIManager, this), this);
    }

    public BingoManager getBingoManager() {
        return bingoManager;
    }

    public MaterialList getMaterialList(){
        return materialList;
    }

    public BingoFunctions getBingoFunctions(){
        return bingoFunctions;
    }

    @Override
    public void onDisable() {
        bingoManager.clearData();
        bingoManager.started = false;
        instance = null;
    }

    public static UltimateBingo getInstance() {
        return instance;
    }

}
