package io.shantek.managers;

import io.shantek.UltimateBingo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class CardTypes {

    UltimateBingo ultimateBingo;
    public CardTypes(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    public boolean checkSmallCardBingo(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);

        for (int i : new int[]{10, 19, 28}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+1).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+2).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+9).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+18).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == Material.LIME_CONCRETE &&
                inv.getItem(20).getType() == Material.LIME_CONCRETE &&
                inv.getItem(30).getType() == Material.LIME_CONCRETE) ||
                (inv.getItem(13).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(21).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(29).getType() == Material.LIME_CONCRETE)) {
            return true;
        }

        return false;
    }

    public boolean checkMediumCardBingo(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);

        for (int i : new int[]{10, 19, 28, 37}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+1).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+2).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+3).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12, 13}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+9).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+18).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+27).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == Material.LIME_CONCRETE &&
                inv.getItem(20).getType() == Material.LIME_CONCRETE &&
                inv.getItem(30).getType() == Material.LIME_CONCRETE &&
                inv.getItem(40).getType() == Material.LIME_CONCRETE) ||
                (inv.getItem(13).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(21).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(29).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(37).getType() == Material.LIME_CONCRETE)) {
            return true;
        }

        return false;
    }

    public boolean checkLargeCardBingo(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inv = ultimateBingo.bingoManager.getBingoGUIs().get(playerId);

        for (int i : new int[]{10, 19, 28, 37, 46}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+1).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+2).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+3).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+4).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        for (int i : new int[]{10, 11, 12, 13, 14}) {
            if (inv.getItem(i).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+9).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+18).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+27).getType() == Material.LIME_CONCRETE &&
                    inv.getItem(i+36).getType() == Material.LIME_CONCRETE) {
                return true;
            }
        }

        if ((inv.getItem(10).getType() == Material.LIME_CONCRETE &&
                inv.getItem(20).getType() == Material.LIME_CONCRETE &&
                inv.getItem(30).getType() == Material.LIME_CONCRETE &&
                inv.getItem(40).getType() == Material.LIME_CONCRETE &&
                inv.getItem(50).getType() == Material.LIME_CONCRETE) ||

                (inv.getItem(13).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(21).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(29).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(37).getType() == Material.LIME_CONCRETE &&
                        inv.getItem(45).getType() == Material.LIME_CONCRETE)) {
            return true;
        }

        return false;
    }



}
