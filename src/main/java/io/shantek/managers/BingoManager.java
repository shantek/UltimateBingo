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

    public void createUniqueBingoCards() {
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            Inventory bingoGUI = Bukkit.createInventory(player, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo");

            List<ItemStack> cards = new ArrayList<>();

            Set<Material> selectedMaterials = new HashSet<>();
            List<Material> availableMaterials = generateMaterials();

            while (selectedMaterials.size() < slots.length && !availableMaterials.isEmpty()) {
                Collections.shuffle(availableMaterials);

                Material material = availableMaterials.remove(0);

                if (!selectedMaterials.contains(material)) {
                    selectedMaterials.add(material);
                    ItemStack item = new ItemStack(material);
                    bingoGUI.setItem(slots[selectedMaterials.size() - 1], item); // -1 because list is 0-indexed
                    cards.add(item);
                }
            }

            playerBingoCards.put(playerId, cards);

            player.openInventory(bingoGUI);
            bingoGUIs.put(playerId, bingoGUI);
        }
    }



    public void createBingoCards() {
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        // Check for bingo based on the card type and size
        String cardSize = ultimateBingo.cardSize;
        boolean hasBingo = false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            Inventory bingoGUI = Bukkit.createInventory(player, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo");

            List<ItemStack> cards = new ArrayList<>();

            Set<Material> selectedMaterials = new HashSet<>();
            List<Material> availableMaterials = generateMaterials();

            // Debug: Print all available materials
            System.out.println("Available materials: " + availableMaterials);

            while (selectedMaterials.size() < slots.length && !availableMaterials.isEmpty()) {
                Collections.shuffle(availableMaterials);

                Material material = availableMaterials.remove(0);

                if (!selectedMaterials.contains(material)) {
                    selectedMaterials.add(material);
                    ItemStack item = new ItemStack(material);
                    bingoGUI.setItem(slots[selectedMaterials.size() - 1], item); // -1 because list is 0-indexed
                    cards.add(item);

                    // Debug: Print the slot and the item being assigned to it
                    System.out.println("Assigning item " + item.getType() + " to slot " + slots[selectedMaterials.size() - 1]);
                }
            }

            playerBingoCards.put(playerId, cards);
            player.openInventory(bingoGUI);
            bingoGUIs.put(playerId, bingoGUI);
        }
    }

    public List<Material> generateMaterials(){
        Map<Integer, List<Material>> materials = ultimateBingo.getMaterialList().getMaterials();
        Random random = new Random();

        List<Material> generatedMaterials = new ArrayList<>();

        int[] percentages = {20, 20, 20, 20, 20};

        for(int difficulty = 1; difficulty <= 5; difficulty++){
            int numCards = (int)(bingoCards * (percentages[difficulty-1] / 100.0));

            List<Material> difficultyMaterials = new ArrayList<>(materials.get(difficulty));

            for(int i = 0; i < numCards && !difficultyMaterials.isEmpty(); i++){
                int randomIndex = random.nextInt(difficultyMaterials.size());
                Material randomMaterial = difficultyMaterials.get(randomIndex);
                generatedMaterials.add(randomMaterial);

                difficultyMaterials.remove(randomIndex);
            }
        }

        while (generatedMaterials.size() < bingoCards) {
            List<Material> easiestMaterials = materials.get(1);
            Material randomMaterial = easiestMaterials.get(random.nextInt(easiestMaterials.size()));
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

                if (hasBingo) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.GREEN + " got BINGO! Nice work!");
                    for (Player target : Bukkit.getOnlinePlayers()){
                        target.sendTitle(ChatColor.GOLD + player.getName() + ChatColor.GREEN +  " got BINGO!"
                                , ChatColor.GREEN.toString() + ChatColor.BOLD + "GG");
                    }

                    bingoCommand.endGame();
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
