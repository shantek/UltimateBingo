package io.shantek.tools;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MaterialList {
    UltimateBingo ultimateBingo;
    public MaterialList(UltimateBingo megaBingo){
        this.ultimateBingo = megaBingo;
        materials = new HashMap<>();
        normal = new ArrayList<>();
        easy = new ArrayList<>();
        hard = new ArrayList<>();
        extreme = new ArrayList<>();
        impossible = new ArrayList<>();

        materials.put(1, easy);
        materials.put(2, normal);
        materials.put(3, hard);
        materials.put(4, extreme);
        materials.put(5, impossible);
    }
    Map<Integer, List<Material>> materials;
    public List<Material> easy;
    public List<Material> normal;
    public List<Material> hard;
    public List<Material> extreme;
    public List<Material> impossible;
    FileConfiguration materialConfig;
    File materialsFile;

    public void add(Material material, int difficulty){
        switch(difficulty){
            case 1:
                easy.add(material);
                break;
            case 2:
                normal.add(material);
                break;
            case 3:
                hard.add(material);
                break;
            case 4:
                extreme.add(material);
                break;
            case 5:
                impossible.add(material);
                break;
        }

    }

    public void removeItem(Material  material, int difficulty){
        switch(difficulty){
            case 1:
                easy.remove(material);
                break;
            case 2:
                normal.remove(material);
                break;
            case 3:
                hard.remove(material);
                break;
            case 4:
                extreme.remove(material);
                break;
            case 5:
                impossible.remove(material);
                break;
        }
        saveMaterialsToFile();
    }

    public void createMaterials(){

        File dataFolder = ultimateBingo.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        materialsFile = new File(dataFolder, "bingoitems.yml");
        if (!materialsFile.exists()) {
            try {
                materialsFile.createNewFile();
                addDefaultMaterials();
                saveMaterialsToFile();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to create bingoitems.yml!");
            }
        } else {
            materialConfig = YamlConfiguration.loadConfiguration(materialsFile);
            loadMaterialsFromFile();
        }


    }

    public void saveMaterialsToFile() {
        materialConfig = YamlConfiguration.loadConfiguration(materialsFile);
        try {
            materialConfig.set("materials.easy", easy.stream().map(Material::name).collect(Collectors.toList()));
            materialConfig.set("materials.normal", normal.stream().map(Material::name).collect(Collectors.toList()));
            materialConfig.set("materials.hard", hard.stream().map(Material::name).collect(Collectors.toList()));
            materialConfig.set("materials.extreme", extreme.stream().map(Material::name).collect(Collectors.toList()));
            materialConfig.set("materials.impossible", impossible.stream().map(Material::name).collect(Collectors.toList()));
            materialConfig.save(materialsFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Could not save bingoitems.yml");
        }
    }

    public void loadMaterialsFromFile() {
        materialConfig = YamlConfiguration.loadConfiguration(materialsFile);

        easy = materialConfig.getStringList("materials.easy").stream().map(Material::valueOf).collect(Collectors.toList());
        normal = materialConfig.getStringList("materials.normal").stream().map(Material::valueOf).collect(Collectors.toList());
        hard = materialConfig.getStringList("materials.hard").stream().map(Material::valueOf).collect(Collectors.toList());
        extreme = materialConfig.getStringList("materials.extreme").stream().map(Material::valueOf).collect(Collectors.toList());
        impossible = materialConfig.getStringList("materials.impossible").stream().map(Material::valueOf).collect(Collectors.toList());

        materials.put(1, easy);
        materials.put(2, normal);
        materials.put(3, hard);
        materials.put(4, extreme);
        materials.put(5, impossible);
    }


    public void addDefaultMaterials(){
        add(Material.IRON_INGOT, 1);
        add(Material.COAL,1);
        add(Material.OAK_PLANKS, 1);
        add(Material.SPRUCE_PLANKS,1);
        add(Material.BIRCH_PLANKS,1);
        add(Material.JUNGLE_PLANKS,1);
        add(Material.ACACIA_PLANKS,1);
        add(Material.DARK_OAK_PLANKS,1);
        add(Material.OAK_LOG,1);
        add(Material.SPRUCE_LOG,1);
        add(Material.BIRCH_LOG,1);
        add(Material.JUNGLE_LOG,1);
        add(Material.ACACIA_LOG,1);
        add(Material.DARK_OAK_LOG,1);
        add(Material.STRIPPED_OAK_LOG,1);
        add(Material.STRIPPED_SPRUCE_LOG,1);
        add(Material.STRIPPED_BIRCH_LOG,1);
        add(Material.STRIPPED_JUNGLE_LOG,1);
        add(Material.STRIPPED_ACACIA_LOG,1);
        add(Material.STRIPPED_DARK_OAK_LOG,1);
        add(Material.ROTTEN_FLESH,1);
        add(Material.BONE,1);
        add(Material.STRING,1);
        add(Material.GUNPOWDER,1);
        add(Material.FEATHER,1);
        add(Material.CHICKEN,1);
        add(Material.LEATHER,1);
        add(Material.BEEF,1);
        add(Material.PORKCHOP,1);
        add(Material.MUTTON,1);
        add(Material.COD,1);
        add(Material.SUGAR_CANE, 1);
        add(Material.BARREL, 1);
        add(Material.CHEST, 1);
        add(Material.BOWL, 1);

        add(Material.GOLD_INGOT, 2);
        add(Material.REDSTONE,2);
        add(Material.CRIMSON_PLANKS,2);
        add(Material.WARPED_PLANKS,2);
        add(Material.CRIMSON_STEM,2);
        add(Material.WARPED_STEM,2);
        add(Material.REDSTONE_TORCH,2);
        add(Material.PISTON,2);
        add(Material.SPIDER_EYE,2);
        add(Material.NOTE_BLOCK,2);
        add(Material.COOKED_CHICKEN,2);
        add(Material.EGG,2);
        add(Material.COOKED_PORKCHOP,2);
        add(Material.RABBIT,2);
        add(Material.TROPICAL_FISH,2);
        add(Material.BOOK,2);
        add(Material.CAULDRON, 2);
        add(Material.CACTUS, 2);
        add(Material.BEETROOT, 2);
        add(Material.LIGHTNING_ROD, 2);
        add(Material.RAIL, 2);
        add(Material.CHEST, 2);
        add(Material.SNOW_BLOCK, 2);
        add(Material.COCOA_BEANS, 2);
        add(Material.MINECART, 2);
        add(Material.GLASS, 2);

        add(Material.LAPIS_LAZULI,3);
        add(Material.QUARTZ,3);
        add(Material.REDSTONE_BLOCK,3);
        add(Material.OBSERVER,3);
        add(Material.ENDER_PEARL,3);
        add(Material.BOOKSHELF,3);
        add(Material.MOSS_BLOCK, 3);
        add(Material.BAMBOO, 3);
        add(Material.PUMPKIN, 3);
        add(Material.BELL, 3);
        add(Material.COOKED_RABBIT,3);
        add(Material.HONEYCOMB, 3);
        add(Material.POWERED_RAIL, 3);
        add(Material.DETECTOR_RAIL, 3);
        add(Material.DIAMOND_BOOTS, 3);
        add(Material.DIAMOND_SWORD, 3);
        add(Material.CAKE, 3);

        add(Material.DIAMOND, 4);
        add(Material.JUKEBOX,4);
        add(Material.ANVIL, 4);
        add(Material.AMETHYST_BLOCK, 4);
        add(Material.MELON, 4);
        add(Material.GOLDEN_APPLE, 4);
        add(Material.AXOLOTL_BUCKET, 4);
        add(Material.SPYGLASS, 4);
        add(Material.BLAZE_ROD,4);
        add(Material.SLIME_BALL,4);
        add(Material.PHANTOM_MEMBRANE,4);
        add(Material.ACTIVATOR_RAIL, 4);
        add(Material.TNT, 4);
        add(Material.CRYING_OBSIDIAN, 4);
        add(Material.DIAMOND_HELMET, 4);
        add(Material.GLOW_BERRIES, 4);
        add(Material.ENDER_PEARL, 4);

        add(Material.EMERALD,5);
        add(Material.NETHERITE_INGOT,5);
        add(Material.NETHERITE_SCRAP,5);
        add(Material.SLIME_BLOCK,5);
        add(Material.RABBIT_FOOT,5);
        add(Material.HEART_OF_THE_SEA,5);
        add(Material.HONEY_BLOCK, 5);
        add(Material.TOTEM_OF_UNDYING, 5);
        add(Material.TRIDENT, 5);
        add(Material.DIAMOND_CHESTPLATE, 5);
        add(Material.ENCHANTED_GOLDEN_APPLE, 5);
        add(Material.SADDLE, 5);
        add(Material.NAME_TAG, 5);

        saveMaterialsToFile();
    }
    public Map<Integer, List<Material>> getMaterials(){
        return materials;
    }

}
