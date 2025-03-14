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

public class BingoManager {

    Map<UUID, List<ItemStack>> playerBingoCards;
    Map<UUID, Inventory> bingoGUIs;
    Map<UUID, Inventory> previousBingoGUIs;
    private int bingoCards;
    private UltimateBingo ultimateBingo;
    public int[] slots;
    public boolean started;
    private BingoCommand bingoCommand;

    public BingoManager(UltimateBingo ultimateBingo, BingoCommand bingoCommand) {
        this.ultimateBingo = ultimateBingo;
        this.bingoCommand = bingoCommand;
    }

    // Method to set the BingoCommand later
    public void setBingoCommand(BingoCommand bingoCommand) {
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
                Inventory bingoGUI = Bukkit.createInventory(null, 54, ChatColor.GREEN.toString() + ChatColor.BOLD + "Bingo" + " " + ChatColor.LIGHT_PURPLE + cardInfo);

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

    public void createGroupBingoCard() {
        started = true;

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


        // Store the string for the card type
        String cardInfo = "group";
        cardInfo += ultimateBingo.currentFullCard ? "/full card" : "/single row";
        cardInfo = "(" + cardInfo + ")";

        // Create a new inventory for each player
        ultimateBingo.groupInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN.toString() + ChatColor.BOLD + "Bingo" + " " + ChatColor.LIGHT_PURPLE + cardInfo);

        // Populate the card inventory with selected materials
        for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
            Material material = availableMaterials.get(i);
            ItemStack item = new ItemStack(material);
            ultimateBingo.groupInventory.setItem(slots[i], item);
        }

    }

    public void createTeamBingoCards() {
        started = true;

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


        // Store the string for the card type
        String cardInfo = ultimateBingo.currentUniqueCard ? "unique" : "identical";
        cardInfo += ultimateBingo.currentFullCard ? "/full card" : "/single row";
        cardInfo = "(" + cardInfo + ")";

        // Create a new inventory for each team
        ultimateBingo.redTeamInventory = Bukkit.createInventory(null, 54, ChatColor.RED.toString() + ChatColor.BOLD + "Bingo" + " " + ChatColor.LIGHT_PURPLE + cardInfo);
        ultimateBingo.blueTeamInventory = Bukkit.createInventory(null, 54, ChatColor.BLUE.toString() + ChatColor.BOLD + "Bingo" + " " + ChatColor.LIGHT_PURPLE + cardInfo);
        ultimateBingo.yellowTeamInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo" + " " + ChatColor.LIGHT_PURPLE + cardInfo);

        // Populate the red team card
        for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
            Material material = availableMaterials.get(i);
            ItemStack item = new ItemStack(material);
            ultimateBingo.redTeamInventory.setItem(slots[i], item);
        }

        // Add the Spyglass to the last slot if the feature is enabled
        if (ultimateBingo.currentRevealCards) {
            ultimateBingo.redTeamInventory.setItem(17, ultimateBingo.bingoFunctions.createSpyglass()); // Add Spyglass to slot 53 (last slot)
        }

        if (!ultimateBingo.currentUniqueCard) {
            // Cards are identical, copy this card over to Yellow and Blue
            ultimateBingo.bingoFunctions.copyInventoryContents(ultimateBingo.redTeamInventory, ultimateBingo.blueTeamInventory);
            ultimateBingo.bingoFunctions.copyInventoryContents(ultimateBingo.redTeamInventory, ultimateBingo.yellowTeamInventory);
        } else {
            // Cards are unique - shuffle the inventory and assign them

            // Populate the Yellow team card
            Collections.shuffle(availableMaterials);

            for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
                Material material = availableMaterials.get(i);
                ItemStack item = new ItemStack(material);
                ultimateBingo.yellowTeamInventory.setItem(slots[i], item);
            }

            // Add the Spyglass to the last slot if the feature is enabled
            if (ultimateBingo.currentRevealCards) {
                ultimateBingo.yellowTeamInventory.setItem(17, ultimateBingo.bingoFunctions.createSpyglass()); // Add Spyglass to slot 53 (last slot)
            }

            Collections.shuffle(availableMaterials);
            // Populate the Blue team card
            for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
                Material material = availableMaterials.get(i);
                ItemStack item = new ItemStack(material);
                ultimateBingo.blueTeamInventory.setItem(slots[i], item);
            }

            // Add the Spyglass to the last slot if the feature is enabled
            if (ultimateBingo.currentRevealCards) {
                ultimateBingo.blueTeamInventory.setItem(17, ultimateBingo.bingoFunctions.createSpyglass()); // Add Spyglass to slot 53 (last slot)
            }
        }

    }

    public boolean checkHasBingoCard(Player player) {
        UUID playerId = player.getUniqueId();
        if (ultimateBingo.gameMode.equalsIgnoreCase("group") || ultimateBingo.gameMode.equalsIgnoreCase("teams")) {
            return true;
        } else {
            return bingoGUIs.containsKey(playerId);
        }
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

        Inventory inv = null;

        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
            inv = ultimateBingo.groupInventory;
        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
            inv = ultimateBingo.bingoFunctions.getTeamInventory(player);
        } else {
            inv = getBingoGUIs().get(player.getUniqueId());
        }

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == completedMaterial) {
                item.setType(ultimateBingo.tickedItemMaterial);
                ItemMeta meta = item.getItemMeta();

                if (ultimateBingo.currentGameMode.equalsIgnoreCase("group") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                    meta.setDisplayName(ChatColor.GREEN + player.getName() + ": " + completedMaterial.name());
                } else {
                    meta.setDisplayName(ChatColor.GREEN + "Completed: " + completedMaterial.name());
                }

                item.setItemMeta(meta);

                // Top up their rockets if using the correct loadout
                ultimateBingo.bingoFunctions.topUpFirstFireworkRocketsStack(player);

                String removedUnderscore = completedMaterial.name().toLowerCase().replace('_', ' ');
                player.sendMessage(ChatColor.GREEN + "You ticked off " + ChatColor.GOLD + removedUnderscore + ChatColor.GREEN);

                if (ultimateBingo.currentGameMode.equals("speedrun") || ultimateBingo.currentGameMode.equals("group") || ultimateBingo.currentGameMode.equals("teams")) {
                    // Reset the player's stats
                    ultimateBingo.bingoFunctions.resetIndividualPlayer(player, false);
                }

                for (Player target : Bukkit.getOnlinePlayers()) {

                    if (ultimateBingo.bingoFunctions.isActivePlayer(target)) {

                        // PLAY FOR ALL PLAYERS
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 5);

                        if (!target.equals(player)) { // Exclude the player who triggered the event
                            if (ultimateBingo.currentRevealCards) {
                                if (ultimateBingo.gameMode.equalsIgnoreCase("group")) {
                                    target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " ticked off " + ChatColor.GREEN + removedUnderscore + ChatColor.WHITE + " from the group bingo card!");
                                } else if (ultimateBingo.gameMode.equalsIgnoreCase("teams")) {

                                    if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("red")) {

                                        target.sendMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " ticked off " + ChatColor.RED + removedUnderscore);

                                    } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("blue")) {

                                        target.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.WHITE + " ticked off " + ChatColor.BLUE + removedUnderscore);


                                    } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("yellow")) {

                                        target.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " ticked off " + ChatColor.YELLOW + removedUnderscore);
                                    }

                                } else {
                                    target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " ticked off " + ChatColor.GREEN + removedUnderscore + ChatColor.WHITE + " from their bingo card!");
                                }
                            } else {
                                target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.WHITE + " ticked off a bingo item.");
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

                    if (ultimateBingo.bingoFunctions.countActivePlayers() > 1 || ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
                        // All players get a win
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (ultimateBingo.bingoFunctions.isActivePlayer(target) && !target.equals(player)) {
                                ultimateBingo.getLeaderboard().addGameResult(
                                        target.getUniqueId(),
                                        cardSize,
                                        ultimateBingo.currentFullCard,
                                        ultimateBingo.currentDifficulty,
                                        ultimateBingo.currentGameMode,
                                        true
                                );
                            }
                        }
                    } else {
                        if (ultimateBingo.bingoFunctions.countActivePlayers() > 1 || ultimateBingo.countSoloGames) {
                            // Update leaderboard: player gets a win
                            ultimateBingo.getLeaderboard().addGameResult(
                                    player.getUniqueId(),
                                    cardSize,
                                    ultimateBingo.currentFullCard,
                                    ultimateBingo.currentDifficulty,
                                    ultimateBingo.currentGameMode,
                                    true
                            );

                            // Other active players get a non-win
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if (ultimateBingo.bingoFunctions.isActivePlayer(target) && !target.equals(player)) {
                                    ultimateBingo.getLeaderboard().addGameResult(
                                            target.getUniqueId(),
                                            cardSize,
                                            ultimateBingo.currentFullCard,
                                            ultimateBingo.currentDifficulty,
                                            ultimateBingo.currentGameMode,
                                            false
                                    );
                                }
                            }
                        }
                    }

                    if (ultimateBingo.gameMode.equalsIgnoreCase("group")) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " collected the last item! Well done, team!");
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (ultimateBingo.bingoFunctions.isActivePlayer(target)) {
                                target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                                target.sendTitle("BINGO!",
                                        ChatColor.GREEN.toString() + ChatColor.BOLD + "Woop woop!");
                            }
                        }
                    }
                        else if (ultimateBingo.gameMode.equalsIgnoreCase("teams")) {

                            if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("red")) {
                                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.RED + player.getName() + ChatColor.WHITE + " collected the last item! Well done, team " + ChatColor.RED + "RED" + ChatColor.WHITE + "!");

                            } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("yellow")) {
                                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.YELLOW + player.getName() + ChatColor.WHITE + " collected the last item! Well done, team " + ChatColor.YELLOW + "YELLOW" + ChatColor.WHITE + "!");

                            } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("blue")) {
                                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.BLUE + player.getName() + ChatColor.WHITE + " collected the last item! Well done, team " + ChatColor.BLUE + "BLUE" + ChatColor.WHITE + "!");

                            } else {
                                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " collected the last item! Well done, team!");

                            }

                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if (ultimateBingo.bingoFunctions.isActivePlayer(target)) {
                                    target.playSound(target.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0f, 1.0f);
                                    target.sendTitle(ultimateBingo.bingoFunctions.getTeam(player).toUpperCase() + " got BINGO!",
                                            ChatColor.GREEN.toString() + ChatColor.BOLD + "Woop woop!");
                                }
                            }
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO! Nice work!");
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (ultimateBingo.bingoFunctions.isActivePlayer(target)) {
                                target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 1.0f);
                                target.sendTitle(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO!",
                                        ChatColor.GREEN.toString() + ChatColor.BOLD + "Woop woop!");
                            }
                        }
                    }

                    ultimateBingo.bingoCommand.stopBingo(player, true);
                } else {

                    // Player doesn't have bingo - take any action we want while the game is still active


                    if (ultimateBingo.currentGameMode.equalsIgnoreCase("brewdash") && ultimateBingo.bingoFunctions.countActivePlayers() > 1) {

                        // Reset potion effects on current player
                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }

                        // Apply potion effect to all other players
                        if (ultimateBingo.currentDifficulty.equalsIgnoreCase("easy")) {
                            ultimateBingo.bingoFunctions.applyRandomNegativePotionToOtherPlayers(player, 20);
                        } else if (ultimateBingo.currentDifficulty.equalsIgnoreCase("normal")) {
                            ultimateBingo.bingoFunctions.applyRandomNegativePotionToOtherPlayers(player, 40);
                        } else {
                            ultimateBingo.bingoFunctions.applyRandomNegativePotionToOtherPlayers(player, 60);
                        }
                    }

                }
                break;
            }
        }
    }

    public void clearData() {
        if (bingoGUIs != null) {
            bingoGUIs.clear();
        }

        if (playerBingoCards != null) {
            playerBingoCards.clear();
        }

        // Clear any old team or group inventories
        ultimateBingo.groupInventory = null;
        ultimateBingo.redTeamInventory = null;
        ultimateBingo.blueTeamInventory = null;
        ultimateBingo.yellowTeamInventory = null;
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

        if (!ultimateBingo.currentGameMode.equalsIgnoreCase("group") && !ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

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

        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
            // Pick a team and assign them to it
            ultimateBingo.bingoFunctions.assignPlayerToActiveTeam(player);

            if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("red")) {

                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.RED + player.getName() + ChatColor.GREEN + " has just joined the " + ChatColor.RED + " RED " + ChatColor.GREEN + "team!");

            } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("blue")) {

                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " has just joined the " + ChatColor.BLUE + " BLUE " + ChatColor.GREEN + "team!");


            } else if (ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("yellow")) {

                ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has just joined the " + ChatColor.YELLOW + " YELLOW " + ChatColor.GREEN + "team!");

            }


        }
        if (!ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
            player.sendMessage(ChatColor.GREEN + "You joined the bingo game. Good luck!");

            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has just joined bingo!");

        }

        // Give them a bingo card
        ultimateBingo.bingoFunctions.giveBingoCard(player);

        // Give them their loadout gear
        ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);

        if (ultimateBingo.bingoStarted && ultimateBingo.bingoManager.checkHasBingoCard(player) && (ultimateBingo.currentGameMode.equals("speedrun") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams") || ultimateBingo.currentGameMode.equals("group"))) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
        }

        // Add them to the player list
        ultimateBingo.bingoFunctions.addPlayer(player.getUniqueId());


    }


}
