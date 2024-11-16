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
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class BingoFunctions
{
    UltimateBingo ultimateBingo;
    public BingoFunctions(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    private Random random = new Random();
    private HashMap<UUID, Boolean> playerMap = new HashMap<>();

    //region Resetting the players

    // Reset the players state at the start and end of games
    public void resetPlayers(){
        for (Player player : Bukkit.getOnlinePlayers()){

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

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

            // Double check they're in the correct world to avoid
            // removing any actual inventory from another world
            if (isActivePlayer(player)) {

                // Clear inventory
                player.getInventory().clear();

            }
            // Clear armor
            player.getInventory().setArmorContents(new ItemStack[4]);

            // Reset XP and levels
            player.setExp(0);
            player.setLevel(0);

        }
    }



    //endregion

    //region Bingo card functionality

    public int countCompleted(Inventory inventory) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == ultimateBingo.tickedItemMaterial) {
                count++;
            }
        }
        return count;
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

        if (itemMeta != null) {
            // Set display name for the Bingo Card
            itemMeta.setDisplayName(ChatColor.GOLD + "Bingo Card");

            // Set lore with two lines
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Card type: " + (ultimateBingo.currentUniqueCard ? ChatColor.BLUE + "Unique" : ChatColor.BLUE + "Identical") + "/" + ultimateBingo.currentDifficulty);
            lore.add(ChatColor.GRAY + "Win condition: " + (ultimateBingo.currentFullCard ? ChatColor.BLUE + "Full card" : ChatColor.BLUE + "Single row"));

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

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {
                giveBingoCard(player);
            }
        }
    }

    public int countTickedItems(List<ItemStack> items) {
        int count = 0;
        for (ItemStack item : items) {
            // Check if the item is not null and is specifically LIME_CONCRETE
            if (item != null && item.getType() == ultimateBingo.tickedItemMaterial) {
                count++;
            }
        }
        return count;
    }

    // Utility method to clone an inventory
    public Inventory cloneInventory(Inventory original) {

        // Store the string for the card type
        String newCardInfo = ultimateBingo.currentUniqueCard ? "unique" : "identical";
        newCardInfo += ultimateBingo.currentFullCard ? "/full card" : "/single row";
        newCardInfo = "(" + newCardInfo + ")";

        Inventory clone = Bukkit.createInventory(null, original.getSize(), ChatColor.GREEN.toString() + ChatColor.BOLD + "Bingo" + ChatColor.BLACK + " " + ChatColor.GOLD + newCardInfo);
        for (int i = 0; i < original.getSize(); i++) {
            ItemStack originalItem = original.getItem(i);
            if (originalItem != null) {
                clone.setItem(i, new ItemStack(originalItem));
            }
        }
        return clone;
    }

    //endregion

    //region Resetting the world

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

    //endregion

    //region Item stacks and equipping

    // Speed run equipment for players
    public void equipLoadoutGear(Player player, int loadout) {

        if (loadout == 1) {

            //region 1st load-out - Basic starter gear

            player.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
            player.getInventory().addItem(new ItemStack(Material.WOODEN_AXE));
            player.getInventory().addItem(new ItemStack(Material.WOODEN_PICKAXE));
            player.getInventory().addItem(new ItemStack(Material.WOODEN_SHOVEL));
            player.getInventory().addItem(new ItemStack(Material.WOODEN_HOE));
            player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));

            //endregion

        } else if (loadout == 2) {

            //region 2nd load-out - Boat

            // Create and set armor
            player.getInventory().setHelmet(createEnchantedArmor(Material.IRON_HELMET, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.WATER_WORKER, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1, 1}));
            player.getInventory().setChestplate(createEnchantedArmor(Material.IRON_CHESTPLATE, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().setLeggings(createEnchantedArmor(Material.IRON_LEGGINGS, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().setBoots(createEnchantedArmor(Material.IRON_BOOTS, new Enchantment[]{
                    Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1, 1}));

            // Equip shield
            ItemStack shield = new ItemStack(Material.SHIELD);
            player.getInventory().setItemInOffHand(shield);

            // Give player their basic tools
            player.getInventory().addItem(createEnchantedItem(Material.IRON_SWORD, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.SWEEPING_EDGE}, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_PICKAXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY}, new int[]{1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_AXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.SILK_TOUCH}, new int[]{1, 1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_SHOVEL, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_HOE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{1, 1, 1}));

            // Add additional items
            player.getInventory().addItem(new ItemStack(Material.RED_BED));
            player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));
            player.getInventory().addItem(new ItemStack(Material.JUNGLE_BOAT, 1));

            //endregion

        } else if (loadout == 3) {

            //region 3rd load-out - Wings

            // Create and set armor
            player.getInventory().setHelmet(createEnchantedArmor(Material.NETHERITE_HELMET, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.WATER_WORKER, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{4, 1, 1, 3, 1, 1}));
            player.getInventory().setChestplate(createEnchantedElytra(new Enchantment[]{
                    Enchantment.DURABILITY, // Unbreaking
                    Enchantment.MENDING
            }, new int[]{3, 1})); // Level 3 Unbreaking, Level 1 Mending
            player.getInventory().setLeggings(createEnchantedArmor(Material.NETHERITE_LEGGINGS, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{4, 1, 3, 1, 1}));
            player.getInventory().setBoots(createEnchantedArmor(Material.NETHERITE_BOOTS, new Enchantment[]{
                    Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{4, 4, 1, 3, 1, 1}));

            // Give player their basic tools
            ItemStack fireworkStack = createFireworkRocket();
            player.getInventory().addItem(fireworkStack);

            player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_SWORD, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.SWEEPING_EDGE}, new int[]{5, 2, 2, 3, 3}));
            player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_PICKAXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY}, new int[]{5, 3, 3}));
            player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_AXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.SILK_TOUCH}, new int[]{5, 3, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_SHOVEL, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{5, 3, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.NETHERITE_HOE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{5, 3, 1}));

            // Add additional items
            player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));

            //endregion

        } else if (loadout == 4) {

            //region 4th load-out - Archer

            // Create and set armor
            player.getInventory().setHelmet(createEnchantedArmor(Material.IRON_HELMET, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.WATER_WORKER, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1, 1}));
            player.getInventory().setChestplate(createEnchantedArmor(Material.IRON_CHESTPLATE, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().setLeggings(createEnchantedArmor(Material.IRON_LEGGINGS, new Enchantment[]{
                    Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().setBoots(createEnchantedArmor(Material.IRON_BOOTS, new Enchantment[]{
                    Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.MENDING, Enchantment.DURABILITY, Enchantment.VANISHING_CURSE, Enchantment.BINDING_CURSE
            }, new int[]{1, 1, 1, 1, 1, 1}));

            // Equip shield
            ItemStack shield = new ItemStack(Material.SHIELD);
            player.getInventory().setItemInOffHand(shield);

            // Give player their basic tools
            player.getInventory().addItem(createEnchantedItem(Material.IRON_SWORD, new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.SWEEPING_EDGE}, new int[]{1, 1, 1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_PICKAXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.LOOT_BONUS_BLOCKS, Enchantment.DURABILITY}, new int[]{1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_AXE, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING, Enchantment.SILK_TOUCH}, new int[]{1, 1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.IRON_SHOVEL, new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.MENDING}, new int[]{1, 1, 1}));
            player.getInventory().addItem(createEnchantedItem(Material.BOW, new Enchantment[]{Enchantment.ARROW_INFINITE, Enchantment.DURABILITY, Enchantment.ARROW_DAMAGE, Enchantment.ARROW_FIRE, Enchantment.ARROW_KNOCKBACK}, new int[]{1, 3, 5, 1, 2}));


            // Add additional items
            player.getInventory().addItem(new ItemStack(Material.ORANGE_BED));
            player.getInventory().addItem(new ItemStack(Material.CRAFTING_TABLE, 1));
            player.getInventory().addItem(new ItemStack(Material.JUNGLE_CHEST_BOAT, 1));
            player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
            //endregion
        }
    }

    private ItemStack createFireworkRocket() {
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, 64); // Create a stack of 64 rockets
        FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();

        if (fireworkMeta != null) {
            // Use reflection to ensure the effects list is initialized without adding effects
            try {
                java.lang.reflect.Field effectsField = fireworkMeta.getClass().getDeclaredField("effects");
                effectsField.setAccessible(true);
                effectsField.set(fireworkMeta, new ArrayList<FireworkEffect>());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Set the flight duration to level 3
            fireworkMeta.setPower(3);
            firework.setItemMeta(fireworkMeta);
       }

        return firework;
    }
    private ItemStack createEnchantedElytra(Enchantment[] enchantments, int[] levels) {
        // Create an Elytra item
        ItemStack elytra = new ItemStack(Material.ELYTRA);

        // Apply enchantments
        ItemMeta meta = elytra.getItemMeta();
        for (int i = 0; i < enchantments.length; i++) {
            meta.addEnchant(enchantments[i], levels[i], true);
        }
        elytra.setItemMeta(meta);

        return elytra;
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

    // Settings items for player bingo cards
    public ItemStack createSpyglass() {
        ItemStack spyglass = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = spyglass.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "View Players Cards");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Get a peek at other players' cards!"));
        spyglass.setItemMeta(meta);
        return spyglass;
    }

    public void topUpFirstFireworkRocketsStack(Player player) {

        if (ultimateBingo.currentLoadoutType == 3) {

            PlayerInventory inventory = player.getInventory();
            ItemStack[] items = inventory.getContents();
            boolean rocketsFound = false;

            // Scan inventory for the first stack of rockets and top it up to 64 if found
            for (ItemStack item : items) {
                if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
                    item.setAmount(64);  // Set the count to 64
                    rocketsFound = true;
                    break;  // Stop after finding the first stack
                }
            }

            // Player has no rockets, let's give them a stack
            if (!rocketsFound) {
                ItemStack fireworkStack = createFireworkRocket();
                player.getInventory().addItem(fireworkStack);
            }
        }
    }

    //endregion

    //region Teleporting functionality

    // Method to scatter players and ensure they face the center horizontally
    public void safeScatterPlayers(List<Player> players, Location center, int radius) {
        World world = center.getWorld();
        Random random = new Random();

        for (Player player : players) {

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                Location safeLocation = findSafeLocation(world, center, radius, random, 20);
                player.teleport(safeLocation);
                setFacing(player, center);
            }
        }
    }

    // Find a safe location around a given center within a specified radius
    public Location findSafeLocation(World world, Location center, int radius, Random random, int attempts) {
        for (int i = 0; i < attempts; i++) { // Attempt up to 10 times to find a safe location
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

    //endregion

    //region Game timers

    public void setGameTimer() {

        // Get a reference to the plugin for us to schedule tasks
        UltimateBingo plugin = UltimateBingo.getInstance();

        if (ultimateBingo.gameTime == 0) {
            // No game timer has been set.
            setGameTimerTasks(plugin, 20, 0.25f);
            setGameTimerTasks(plugin, 40, 0.30f);
            setGameTimerTasks(plugin, 60, 0.35f);

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
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "The game will end in 3 minutes!");


            for (Player p : Bukkit.getOnlinePlayers()) {

                if (ultimateBingo.bingoFunctions.isActivePlayer(p)) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                }
                            }
        }, threeMinutesLeft);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "The game will end in 2 minutes!");
            for (Player p : Bukkit.getOnlinePlayers()) {

                if (ultimateBingo.bingoFunctions.isActivePlayer(p)) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                }
            }
        }, twoMinutesLeft);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "The game will end in 1 minute!");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (ultimateBingo.bingoFunctions.isActivePlayer(p)) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                }
            }
        }, oneMinuteLeft);

        // Loop for each of the last 5 seconds
        for (int i = 5; i >= 1; i--) {
            final int finalI = i;  // Create a final variable to use inside the lambda
            int delay = gameLength - (i * 20);  // Calculate delay in ticks (20 ticks per second)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Game ends in " + finalI + " second" + (finalI == 1 ? "" : "s") + "!");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Play a tone sound effect at the player's location

                    if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
                    }
                }
            }, delay);
        }


        // End the game
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            ultimateBingo.bingoCommand.bingoGameOver();

        }, gameLength);

    }

    public void setGameTimerTasks(Plugin plugin, int minutes, float walkSpeed) {

        // Calculate the amount of ticks needed
        int delayTicks = minutes * 60 * 20;

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "The game has now been running for " + minutes + " minutes.");
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.YELLOW + "You've just received a speed boost!");
            for (Player p : Bukkit.getOnlinePlayers()) {

                if (ultimateBingo.bingoFunctions.isActivePlayer(p)) {

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);

                    // Increse their walk speed
                    p.setWalkSpeed(walkSpeed);
                }
            }
        }, delayTicks); // Delay of 18,000 ticks, equivalent to 15 minutes

    }

    // Format duration from minutes to a readable format
    public String formatAndShowGameDuration(long durationMillis) {
        // Convert milliseconds to minutes
        long totalMinutes = durationMillis / 1000 / 60;

        // Determine the appropriate format based on the total minutes
        if (totalMinutes < 60) {
            return totalMinutes + (totalMinutes == 1 ? " minute" : " minutes");
        } else {
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;
            return String.format("%d hour%s %d minute%s",
                    hours, (hours == 1 ? "" : "s"),
                    minutes, (minutes == 1 ? "" : "s"));
        }
    }

    //endregion

    //region Bingo configuration functions

    public String validateOrDefault(String input, String[] validOptions, String defaultOption) {
        input = input.toLowerCase();
        for (String option : validOptions) {
            if (option.equals(input)) {
                return option;
            }
        }
        return validOptions[random.nextInt(validOptions.length)];
    }

    public int validateOrDefaultInt(int input, int range, int defaultOption) {
        try {
            if (input >= 0 && input < range) {
                return input;
            }
        } catch (NumberFormatException e) {
            // Log or handle error if necessary
        }
        return random.nextInt(range);
    }

    public boolean validateOrDefaultBoolean(String input, String[] validOptions, boolean defaultOption) {
        input = input.toLowerCase();
        if (validOptions[0].equals(input)) {
            return true;
        } else if (validOptions[1].equals(input)) {
            return false;
        }
        return random.nextBoolean();
    }

    //endregion

    //region Player notifications

    public boolean isActivePlayer(Player player) {

        boolean isActivePlayer = true;

        // Check if multi world bingo is enabled and they're in the bingo world
        if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
            isActivePlayer = false;
        }

        if (isActivePlayer || !ultimateBingo.multiWorldServer) {
            isActivePlayer = true;
        }

        return isActivePlayer;

    }

    public int countActivePlayers() {
        int playerCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Check if the player has a generated bingo card
            if (ultimateBingo.bingoManager.checkHasBingoCard(player)) {
                playerCount++;
            }
        }
        return playerCount;
    }

  public void broadcastMessageToBingoPlayers(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isActivePlayer(player)) {
                player.sendMessage(message);
            }
        }
    }



    //endregion

    //region Potion functionality

    public void applyRandomNegativePotionToOtherPlayers(Player excludedPlayer, int durationInSeconds) {
        // Define a list of negative potion effects
        List<PotionEffectType> negativePotions = Arrays.asList(
                PotionEffectType.SLOW,
                PotionEffectType.SLOW_FALLING,
                PotionEffectType.BLINDNESS,
                PotionEffectType.SLOW_DIGGING,
                PotionEffectType.HUNGER,
                PotionEffectType.POISON,
                PotionEffectType.LEVITATION

        );

        // Random instance to select a random potion
        Random random = new Random();

        // Pick a random potion effect from the list (applies the same effect to all players)
        PotionEffectType randomPotion = negativePotions.get(random.nextInt(negativePotions.size()));

        // Convert the potion name to a friendly format
        String friendlyPotionName = randomPotion.getName().toLowerCase().replace('_', ' ').toUpperCase();

        // Loop through all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Exclude the passed player and only process active players
            if (!player.equals(excludedPlayer) && ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                // Remove all existing potion effects from the player
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }

                // Apply the selected potion effect for the given duration (in ticks, 20 ticks per second)
                player.addPotionEffect(new PotionEffect(randomPotion, durationInSeconds * 20, 0));

                // Create the subtitle message
                String subtitle = ChatColor.RED + friendlyPotionName + " for " + durationInSeconds + " seconds!";

                // Send the title (empty) and subtitle to the player
                player.sendTitle("", subtitle, 10, 70, 20);  // 10 ticks fade in, 70 ticks stay, 20 ticks fade out

                // Play the potion break sound to the player
                player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 1.0f);

            }

        }

        // Create the subtitle message
        String subtitle = ChatColor.GREEN + friendlyPotionName + " for " + durationInSeconds + " seconds!";

        // Send the title (empty) and subtitle to the player
        excludedPlayer.sendTitle("", subtitle, 10, 70, 20);  // 10 ticks fade in, 70 ticks stay, 20 ticks fade out
    }


    //endregion

    //region Player Tracking for Games

    // Method to store a UUID in the map
    public void addPlayer(UUID playerId) {
        playerMap.put(playerId, true);
    }

    // Method to clear the map
    public void clearPlayers() {
        playerMap.clear();
    }

    // Method to check if a UUID is in the map
    public boolean isPlayerInGame(UUID playerId) {
        return playerMap.containsKey(playerId);
    }

    //endregion

    //region Team functionality

    private Random teamRandom = new Random();
    private HashMap<UUID, Boolean> activePlayersMap = new HashMap<>();
    private HashMap<UUID, String> playerTeamsMap = new HashMap<>();

    // Store a reference to all online players
    public void assignTeams() {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> redTeam = new ArrayList<>();
        List<Player> yellowTeam = new ArrayList<>();
        List<Player> blueTeam = new ArrayList<>();
        List<Player> unassignedPlayers = new ArrayList<>();

        // Display initial messages
        onlinePlayers.forEach(player -> {
            boolean activePlayer = true;
            // Check if multi world bingo is enabled and they're in the bingo world
            if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
                activePlayer = false;
            }

            if (activePlayer) {
                // Check the block below the player
                Location locationBelow = player.getLocation().subtract(0, 1, 0);
                Material blockStandingOn = locationBelow.getBlock().getType();
                switch (blockStandingOn) {
                    case BLUE_WOOL:
                        blueTeam.add(player);
                        playerTeamsMap.put(player.getUniqueId(), "blue");
                        break;
                    case RED_WOOL:
                        redTeam.add(player);
                        playerTeamsMap.put(player.getUniqueId(), "red");
                        break;
                    case YELLOW_WOOL:
                        yellowTeam.add(player);
                        playerTeamsMap.put(player.getUniqueId(), "yellow");
                        break;
                    default:
                        unassignedPlayers.add(player);
                }
            }
        });

        distributeUnassignedPlayers(unassignedPlayers, redTeam, yellowTeam, blueTeam);

        // Print team assignments for testing
        onlinePlayers.forEach(player -> {
            String team = playerTeamsMap.getOrDefault(player.getUniqueId(), "None");
            player.sendMessage("You are on the " + team + " team!");
        });
    }

    private void distributeUnassignedPlayers(List<Player> unassignedPlayers, List<Player> redTeam, List<Player> yellowTeam, List<Player> blueTeam) {
        for (Player player : unassignedPlayers) {
            int redTeamSize = redTeam.size();
            int yellowTeamSize = yellowTeam.size();
            int blueTeamSize = blueTeam.size();

            if (redTeamSize <= yellowTeamSize && redTeamSize <= blueTeamSize) {
                redTeam.add(player);
                playerTeamsMap.put(player.getUniqueId(), "red");
            } else if (yellowTeamSize <= redTeamSize && yellowTeamSize <= blueTeamSize) {
                yellowTeam.add(player);
                playerTeamsMap.put(player.getUniqueId(), "yellow");
            } else {
                blueTeam.add(player);
                playerTeamsMap.put(player.getUniqueId(), "blue");
            }
        }
    }

    // Method to assign a player to an active team
    public void assignPlayerToActiveTeam(Player player) {
        Map<String, Integer> teamSizes = new LinkedHashMap<>();
        teamSizes.put("red", 0);
        teamSizes.put("yellow", 0);
        teamSizes.put("blue", 0);

        // Count the number of players in each team
        playerTeamsMap.values().forEach(team -> {
            if (teamSizes.containsKey(team)) {
                teamSizes.put(team, teamSizes.get(team) + 1);
            }
        });

        // Filter out the teams with no players
        teamSizes.entrySet().removeIf(entry -> entry.getValue() == 0);

        if (teamSizes.isEmpty()) {
            // No existing players in any team, inform the player
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "No active teams to join.");
            return;
        }

        // Find the team with the least players among the ones with at least one player
        String teamToJoin = teamSizes.keySet().stream()
                .min(Comparator.comparingInt(teamSizes::get))
                .orElseThrow();

        playerTeamsMap.put(player.getUniqueId(), teamToJoin);
        player.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "You have been assigned to the " + teamToJoin + " team!");
        notifyActivePlayers(player);
    }

    // Method to get the team of a player
    public String getTeam(Player player) {
        return playerTeamsMap.getOrDefault(player.getUniqueId(), "None");
    }

    // Method to get the team inventory of a player
    public Inventory getTeamInventory(Player player) {
        String team = getTeam(player);
        return switch (team.toLowerCase()) {
            case "blue" -> ultimateBingo.blueTeamInventory;
            case "red" -> ultimateBingo.redTeamInventory;
            case "yellow" -> ultimateBingo.yellowTeamInventory;
            default -> null; // or some default inventory
        };
    }

    // Method to send a message to all active players with the list of active players and their teams
    public void notifyActivePlayers(Player playerToSend) {
        List<Player> activePlayers = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder("Active players in the game:\n");

        // Build the list of active players and their teams
        Bukkit.getOnlinePlayers().forEach(player -> {
            String team = playerTeamsMap.get(player.getUniqueId());
            if (team != null) {
                net.md_5.bungee.api.ChatColor color;
                switch (team.toLowerCase()) {
                    case "blue":
                        color = net.md_5.bungee.api.ChatColor.BLUE;
                        break;
                    case "red":
                        color = net.md_5.bungee.api.ChatColor.RED;
                        break;
                    case "yellow":
                        color = net.md_5.bungee.api.ChatColor.YELLOW;
                        break;
                    default:
                        color = net.md_5.bungee.api.ChatColor.WHITE;
                        break;
                }
                messageBuilder.append(color).append(player.getName()).append(net.md_5.bungee.api.ChatColor.RESET).append(", ");
                activePlayers.add(player);
            }
        });

        // Trim last comma and space
        if (messageBuilder.length() > 2) {
            messageBuilder.setLength(messageBuilder.length() - 2);
        }

        String message = messageBuilder.toString();
        playerToSend.sendMessage(message);
    }

    public boolean isRedTeamNotEmpty() {
        return playerTeamsMap.containsValue("red");
    }

    public boolean isYellowTeamNotEmpty() {
        return playerTeamsMap.containsValue("yellow");
    }

    public boolean isBlueTeamNotEmpty() {
        return playerTeamsMap.containsValue("blue");

    }

    public String getRedTeamPlayerNames() {
        return getPlayerNamesByTeam("red");
    }

    public String getYellowTeamPlayerNames() {
        return getPlayerNamesByTeam("yellow");
    }

    public String getBlueTeamPlayerNames() {
        return getPlayerNamesByTeam("blue");
    }

    private String getPlayerNamesByTeam(String teamColor) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> teamColor.equals(playerTeamsMap.get(player.getUniqueId())))
                .map(Player::getName)
                .collect(Collectors.joining(", "));
    }

    public void copyInventoryContents(Inventory source, Inventory destination) {
        for (int i = 0; i < source.getSize(); i++) {
            destination.setItem(i, source.getItem(i) == null ? null : source.getItem(i).clone());
        }
    }

    //endregion
}
