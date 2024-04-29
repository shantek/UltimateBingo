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
import org.bukkit.plugin.java.JavaPlugin;
import io.shantek.managers.CardTypes;


public final class UltimateBingo extends JavaPlugin {
    public BingoManager bingoManager;
    private MaterialList materialList;
    public BingoFunctions bingoFunctions;
    public Location bingoSpawnLocation;
    public String cardSize;
    public CardTypes cardTypes;
    public boolean fullCard = false;

    @Override
    public void onEnable() {
        // Initialize instances
        materialList = new MaterialList(this);
        bingoFunctions = new BingoFunctions(this);
        cardTypes = new CardTypes(this);
        SettingsManager settingsManager = new SettingsManager(this);
        RespawnListener respawnListener = new RespawnListener(this);
        BingoCraftListener bingoCraftListener = new BingoCraftListener(this);
        BingoFurnaceRemoveListener bingoFurnaceRemoveListener = new BingoFurnaceRemoveListener(this);
        BingoInteractListener bingoStickListener = new BingoInteractListener(this);
        BingoInventoryOpenListener bingoInventoryOpenListener = new BingoInventoryOpenListener(this);
        SettingsListener settingsListener = new SettingsListener(materialList, settingsManager);

        // Initialize bingoManager after other dependencies
        bingoManager = new BingoManager(this, new BingoCommand(this, settingsManager, bingoManager));

        // Register events
        getCommand("bingo").setExecutor(new BingoCommand(this, settingsManager, bingoManager));
        getCommand("bingo").setTabCompleter(new BingoCompleter());
        Bukkit.getPluginManager().registerEvents(bingoCraftListener, this);
        Bukkit.getPluginManager().registerEvents(new BingoPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(bingoInventoryOpenListener, this);
        Bukkit.getPluginManager().registerEvents(settingsListener, this);
        Bukkit.getPluginManager().registerEvents(respawnListener, this);
        Bukkit.getPluginManager().registerEvents(bingoStickListener, this);
        Bukkit.getPluginManager().registerEvents(bingoFurnaceRemoveListener, this);
        materialList.createMaterials();
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
}
