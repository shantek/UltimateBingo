package io.shantek.listeners;

import io.shantek.UltimateBingo;
import io.shantek.managers.BingoGameGUIManager;
import io.shantek.managers.SettingsManager;
import io.shantek.tools.ItemBuilder;
import io.shantek.tools.MaterialList;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class SettingsListener implements Listener {
    MaterialList materialList;
    private SettingsManager settingsManager;
    public BingoGameGUIManager bingoGameGUIManager;
    public UltimateBingo ultimateBingo;
    private boolean sentWarning;

    public SettingsListener(MaterialList materialList, SettingsManager settingsManager, BingoGameGUIManager bingoGameGUIManager, UltimateBingo ultimateBingo) {
        this.materialList = materialList;
        this.settingsManager = settingsManager;
        this.bingoGameGUIManager = bingoGameGUIManager;
        this.ultimateBingo = ultimateBingo;
        sentWarning = false;
    }

    Random random = new Random(); // Create a Random object

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();

        if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

            // Ensure the event was triggered in the Bingo configuration GUI
            if (e.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo Configuration")) {
                e.setCancelled(true);  // Prevent dragging items

                int slot = e.getRawSlot();
                // Ensure clicks are within the inventory size
                if (slot >= 0 && slot < 9) {
                    switch (slot) {
                        case 0:
                            bingoGameGUIManager.toggleGameMode(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 1:
                            bingoGameGUIManager.toggleDifficulty(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 2:
                            bingoGameGUIManager.toggleCardSize(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 3:
                            bingoGameGUIManager.toggleCardType(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 4:
                            bingoGameGUIManager.toggleWinCondition(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 5:
                            bingoGameGUIManager.toggleRevealCards(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 6:
                            bingoGameGUIManager.toggleGameTime(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 7:
                            bingoGameGUIManager.toggleLoadout(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                        case 8:

                            // Set all the game config ready to play
                            ultimateBingo.bingoGameGUIManager.setGameConfiguration();

                            ultimateBingo.bingoSpawnLocation = player.getLocation();
                            ultimateBingo.bingoCommand.startBingo(player);

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            player.closeInventory();
                            break;
                        default:
                            // This case handles any undefined slots, no action is taken
                            break;
                    }
                }

                ultimateBingo.configFile.saveConfig();

            } else if (e.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo Settings")) {
                e.setCancelled(true);

                if (e.getCurrentItem().getItemMeta() != null) {
                    createItemSettings(player, settingsManager.getDifficultyInt(e.getCurrentItem().getItemMeta().getDisplayName()));
                }

            } else {

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

                if (e.getClickedInventory() == e.getView().getTopInventory() && e.getCurrentItem() != null) {
                    if (isEasyDifficulty || isNormalDifficulty || isHardDifficulty || isExtremeDifficulty || isImpossibleDifficulty) {
                        e.setCancelled(true);

                        ItemStack clickedItem = e.getCurrentItem();

                        boolean itemRemoved = false;

                        if (e.getClick().isLeftClick()) {

                            if (isEasyDifficulty) {

                                if (materialList.easy.size() <= 15) {
                                    player.sendMessage(ChatColor.RED + "This category needs a minimum of 15 items.");
                                } else {
                                    itemRemoved = true;
                                    materialList.removeItem(clickedItem.getType(), 1);
                                    createItemSettings(player, 1);
                                }
                            } else if (isNormalDifficulty) {
                                if (materialList.normal.size() <= 15) {
                                    player.sendMessage(ChatColor.RED + "This category needs a minimum of 15 items.");
                                } else {
                                    itemRemoved = true;
                                    materialList.removeItem(clickedItem.getType(), 2);
                                    createItemSettings(player, 2);
                                }
                            } else if (isHardDifficulty) {
                                if (materialList.hard.size() <= 10) {
                                    player.sendMessage(ChatColor.RED + "This category needs a minimum of 10 items.");
                                } else {
                                    itemRemoved = true;
                                    materialList.removeItem(clickedItem.getType(), 3);
                                    createItemSettings(player, 3);
                                }
                            } else if (isExtremeDifficulty) {
                                if (materialList.extreme.size() <= 10) {
                                    player.sendMessage(ChatColor.RED + "This category needs a minimum of 10 items.");
                                } else {
                                    itemRemoved = true;
                                    materialList.removeItem(clickedItem.getType(), 4);
                                    createItemSettings(player, 4);
                                }
                            } else if (isImpossibleDifficulty) {
                                if (materialList.impossible.size() <= 5) {
                                    player.sendMessage(ChatColor.RED + "This category needs a minimum of 5 items.");
                                } else {
                                    itemRemoved = true;
                                    materialList.removeItem(clickedItem.getType(), 5);
                                    createItemSettings(player, 5);
                                }
                            }
                            if (itemRemoved) {
                                player.sendMessage(ChatColor.GREEN + "You removed "
                                        + ChatColor.GOLD + clickedItem.getType().name() + ChatColor.GREEN + " from the Bingo Items");
                            }
                        }
                    }
                }

                Inventory clickedInv = e.getClickedInventory();
                if (clickedInv != null && clickedInv.getType() == InventoryType.PLAYER) {

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
