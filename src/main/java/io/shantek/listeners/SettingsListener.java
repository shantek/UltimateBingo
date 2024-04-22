package io.shantek.listeners;

import io.shantek.managers.SettingsManager;
import io.shantek.tools.ItemBuilder;
import io.shantek.tools.MaterialList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SettingsListener implements Listener {
    MaterialList materialList;
    private SettingsManager settingsManager;
    private boolean sentWarning;
    public SettingsListener(MaterialList materialList, SettingsManager settingsManager){
        this.materialList = materialList;
        this.settingsManager = settingsManager;
        sentWarning = false;
    }

    @EventHandler
    public void onInventoryClick (InventoryClickEvent e){
        boolean isSameInventory = ChatColor.translateAlternateColorCodes('&',
                e.getView().getTitle()).equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo Settings") && e.getCurrentItem() != null;

        if (isSameInventory && e.getWhoClicked() instanceof Player player){
            e.setCancelled(true);

            if (e.getCurrentItem().getItemMeta() != null){
                createItemSettings(player, settingsManager.getDifficultyInt(e.getCurrentItem().getItemMeta().getDisplayName()));
            }
        }

        boolean isEasyDifficulty = ChatColor.translateAlternateColorCodes('&'
                , e.getView().getTitle()).equals(settingsManager.getDifficultyDisplay(1));

        boolean isNormalDifficulty = ChatColor.translateAlternateColorCodes('&'
                , e.getView().getTitle()).equals(settingsManager.getDifficultyDisplay(2));

        boolean isHardDifficulty = ChatColor.translateAlternateColorCodes('&'
                , e.getView().getTitle()).equals(settingsManager.getDifficultyDisplay(3));

        boolean isExtremeDifficulty = ChatColor.translateAlternateColorCodes('&'
                , e.getView().getTitle()).equals(settingsManager.getDifficultyDisplay(4));

        boolean isImpossibleDifficulty = ChatColor.translateAlternateColorCodes('&'
                , e.getView().getTitle()).equals(settingsManager.getDifficultyDisplay(5));

        if (e.getClickedInventory() == e.getView().getTopInventory() && e.getCurrentItem() != null && e.getWhoClicked() instanceof Player player){
            if (isEasyDifficulty || isNormalDifficulty || isHardDifficulty || isExtremeDifficulty || isImpossibleDifficulty){
                e.setCancelled(true);

                ItemStack clickedItem = e.getCurrentItem();;

                if (e.getClick().isLeftClick()){

                    if (isEasyDifficulty){
                        materialList.removeItem(clickedItem.getType(),1);
                        createItemSettings(player, 1);
                    }

                    if (isNormalDifficulty){
                        materialList.removeItem(clickedItem.getType(), 2);
                        createItemSettings(player, 2);
                    }

                    if (isHardDifficulty){
                        materialList.removeItem(clickedItem.getType(), 3);
                        createItemSettings(player, 3);

                    }

                    if (isExtremeDifficulty){
                        materialList.removeItem(clickedItem.getType(), 4);
                        createItemSettings(player, 4);

                    }

                    if (isImpossibleDifficulty){
                        materialList.removeItem(clickedItem.getType(), 5);
                        createItemSettings(player, 5);

                    }

                    player.sendMessage(ChatColor.GREEN + "You removed "
                            + ChatColor.GOLD + clickedItem.getType().name() + ChatColor.GREEN +" from the Bingo Items");
                }


            }
        }


        Inventory clickedInv = e.getClickedInventory();

        if (clickedInv != null && clickedInv.getType() == InventoryType.PLAYER && e.getWhoClicked() instanceof Player player) {

            if (isEasyDifficulty || isNormalDifficulty || isHardDifficulty || isExtremeDifficulty || isImpossibleDifficulty) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();

                if (clickedItem != null) {
                    Material material = clickedItem.getType();

                    if (settingsManager.getDifficultyInt(e.getView().getTitle()) != 0) {
                        if (!materialList.getMaterials().get(settingsManager.getDifficultyInt(e.getView().getTitle())).contains(material)) {

                            materialList.add(material, settingsManager.getDifficultyInt(e.getView().getTitle()));
                            player.sendMessage(ChatColor.GREEN + "You added " + ChatColor.GOLD
                                    + material.name() + ChatColor.GREEN + " to the Bingo Items!");
                            createItemSettings(player, settingsManager.getDifficultyInt(e.getView().getTitle()));

                            materialList.saveMaterialsToFile();


                        } else {
                            player.sendMessage(ChatColor.RED + material.name() + " already exists in this difficulty!");
                        }

                    } else {
                        player.sendMessage(ChatColor.RED + "An error occurred, please try again.");
                    }
                }
            }
        }


    }
    public void createItemSettings(Player player, int difficulty){
        Inventory bingoItems = Bukkit.createInventory(player, 54, settingsManager.getDifficultyDisplay(difficulty));

        for (Material material : materialList.getMaterials().get(difficulty)){
            ItemStack item = new ItemBuilder(material).withLore(ChatColor.GRAY + "LEFT-CLICK to Remove").build();
            bingoItems.addItem(item);

        }
        if (bingoItems.getItem(53) != null && !sentWarning){
            player.sendMessage(ChatColor.RED + "You have reached the maximum of items that are visible in the GUI! " +
                    "The item will still be added but you can't see it in the settings if you do not remove any other items. " +
                    "In the future there will hopefully be GUI pages to see all added items. This message will only be sent once after every restart." );
            sentWarning = true;
        }

        player.openInventory(bingoItems);
    }
}
