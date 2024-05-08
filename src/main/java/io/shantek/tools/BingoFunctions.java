package io.shantek.tools;

import io.shantek.UltimateBingo;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
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

    public void resetIndividualPlayer(Player player, boolean fullReset) {

        // Reset health to max health (20.0 is full health)
        player.setHealth(20.0);

        // Reset food level to max (20 is full hunger)
        player.setFoodLevel(20);

        // Reset saturation to max (5.0F is full saturation)
        player.setSaturation(5.0F);

        // Reset exhaustion to 0 (no exhaustion)
        player.setExhaustion(0.0F);

        if (fullReset) {
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

    // Speed run equipment for players
    public void equipSpeedRunGear(Player player) {
        // Create and set armor
        player.getInventory().setHelmet(createEnchantedArmor(Material.NETHERITE_HELMET, new Enchantment[]{
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.WATER_WORKER, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
        }, new int[]{4, 1, 1, 3, 1, 1}));
        player.getInventory().setChestplate(createEnchantedArmor(Material.NETHERITE_CHESTPLATE, new Enchantment[]{
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
        }, new int[]{4, 1, 3, 1, 1}));
        player.getInventory().setLeggings(createEnchantedArmor(Material.NETHERITE_LEGGINGS, new Enchantment[]{
                Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
        }, new int[]{4, 1, 3, 1, 1}));
        player.getInventory().setBoots(createEnchantedArmor(Material.NETHERITE_BOOTS, new Enchantment[]{
                Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
        }, new int[]{4, 4, 1, 3, 1, 1}));

        // Equip shield
        ItemStack shield = new ItemStack(Material.SHIELD);
        player.getInventory().setItemInOffHand(shield);

        // Give player their basic tools
        player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_SWORD, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.SWEEPING_EDGE}, new int[]{5, 2, 2, 3, 3}));
        player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_PICKAXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY}, new int[]{5, 3, 3}));
        player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_AXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.SILK_TOUCH}, new int[]{5, 3, 1, 1}));
        player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_SHOVEL, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{5, 3, 1}));

        // Add additional items
        player.getInventory().addItem(new ItemStack(Material.PURPLE_BED));
        player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));
        player.getInventory().addItem(new ItemStack(Material.JUNGLE_BOAT, 1));
    }

    // Utility method to create enchanted armor
    private ItemStack createEnchantedArmor(Material material, Enchantment[] enchantments, int[] levels) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        for (int i = 0; i < enchantments.length; i++) {
            meta.addEnchant(enchantments[i], levels[i], true);
        }
        item.setItemMeta(meta);
        return item;
    }

    // Utility method to create an enchanted item
    private ItemStack createEnchantedItem(Material material, Enchantment[] enchantments, int[] levels) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        for (int i = 0; i < enchantments.length; i++) {
            meta.addEnchant(enchantments[i], levels[i], true);
        }
        item.setItemMeta(meta);
        return item;
    }

    // Logic to find safe locations for teleporting

    // Method to scatter players and ensure they face the center horizontally
    public void safeScatterPlayers(List<Player> players, Location center, int radius) {
        World world = center.getWorld();
        Random random = new Random();

        for (Player player : players) {
            Location safeLocation = findSafeLocation(world, center, radius, random);
            player.teleport(safeLocation);
            setFacing(player, center);
        }
    }

    // Find a safe location around a given center within a specified radius
    private Location findSafeLocation(World world, Location center, int radius, Random random) {
        for (int i = 0; i < 10; i++) { // Attempt up to 10 times to find a safe location
            int dx = random.nextInt(radius * 2) - radius;
            int dz = random.nextInt(radius * 2) - radius;
            Location loc = center.clone().add(dx, 0, dz);
            loc = world.getHighestBlockAt(loc).getLocation().add(0, 1, 0); // Adjust to one above the highest solid block

            if (isSafeLocation(loc)) {
                return loc;
            }
        }
        return center; // Return the center if no safe location is found
    }

    // Determine if a location is safe for teleportation
    private boolean isSafeLocation(Location location) {
        Material block = location.getBlock().getType();
        Material below = location.clone().add(0, -1, 0).getBlock().getType();
        return below.isSolid() && block == Material.AIR; // Ensure solid ground and free space above
    }

    // Set the facing of the player towards the center point horizontally
    private void setFacing(Player player, Location center) {
        Location playerLoc = player.getLocation();
        double dx = center.getX() - playerLoc.getX();
        double dz = center.getZ() - playerLoc.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90;
        playerLoc.setYaw(yaw);
        playerLoc.setPitch(0); // Ensure players look straight ahead, not up or down
        player.teleport(playerLoc);
    }

    // Settings items for player bingo cards
    public ItemStack createSpyglass() {
        ItemStack spyglass = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = spyglass.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "View Players Cards");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Get a peek at other players' cards!"));
        spyglass.setItemMeta(meta);
        return spyglass;
    }

    public void setGameTimer() {

        // Get a reference to the plugin for us to schedule tasks
        UltimateBingo plugin = UltimateBingo.getInstance();

        if (ultimateBingo.gameTime == 0) {
            // No game timer has been set.
            setGameTimerTasks(plugin, 20, 0.4f);
            setGameTimerTasks(plugin, 40, 0.5f);
            setGameTimerTasks(plugin, 60, 0.6f);

        } else {
            // This game has a game timer, no perks will be given
            // We'll add a timer to end the game and send some warnings prior to ending
            setGameCountdownTask(plugin, ultimateBingo.gameTime);
        }

    }

    public void setGameCountdownTask(Plugin plugin, int minutes) {

        // Calculate the amount of ticks needed
        int threeMinutesLeft = (minutes - 3) * 60 * 20;
        int twoMinutesLeft = (minutes - 2) * 60 * 20;
        int oneMinuteLeft = (minutes - 1) * 60 * 20;
        int gameLength = minutes * 60 * 20;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The game will end in 3 minutes!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                            }
        }, threeMinutesLeft); // Delay of 18,000 ticks, equivalent to 15 minutes

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The game will end in 3 minutes!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            }
        }, twoMinutesLeft); // Delay of 18,000 ticks, equivalent to 15 minutes

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The game will end in 1 minute!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
            }
        }, oneMinuteLeft); // Delay of 18,000 ticks, equivalent to 15 minutes

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            ultimateBingo.bingoCommand.stopBingo(null, false);

        }, gameLength); // Delay of 18,000 ticks, equivalent to 15 minutes

    }

    public void setGameTimerTasks(Plugin plugin, int minutes, float walkSpeed) {

        // Calculate the amount of ticks needed
        int delayTicks = minutes * 60 * 20;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Bukkit.broadcastMessage(ChatColor.GREEN + "The game has now been running for " + minutes + " minutes.");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "You've just received a speed boost!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);

                // Increse their walk speed
                p.setWalkSpeed(walkSpeed);

            }
        }, delayTicks); // Delay of 18,000 ticks, equivalent to 15 minutes

    }
}
