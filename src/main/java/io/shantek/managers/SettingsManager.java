package io.shantek.managers;

import io.shantek.UltimateBingo;
import io.shantek.tools.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SettingsManager {
    UltimateBingo ultimateBingo;

    public SettingsManager(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    public Inventory createSettingsGUI(Player player){
        Inventory settingsGUI = Bukkit.createInventory(player, 9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo Settings");

        ItemStack easy = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS)
                .withDisplayName(ChatColor.AQUA + "Add/Remove Easy Materials").build();

        ItemStack normal = new ItemBuilder(Material.GREEN_STAINED_GLASS)
                .withDisplayName(ChatColor.GREEN + "Add/Remove Normal Materials").build();

        ItemStack hard = new ItemBuilder(Material.YELLOW_STAINED_GLASS)
                .withDisplayName(ChatColor.YELLOW + "Add/Remove Hard Materials").build();

        ItemStack extreme = new ItemBuilder(Material.ORANGE_STAINED_GLASS)
                .withDisplayName(ChatColor.GOLD + "Add/Remove Extreme Materials").build();

        ItemStack impossible = new ItemBuilder(Material.RED_STAINED_GLASS)
                .withDisplayName(ChatColor.RED + "Add/Remove Impossible Materials").build();

        settingsGUI.setItem(2, easy);
        settingsGUI.setItem(3, normal);
        settingsGUI.setItem(4, hard);
        settingsGUI.setItem(5, extreme);
        settingsGUI.setItem(6, impossible);

        return settingsGUI;
    }

    public String getDifficultyDisplay(int difficulty){
        if (difficulty == 1){
            return ChatColor.AQUA + "Add/Remove Easy Materials";
        }

        if (difficulty == 2){
            return ChatColor.GREEN + "Add/Remove Normal Materials";
        }

        if (difficulty == 3){
            return ChatColor.YELLOW + "Add/Remove Hard Materials";
        }

        if (difficulty == 4){
            return ChatColor.GOLD + "Add/Remove Extreme Materials";
        }

        if (difficulty == 5){
            return ChatColor.RED + "Add/Remove Impossible Materials";
        }
        return null;
    }

    public int getDifficultyInt(String display){
        if (display.equals(ChatColor.AQUA + "Add/Remove Easy Materials")){
            return 1;
        }

        if (display.equals(ChatColor.GREEN + "Add/Remove Normal Materials")){
            return 2;
        }

        if (display.equals(ChatColor.YELLOW + "Add/Remove Hard Materials")){
            return 3;
        }

        if (display.equals(ChatColor.GOLD + "Add/Remove Extreme Materials")){
            return 4;
        }

        if (display.equals(ChatColor.RED + "Add/Remove Impossible Materials")){
            return 5;
        }
        return 0;
    }

}
