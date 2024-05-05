package io.shantek.tools;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class BingoFunctions
{
    UltimateBingo ultimateBingo;
    public BingoFunctions(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    // Reset the players state at the start and end of games
    public void resetPlayers(){
        for (Player player : Bukkit.getOnlinePlayers()){
            // Reset health to max health (20.0 is full health)
            player.setHealth(20.0);

            // Reset food level to max (20 is full hunger)
            player.setFoodLevel(20);

            // Reset saturation to max (5.0F is full saturation)
            player.setSaturation(5.0F);

            // Reset exhaustion to 0 (no exhaustion)
            player.setExhaustion(0.0F);

            // Reset remaining potion effects
            for (PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }

            // Clear inventory
            player.getInventory().clear();

            // Clear armor
            player.getInventory().setArmorContents(new ItemStack[4]);

            // Reset XP and levels
            player.setExp(0);
            player.setLevel(0);

        }
    }

    public void resetIndividualPlayer(Player player) {

        // Reset health to max health (20.0 is full health)
        player.setHealth(20.0);

        // Reset food level to max (20 is full hunger)
        player.setFoodLevel(20);

        // Reset saturation to max (5.0F is full saturation)
        player.setSaturation(5.0F);

        // Reset exhaustion to 0 (no exhaustion)
        player.setExhaustion(0.0F);

        // Reset remaining potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Clear inventory
        player.getInventory().clear();

        // Clear armor
        player.getInventory().setArmorContents(new ItemStack[4]);

        // Reset XP and levels
        player.setExp(0);
        player.setLevel(0);
    }

    public void giveBingoCard(Player player) {
        PlayerInventory inventory = player.getInventory(); // Get the player's inventory

        // Check if the player already has a bingo card
        if (hasBingoCard(inventory)) {
            player.sendMessage(ChatColor.YELLOW + "You already have a Bingo Card.");
            return; // Stop further execution if they already have one
        }

        ItemStack bingoCard = new ItemStack(ultimateBingo.bingoCardMaterial);
        ItemMeta itemMeta = bingoCard.getItemMeta();

        if (itemMeta != null) { // Always good to check for null when working with ItemMeta
            // Set display name for the Bingo Card
            itemMeta.setDisplayName(ChatColor.GOLD + "Bingo Card");

            // Set lore with two lines
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Card type: " + (ultimateBingo.uniqueCard ? ChatColor.BLUE + "Unique" : ChatColor.BLUE + "Identical") + "/" + ultimateBingo.difficulty);
            lore.add(ChatColor.GRAY + "Win condition: " + (ultimateBingo.fullCard ? ChatColor.BLUE + "Full card" : ChatColor.BLUE + "Single row"));

            itemMeta.setLore(lore); // Apply the lore to the item meta
            bingoCard.setItemMeta(itemMeta); // Apply the modified item meta back to the item stack

            // Check if the inventory is full
            if (inventory.firstEmpty() == -1) {
                player.sendMessage(ChatColor.RED + "Unable to give you a bingo card, your inventory is full.");
            } else {
                // Check if slot 0 is empty
                if (inventory.getItem(0) == null) {
                    inventory.setItem(0, bingoCard); // Place the bingo card in slot 0
                } else {
                    inventory.addItem(bingoCard); // Automatically places in the first available slot
                }
            }
        } else {
            // Log or handle the case where item meta couldn't be retrieved
            Bukkit.getLogger().warning("Failed to retrieve item meta for Bingo Card.");
        }
    }

    private boolean hasBingoCard(PlayerInventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == ultimateBingo.bingoCardMaterial) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Bingo Card")) {
                    return true; // Bingo card found
                }
            }
        }
        return false; // No Bingo card found
    }
    // Give all players a bingo card
    public void giveBingoCardToAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            giveBingoCard(player);
        }
    }

    // Reset the time and weather at the start of the game
    public void resetTimeAndWeather() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(0);  // Set time to 0 (6 AM)
        }

        // Clear weather
        for (World world : Bukkit.getWorlds()) {
            world.setStorm(false);  // Disable rain
            world.setThundering(false);  // Disable thunder
            world.setWeatherDuration(0);  // Set weather duration to 0
        }
    }

    // Despawn all items on the ground at the start/end of the game
    public void despawnAllItems() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(Item.class)) {
                entity.remove();
            }
        }
    }

}
