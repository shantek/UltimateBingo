package io.shantek.managers;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CardTypes {

    UltimateBingo ultimateBingo;
    public CardTypes(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    public boolean checkSmallCardBingo(Player player) {

        UUID playerId = player.getUniqueId();

        Inventory inv;
        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
            inv = ultimateBingo.groupInventory;
        } else {
            inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);
        }

        if (ultimateBingo.currentFullCard) {
            // Check for a full card bingo instead of individual lines
        }
        for (int i : new int[]{10, 19, 28}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+1).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+2).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+9).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+18).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(20).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(30).getType() == ultimateBingo.tickedItemMaterial) ||
                (inv.getItem(12).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(20).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(28).getType() == ultimateBingo.tickedItemMaterial)) {
            return true;
        }

        return false;
    }

    public boolean checkMediumCardBingo(Player player) {

        UUID playerId = player.getUniqueId();
        Inventory inv;
        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
            inv = ultimateBingo.groupInventory;
        } else {
            inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);
        }

        for (int i : new int[]{10, 19, 28, 37}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+1).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+2).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+3).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12, 13}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+9).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+18).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+27).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(20).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(30).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(40).getType() == ultimateBingo.tickedItemMaterial) ||
                (inv.getItem(13).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(21).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(29).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(37).getType() == ultimateBingo.tickedItemMaterial)) {
            return true;
        }

        return false;
    }

    public boolean checkLargeCardBingo(Player player) {

        UUID playerId = player.getUniqueId();
        Inventory inv;
        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
            inv = ultimateBingo.groupInventory;
        } else {
            inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);
        }

        for (int i : new int[]{10, 19, 28, 37, 46}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+1).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+2).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+3).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+4).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12, 13, 14}) {
            if (inv.getItem(i).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+9).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+18).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+27).getType() == ultimateBingo.tickedItemMaterial &&
                    inv.getItem(i+36).getType() == ultimateBingo.tickedItemMaterial) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(20).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(30).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(40).getType() == ultimateBingo.tickedItemMaterial &&
                inv.getItem(50).getType() == ultimateBingo.tickedItemMaterial) ||

                (inv.getItem(14).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(22).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(30).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(38).getType() == ultimateBingo.tickedItemMaterial &&
                        inv.getItem(46).getType() == ultimateBingo.tickedItemMaterial)) {
            return true;
        }

        return false;
    }

    public boolean checkFullCard(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inv;
        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
            inv = ultimateBingo.groupInventory;
        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

            inv = ultimateBingo.bingoFunctions.getTeamInventory(player);

        } else {
            inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            // Skip the check for slot 17
            if (i == 17) continue;

            ItemStack item = inv.getItem(i); // Get the item in the current slot
            // If the slot is not empty and not lime concrete, return false
            if (item != null && item.getType() != ultimateBingo.tickedItemMaterial) {
                return false;
            }
        }

        // If all slots are either empty or lime concrete, return true
        return true;
    }
}
