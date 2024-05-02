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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import io.shantek.managers.CardTypes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


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

        Bukkit.getPluginManager().registerEvents(bingoCraftListener, this);
        Bukkit.getPluginManager().registerEvents(bingoPickupListener, this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(bingoInventoryOpenListener, this);
        Bukkit.getPluginManager().registerEvents(bingoInventoryCloseListener, this);
        Bukkit.getPluginManager().registerEvents(settingsListener, this);
        Bukkit.getPluginManager().registerEvents(respawnListener, this);
        Bukkit.getPluginManager().registerEvents(bingoStickListener, this);
        materialList.createMaterials();

        // Check if the data folder already exists, create if it doesn't
        configFile.checkforDataFolder();

        // Load the game configuration
        configFile.reloadConfigFile();
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