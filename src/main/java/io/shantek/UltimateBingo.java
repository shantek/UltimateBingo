// This project is based on Mega Bingo by Elmer Lion
// You can find the original project here https://github.com/ElmerLion/megabingo

// Distributed under the GNU General Public License v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import io.shantek.managers.ConfigFile;
import io.shantek.tools.MaterialList;
import io.shantek.tools.BingoFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.shantek.managers.CardTypes;

public final class UltimateBingo extends JavaPlugin {
    public BingoManager bingoManager;
    private MaterialList materialList;
    public BingoFunctions bingoFunctions;
    public BingoCommand bingoCommand;
    public Location bingoSpawnLocation;
    public ConfigFile configFile;

    private YamlConfiguration gameConfig;
    public CardTypes cardTypes;
    public boolean fullCard = false;
    public String difficulty;
    public String cardSize;
    public boolean uniqueCard;
    public boolean consoleLogs = true;
    public boolean bingoCardActive = false;
    public String gameMode = "traditional";
    public boolean respawnTeleport = true;

    public static UltimateBingo instance;

    @Override
    public void onEnable() {
        // Save the instance of the plugin
        instance = this;

        SettingsManager settingsManager = new SettingsManager(this);
        bingoManager = new BingoManager(this, new BingoCommand(this, settingsManager, bingoManager));
        bingoCommand = new BingoCommand(this, settingsManager, bingoManager);
        materialList = new MaterialList(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);
        configFile = new ConfigFile(this);

        RespawnListener respawnListener = new RespawnListener(this);
        BingoCraftListener bingoCraftListener = new BingoCraftListener(this);
        BingoPickupListener bingoPickupListener = new BingoPickupListener(this);
        BingoInteractListener bingoStickListener = new BingoInteractListener(this);
        BingoInventoryOpenListener bingoInventoryOpenListener = new BingoInventoryOpenListener(this);
        BingoInventoryCloseListener bingoInventoryCloseListener = new BingoInventoryCloseListener(this);
        SettingsListener settingsListener = new SettingsListener(materialList, settingsManager);

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
        Bukkit.getPluginManager().registerEvents(new BingoCraftListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInventoryOpenListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoInventoryCloseListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(), this); // Assuming this doesn't need 'this'

        // You mentioned a SettingsListener which seems to need a new SettingsManager instance each time
        SettingsManager settingsManager = new SettingsManager(this);
        Bukkit.getPluginManager().registerEvents(new SettingsListener(materialList, settingsManager), this);
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
