// This project is based on Mega Bingo by Elmer Lion
// You can find the original project here https://github.com/ElmerLion/megabingo

// Distributed under the GNU General Public License v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import io.shantek.tools.MaterialList;
import io.shantek.tools.BingoFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private YamlConfiguration gameConfig;
    private File gameConfigFile;

    public CardTypes cardTypes;
    public boolean fullCard = false;
    public String difficulty;
    public String cardSize;
    public boolean uniqueCard;

    @Override
    public void onEnable() {

        SettingsManager settingsManager = new SettingsManager(this);
        bingoManager = new BingoManager(this, new BingoCommand(this, settingsManager, bingoManager));
        bingoCommand = new BingoCommand(this, settingsManager, bingoManager);
        materialList = new MaterialList(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);

        RespawnListener respawnListener = new RespawnListener(this);
        BingoCraftListener bingoCraftListener = new BingoCraftListener(this);
        BingoPickupListener bingoPickupListener = new BingoPickupListener(this);
        BingoInteractListener bingoStickListener = new BingoInteractListener(this);
        BingoInventoryOpenListener bingoInventoryOpenListener = new BingoInventoryOpenListener(this);
        SettingsListener settingsListener = new SettingsListener(materialList, settingsManager);


        getCommand("bingo").setExecutor(new BingoCommand(this, settingsManager, bingoManager));
        getCommand("bingo").setTabCompleter(new BingoCompleter());

        Bukkit.getPluginManager().registerEvents(bingoCraftListener, this);
        Bukkit.getPluginManager().registerEvents(bingoPickupListener, this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(bingoInventoryOpenListener, this);
        Bukkit.getPluginManager().registerEvents(settingsListener, this);
        Bukkit.getPluginManager().registerEvents(respawnListener, this);
        Bukkit.getPluginManager().registerEvents(bingoStickListener, this);
        materialList.createMaterials();

        // Load the game configuration
        loadGameConfig();

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
    }

    public void loadGameConfig() {
        // Create or load the GameConfig.yml file in the plugin's data folder
        gameConfigFile = new File(getDataFolder(), "GameConfig.yml");
        if (!gameConfigFile.exists()) {
            saveResource("GameConfig.yml", false);
        }

        // Load the YAML file into memory
        gameConfig = YamlConfiguration.loadConfiguration(gameConfigFile);

        // Load the values from the configuration
        fullCard = gameConfig.getBoolean("full-card", false);
        uniqueCard = gameConfig.getBoolean("unique-card", false);
        difficulty = gameConfig.getString("difficulty", "easy");
        cardSize = gameConfig.getString("card-size", "medium");
    }

    public void saveGameConfig() {
        // Set the values in the YAML configuration object
        gameConfig.set("full-card", fullCard);
        gameConfig.set("difficulty", difficulty);
        gameConfig.set("card-size", cardSize);
        gameConfig.set("unique-card", uniqueCard);

        // Save the YAML configuration to the file
        try {
            gameConfig.save(gameConfigFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save GameConfig.yml", e);
        }
    }

}