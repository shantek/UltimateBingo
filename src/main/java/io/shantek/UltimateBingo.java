// This project is based on Mega Bingo by Elmer Lion
// You can find the original project here https://github.com/ElmerLion/megabingo

// Distributed under the GNU General Public License v3.0

package io.shantek;

import io.shantek.listeners.*;
import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import io.shantek.tools.MaterialList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateBingo extends JavaPlugin {
    public BingoManager bingoManager;
    private MaterialList materialList;


    @Override
    public void onEnable() {


        materialList = new MaterialList(this);

        SettingsManager settingsManager = new SettingsManager(this);

        BingoCraftListener bingoCraftListener = new BingoCraftListener(this);
        BingoPickupListener bingoPickupListener = new BingoPickupListener(this);
        BingoInventoryOpenListener bingoInventoryOpenListener = new BingoInventoryOpenListener(this);
        SettingsListener settingsListener = new SettingsListener(materialList, settingsManager);

        bingoManager = new BingoManager(this, new BingoCommand(this, settingsManager, bingoManager));

        getCommand("bingo").setExecutor(new BingoCommand(this, settingsManager, bingoManager));
        getCommand("bingo").setTabCompleter(new BingoCompleter());

        Bukkit.getPluginManager().registerEvents(bingoCraftListener, this);
        Bukkit.getPluginManager().registerEvents(bingoPickupListener, this);
        Bukkit.getPluginManager().registerEvents(new BingoGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(bingoInventoryOpenListener, this);
        Bukkit.getPluginManager().registerEvents(settingsListener, this);

        materialList.createMaterials();

    }


    public BingoManager getBingoManager() {
        return bingoManager;
    }
    public MaterialList getMaterialList(){
        return materialList;
    }

    @Override
    public void onDisable() {
        bingoManager.clearData();
        bingoManager.started = false;
    }
}
