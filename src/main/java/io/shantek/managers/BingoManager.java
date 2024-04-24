package io.shantek.managers;

import io.shantek.BingoCommand;
import io.shantek.UltimateBingo;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class BingoManager{

    Map<UUID, List<ItemStack>> playerBingoCards;
    Map<UUID, Inventory> bingoGUIs;
    private int bingoCards;
    private UltimateBingo ultimateBingo;
    private int[] slots;
    public boolean started;

    private BingoCommand bingoCommand;

    public BingoManager(UltimateBingo ultimateBingo, BingoCommand bingoCommand){
        this.ultimateBingo = ultimateBingo;
        this.bingoCommand = bingoCommand;
    }

    public void createUniqueBingoCards(){
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        slots = new int[]{10,11,12,13,19,20,21,22,28,29,30,31,37,38,39,40};

        for (Player player : Bukkit.getOnlinePlayers()){
            UUID playerId = player.getUniqueId();
            Inventory bingoGUI = Bukkit.createInventory(player, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ultimate Bingo");

            List<ItemStack> cards = new ArrayList<>();
            List<Material> generatedMaterials = generateMaterials();

            Collections.shuffle(generatedMaterials);

            for(int i = 0; i < generatedMaterials.size() && i < slots.length; i++) {
                Material material = generatedMaterials.get(i);
                ItemStack item = new ItemStack(material);
                bingoGUI.setItem(slots[i], item);
            }

            playerBingoCards.put(playerId, cards);
            for (ItemStack card : playerBingoCards.get(playerId)){
                System.out.println(card);
            }


            player.openInventory(bingoGUI);
            bingoGUIs.put(playerId, bingoGUI);
        }
    }
    public void createBingoCards(){
        started = true;
        playerBingoCards = new HashMap<>();
        bingoGUIs = new HashMap<>();

        slots = new int[]{10,11,12,13,19,20,21,22,28,29,30,31,37,38,39,40};

        // Generate materials and shuffle them
        List<Material> generatedMaterials = generateMaterials();
        Collections.shuffle(generatedMaterials);

        for (Player player : Bukkit.getOnlinePlayers()){
            UUID playerId = player.getUniqueId();
            Inventory bingoGUI = Bukkit.createInventory(player, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ultimate Bingo");

            List<ItemStack> cards = new ArrayList<>();

            for(int i = 0; i < generatedMaterials.size() && i < slots.length; i++) {
                Material material = generatedMaterials.get(i);
                ItemStack item = new ItemStack(material);
                bingoGUI.setItem(slots[i], item);
                cards.add(item);  // Add item to cards list
            }

            playerBingoCards.put(playerId, cards);
            for (ItemStack card : playerBingoCards.get(playerId)){
                System.out.println(card);
            }

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



                if(checkForBingo(player)){
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

    public boolean checkForBingo(Player player) {
        UUID playerId = player.getUniqueId();
        Inventory inv = getBingoGUIs().get(playerId);

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
