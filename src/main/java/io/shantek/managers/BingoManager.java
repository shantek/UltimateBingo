package io.shantek.managers;

import io.shantek.BingoCommand;
import io.shantek.UltimateBingo;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BingoManager{

    Map<UUID, List<ItemStack>> playerBingoCards;
    Map<UUID, Inventory> bingoGUIs;
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

        // Generate and shuffle materials for the card
        List<Material> availableMaterials = generateMaterials(1);
        Collections.shuffle(availableMaterials);

        // Get slots based on the card size
        int[] slots = determineSlotsBasedOnCardSize();

        // Distribute unique cards to each player
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();

            // Create a new inventory for each player
            Inventory bingoGUI = Bukkit.createInventory(null, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ultimate Bingo");

            // Populate the card inventory with selected materials
            for (int i = 0; i < slots.length && i < availableMaterials.size(); i++) {
                Material material = availableMaterials.get(i);
                ItemStack item = new ItemStack(material);
                bingoGUI.setItem(slots[i], item);
            }

            // Open the unique card inventory for each player
            player.openInventory(bingoGUI);
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

    public void createUniqueBingoCards() {
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        // Generate a single set of materials for all players
        List<Material> sharedMaterials = generateMaterials(1);

        // Distribute unique shuffled cards to each player
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            Inventory bingoGUI = Bukkit.createInventory(player, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ultimate Bingo");

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

            playerBingoCards.put(playerId, cards);
            bingoGUIs.put(playerId, bingoGUI);
            player.openInventory(bingoGUI);
        }
    }

    private int[] determineSlotsBasedOnCardSize() {
        // Define slot arrangements for different card sizes
        int[] smallSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        int[] mediumSlots = {10, 11, 12, 13, 19, 20, 21, 22, 28, 29, 30, 31, 37, 38, 39, 40};
        int[] largeSlots = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32, 37, 38, 39, 40, 41, 46, 47, 48, 49, 50};

        return switch (ultimateBingo.cardSize.toLowerCase()) {
            case "small" -> smallSlots;
            case "medium" -> mediumSlots;
            case "large" -> largeSlots;
            default -> mediumSlots; // Default to medium if something goes wrong
        };
    }



    private static final int TOTAL_ITEMS = 30;

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
            case 1 -> new int[]{15, 10, 5, 0, 0};
            case 2 -> new int[]{10, 10, 5, 5, 0};
            case 3 -> new int[]{10, 5, 5, 5, 5};
            default -> new int[]{15, 10, 5, 0, 0};
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
                item.setType(Material.LIME_CONCRETE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + "Completed: " + completedMaterial.name());
                item.setItemMeta(meta);

                String removedUnderscore = completedMaterial.name().toLowerCase().replace('_', ' ');
                player.sendMessage(ChatColor.GREEN + "You completed the " + ChatColor.GOLD + removedUnderscore + ChatColor.GREEN + " item in your bingo card!");

                for (Player target : Bukkit.getOnlinePlayers()) {
                    // PLAY FOR ALL PLAYERS
                    target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 5);

                    if (!target.equals(player)) { // Exclude the player who triggered the event
                        target.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GREEN + " ticked off a bingo item.");
                    }
                }

                // Check for bingo based on the card type and size
                String cardSize = ultimateBingo.cardSize;
                boolean hasBingo = false;

                // If it's a full card, we'll check the entire card instead
                if (ultimateBingo.fullCard) {
                  if (ultimateBingo.cardTypes.checkFullCard(player)) {
                      hasBingo = true;
                  }

                } else {

                    // Not a full card, check for traditional line bingo
                    switch (cardSize) {
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
                    Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO! Nice work!");
                    for (Player target : Bukkit.getOnlinePlayers()){
                        target.sendTitle(ChatColor.GOLD + player.getName() + ChatColor.GREEN +  " got BINGO!"
                                , ChatColor.GREEN.toString() + ChatColor.BOLD + "GG");
                    }

                    ultimateBingo.bingoCommand.stopBingo(player, true);

                }

                break;
            }
        }
    }

    public void clearData(){
        bingoGUIs.clear();
        playerBingoCards.clear();
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

}
