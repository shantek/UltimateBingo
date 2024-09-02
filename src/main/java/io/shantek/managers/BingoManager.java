package io.shantek.managers;

import io.shantek.BingoCommand;
import io.shantek.UltimateBingo;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BingoManager{

    Map<UUID, List<ItemStack>> playerBingoCards;
    Map<UUID, Inventory> bingoGUIs;
    Map<UUID, Inventory> previousBingoGUIs;
    private int bingoCards;
    private UltimateBingo ultimateBingo;
    public int[] slots;
    public boolean started;
    private BingoCommand bingoCommand;

    public BingoManager(UltimateBingo ultimateBingo, BingoCommand bingoCommand){
        this.ultimateBingo = ultimateBingo;
        this.bingoCommand = bingoCommand;
    }

    public void createBingoCards() {
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        // Determine difficulty level and set TOTAL_ITEMS based on it
        int difficultyLevel;
        switch (ultimateBingo.currentDifficulty.toLowerCase()) {
            case "normal":
                difficultyLevel = 2;
                TOTAL_ITEMS = 21;
                break;
            case "hard":
                difficultyLevel = 3;
                TOTAL_ITEMS = 30;
                break;
            default:
                difficultyLevel = 1; // Default to "easy"
                TOTAL_ITEMS = 14;
                break;
        }

        // Generate and shuffle materials for the card
        List<Material> availableMaterials = generateMaterials(difficultyLevel);
        Collections.shuffle(availableMaterials);

        // Get slots based on the card size
        int[] slots = determineSlotsBasedOnCardSize();

        // Distribute unique cards to each player
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {
                UUID playerId = player.getUniqueId();

                // Store the string for the card type
                String cardInfo = ultimateBingo.currentUniqueCard ? "unique" : "identical";
                cardInfo += ultimateBingo.currentFullCard ? "/full card" : "/single row";
                cardInfo = "(" + cardInfo + ")";

                // Create a new inventory for each player
                Inventory bingoGUI = Bukkit.createInventory(null, 54, ChatColor.GREEN.toString() + ChatColor.BOLD + "Bingo" + ChatColor.BLACK + " " + ChatColor.GOLD + cardInfo);

                // Populate the card inventory with selected materials
                for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
                    Material material = availableMaterials.get(i);
                    ItemStack item = new ItemStack(material);
                    bingoGUI.setItem(slots[i], item);
                }

                // Add the Spyglass to the last slot if the feature is enabled
                if (ultimateBingo.currentRevealCards) {
                    bingoGUI.setItem(17, ultimateBingo.bingoFunctions.createSpyglass()); // Add Spyglass to slot 53 (last slot)
                }
                bingoGUIs.put(playerId, bingoGUI);

                // Store the card for each player
                List<ItemStack> cards = new ArrayList<>();
                for (int slot : slots) {
                    ItemStack item = bingoGUI.getItem(slot);
                    if (item != null) {
                        cards.add(item);
                    }
                }
                playerBingoCards.put(playerId, cards);
            }
        }
    }

    public boolean checkHasBingoCard(Player player) {
        UUID playerId = player.getUniqueId();
        return bingoGUIs.containsKey(playerId);
    }

    public void createUniqueBingoCards() {
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        // Determine difficulty level and set TOTAL_ITEMS based on it
        int difficultyLevel;
        switch (ultimateBingo.currentDifficulty.toLowerCase()) {
            case "normal":
                difficultyLevel = 2;
                TOTAL_ITEMS = 21; // Set TOTAL_ITEMS for normal difficulty
                break;
            case "hard":
                difficultyLevel = 3;
                TOTAL_ITEMS = 30; // Set TOTAL_ITEMS for hard difficulty
                break;
            default:
                difficultyLevel = 1; // Default to "easy"
                TOTAL_ITEMS = 14; // Set TOTAL_ITEMS for easy difficulty
                break;
        }

        // Generate a single set of materials for all players
        List<Material> sharedMaterials = generateMaterials(difficultyLevel);

        // Distribute unique shuffled cards to each player
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                UUID playerId = player.getUniqueId();

                // Store the string for the card type
                String cardInfo = ultimateBingo.currentUniqueCard ? "unique" : "identical";
                cardInfo += ultimateBingo.currentFullCard ? "/full card" : "/single row";
                cardInfo = "(" + cardInfo + ")";

                // Create a new inventory for each player
                Inventory bingoGUI = Bukkit.createInventory(null, 54, ChatColor.GREEN.toString() + ChatColor.BOLD + "Bingo" + ChatColor.BLACK + " " + ChatColor.GOLD + cardInfo);

                // Shuffle the shared materials uniquely for each player
                List<Material> playerMaterials = new ArrayList<>(sharedMaterials);
                Collections.shuffle(playerMaterials);

                List<ItemStack> cards = new ArrayList<>();
                int[] slots = determineSlotsBasedOnCardSize(); // Determine slots based on card size

                // Populate the bingo GUI with shuffled materials
                for (int i = 0; i < slots.length && i < playerMaterials.size(); i++) {
                    Material material = playerMaterials.get(i);
                    ItemStack item = new ItemStack(material);
                    bingoGUI.setItem(slots[i], item);
                    cards.add(item);
                }

                // Add the Spyglass to the last slot if the feature is enabled
                if (ultimateBingo.currentRevealCards) {
                    bingoGUI.setItem(17, ultimateBingo.bingoFunctions.createSpyglass()); // Add Spyglass to slot 53 (last slot)
                }

                playerBingoCards.put(playerId, cards);
                bingoGUIs.put(playerId, bingoGUI);

            }
        }
    }

    private int[] determineSlotsBasedOnCardSize() {
        // Define slot arrangements for different card sizes
        int[] smallSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] mediumSlots = {10, 11, 12, 13, 19, 20, 21, 22, 28, 29, 30, 31, 37, 38, 39, 40};
        int[] largeSlots = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32, 37, 38, 39, 40, 41, 46, 47, 48, 49, 50};

        return switch (ultimateBingo.currentCardSize.toLowerCase()) {
            case "small" -> smallSlots;
            case "medium" -> mediumSlots;
            case "large" -> largeSlots;
            default -> mediumSlots; // Default to medium if something goes wrong
        };
    }

    private int TOTAL_ITEMS = 30;

    public List<Material> generateMaterials(int type) {
        Map<Integer, List<Material>> materials = ultimateBingo.getMaterialList().getMaterials();
        Random random = new Random();
        List<Material> generatedMaterials = new ArrayList<>();

        // Adjust the type if it is greater than 3 - Default to easy
        if (type > 3) {
            type = 1;
        }

        // Define the distribution of items across difficulties based on the type
        int[] distribution = switch (type) {
            case 1 -> new int[]{15, 15, 0, 0, 0};
            case 2 -> new int[]{5, 10, 10, 5, 0};
            case 3 -> new int[]{0, 5, 10, 10, 5};
            default -> new int[]{15, 15, 0, 0, 0};
        };

        // Generate materials based on the defined distribution
        for (int difficulty = 1; difficulty <= 5; difficulty++) {
            List<Material> difficultyMaterials = new ArrayList<>(materials.get(difficulty));
            int itemsToGenerate = distribution[difficulty - 1];
            for (int i = 0; i < itemsToGenerate && !difficultyMaterials.isEmpty(); i++) {
                int randomIndex = random.nextInt(difficultyMaterials.size());
                Material randomMaterial = difficultyMaterials.get(randomIndex);
                generatedMaterials.add(randomMaterial);
                difficultyMaterials.remove(randomIndex);
            }
        }

        // Ensure we always return exactly TOTAL_ITEMS materials
        while (generatedMaterials.size() < TOTAL_ITEMS) {
            List<Material> fallbackMaterials = materials.get(1);
            Material randomMaterial = fallbackMaterials.get(random.nextInt(fallbackMaterials.size()));
            generatedMaterials.add(randomMaterial);
        }

        return generatedMaterials;
    }

    public void markItemAsComplete(Player player, Material completedMaterial) {

        Inventory inv = getBingoGUIs().get(player.getUniqueId());
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == completedMaterial) {
                item.setType(ultimateBingo.tickedItemMaterial);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Completed: " + completedMaterial.name());
                item.setItemMeta(meta);

                // Top up their rockets if using the correct loadout
                ultimateBingo.bingoFunctions.topUpFirstFireworkRocketsStack(player);

                String removedUnderscore = completedMaterial.name().toLowerCase().replace('_', ' ');
                player.sendMessage(ChatColor.GREEN + "You ticked off " + ChatColor.GOLD + removedUnderscore + ChatColor.GREEN + " from your bingo card!");

                if (ultimateBingo.currentGameMode.equals("speedrun")) {
                    // Reset the players stats
                    ultimateBingo.bingoFunctions.resetIndividualPlayer(player, false);
                }

                for (Player target : Bukkit.getOnlinePlayers()) {

                    if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                        // PLAY FOR ALL PLAYERS
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 5);

                        if (!target.equals(player)) { // Exclude the player who triggered the event
                            if (ultimateBingo.currentRevealCards) {
                                target.sendMessage(ChatColor.GREEN + player.getName() + " ticked off " + ChatColor.GOLD + removedUnderscore + ChatColor.GREEN + " from their bingo card!");

                            } else {
                                target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GREEN + " ticked off a bingo item.");
                            }

                        }
                    }
                }

                // Check for bingo based on the card type and size
                String cardSize = ultimateBingo.currentCardSize;
                boolean hasBingo = false;

                // If it's a full card, we'll check the entire card instead
                if (ultimateBingo.currentFullCard) {
                  if (ultimateBingo.cardTypes.checkFullCard(player)) {
                      hasBingo = true;
                  }

                } else {

                    // Not a full card, check for traditional line bingo
                    switch (cardSize.toLowerCase()) {
                        case "small":
                            if (ultimateBingo.cardTypes.checkSmallCardBingo(player)) {
                                hasBingo = true;
                            }
                            break;
                        case "medium":
                            if (ultimateBingo.cardTypes.checkMediumCardBingo(player)) {
                                hasBingo = true;
                            }
                            break;
                        case "large":
                            if (ultimateBingo.cardTypes.checkLargeCardBingo(player)) {
                                hasBingo = true;
                            }
                            break;
                    }
                }
                if (hasBingo) {
                    // Disable the game
                    ultimateBingo.bingoStarted = false;

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO! Nice work!");
                    for (Player target : Bukkit.getOnlinePlayers()){

                        if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {
                            target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 1.0f);

                            target.sendTitle(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO!"
                                    , ChatColor.GREEN.toString() + ChatColor.BOLD + "Woop woop!");
                        }
                    }
                    ultimateBingo.bingoCommand.stopBingo(player, true);
                }
                break;
            }
        }
    }

    public void clearData(){
        if (bingoGUIs != null) {
            bingoGUIs.clear();
        }

        if (playerBingoCards != null) {
            playerBingoCards.clear();
        }
    }

    public void setBingoCards(int amount) {
        bingoCards = amount;
    }

    public Map<UUID, Inventory> getBingoGUIs() {
        return bingoGUIs;
    }

    public Map<UUID, List<ItemStack>> getPlayerBingoCards() {
        return playerBingoCards;
    }

    public int[] getSlots() {
        return slots;
    }

    public boolean isStarted() {
        return started;
    }

    public void joinGameInProgress(Player player) {
        UUID playerId = player.getUniqueId();

        // Check if the player already has a Bingo card
        if (bingoGUIs.containsKey(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "You already have a Bingo card.");
            return;
        }

        if (playerBingoCards.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No Bingo cards are available to clone. Please wait for the next round.");
            return;
        }

        // Find the card with the fewest ticked off items
        UUID idOfLeastTickedCard = null;
        int fewestTickedItems = Integer.MAX_VALUE;
        for (Map.Entry<UUID, List<ItemStack>> entry : playerBingoCards.entrySet()) {
            int tickedItemsCount = ultimateBingo.bingoFunctions.countTickedItems(entry.getValue());
            if (tickedItemsCount < fewestTickedItems) {
                fewestTickedItems = tickedItemsCount;
                idOfLeastTickedCard = entry.getKey();
            }
        }

        if (idOfLeastTickedCard == null) {
            player.sendMessage(ChatColor.RED + "No suitable Bingo card found.");
            return;
        }

        // Clone the Bingo GUI and card list
        Inventory originalGui = bingoGUIs.get(idOfLeastTickedCard);
        Inventory clonedGui = ultimateBingo.bingoFunctions.cloneInventory(originalGui);
        List<ItemStack> clonedCardList = new ArrayList<>(playerBingoCards.get(idOfLeastTickedCard));

        // Assign the cloned GUI and card list to the new player
        bingoGUIs.put(playerId, clonedGui);
        playerBingoCards.put(playerId, clonedCardList);

        player.sendMessage(ChatColor.GREEN + "You've been given an in-progress bingo card, good luck!");
        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has just joined bingo!");

        // Give them a bingo card
        ultimateBingo.bingoFunctions.giveBingoCard(player);

        // Give them their loadout gear
        ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);

        if (ultimateBingo.bingoStarted && ultimateBingo.bingoManager.checkHasBingoCard(player) && ultimateBingo.currentGameMode.equals("speedrun")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
        }

    }



}
