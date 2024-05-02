package io.shantek.managers;

import io.shantek.UltimateBingo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ConfigFile {

    private UltimateBingo ultimateBingo;

    public ConfigFile(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public void checkforDataFolder() {
        if (!ultimateBingo.getDataFolder().exists()) {
            if (ultimateBingo.getDataFolder().mkdir()) {
                ultimateBingo.getLogger().info("Data folder created successfully.");
            } else {
                ultimateBingo.getLogger().warning("Failed to create data folder.");
            }
        }
    }

    public void reloadConfigFile() {
        try {
            ultimateBingo.getLogger().info("Reloading config file.");
            File configFile = new File(ultimateBingo.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                ultimateBingo.getLogger().info("Config file not found. Creating a new one...");
                saveDefaultConfig("config.yml", configFile);
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            boolean keysMissing = checkForMissingKeys(config);
            Map<String, Object> missingKeyValues = saveMissingKeyValues(config);
            saveDefaultConfig("config.yml", configFile);
            config = YamlConfiguration.loadConfiguration(configFile);
            updateConfigWithMissingKeyValues(config, missingKeyValues);

            config.save(configFile);  // Save once after all updates

            ultimateBingo.fullCard = getBoolean(config, "full-card", false);
            ultimateBingo.difficulty = getString(config, "difficulty", "normal");
            ultimateBingo.cardSize = getString(config, "card-size", "medium");
            ultimateBingo.uniqueCard = getBoolean(config, "unique-card", false);
            ultimateBingo.consoleLogs = getBoolean(config, "console-logs", true);
        } catch (Exception e) {
            ultimateBingo.getLogger().log(Level.SEVERE, "An error occurred while reloading the config file", e);
        }
    }

    private boolean checkForMissingKeys(FileConfiguration config) {
        List<String> keysToCheck = Arrays.asList(
                "full-card", "difficulty", "card-size", "unique-card", "console-logs");
        return keysToCheck.stream().anyMatch(key -> !config.contains(key));
    }

    private Map<String, Object> saveMissingKeyValues(FileConfiguration config) {
        List<String> keysToCheck = Arrays.asList(
                "full-card", "difficulty", "card-size", "unique-card", "console-logs");

        Map<String, Object> missingKeyValues = new HashMap<>();
        keysToCheck.forEach(key -> {
            if (config.contains(key)) {
                missingKeyValues.put(key, config.get(key));
            }
        });
        return missingKeyValues;
    }

    private void updateConfigWithMissingKeyValues(FileConfiguration config, Map<String, Object> missingKeyValues) {
        missingKeyValues.forEach(config::set);
    }

    private void saveDefaultConfig(String resourceName, File destination) throws IOException {
        try (InputStream resourceStream = getClass().getResourceAsStream("/" + resourceName)) {
            if (resourceStream != null) {
                Files.copy(resourceStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Resource stream for default config is null");
            }
        }
    }

    private String getString(FileConfiguration config, String key, String defaultValue) {
        if (config.contains(key) && config.isString(key)) {
            String value = config.getString(key).trim().replaceAll("\\s+", " ");
            config.set(key, value);
            return value;
        } else {
            config.set(key, defaultValue);
            return defaultValue;
        }
    }

    private boolean getBoolean(FileConfiguration config, String key, boolean defaultValue) {
        if (config.contains(key) && config.isBoolean(key)) {
            return config.getBoolean(key);
        } else {
            config.set(key, defaultValue);
            return defaultValue;
        }
    }

    public void saveConfig() {
        try {
            File configFile = new File(ultimateBingo.getDataFolder(), "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            // Update config values with current game state
            config.set("full-card", ultimateBingo.fullCard);
            config.set("difficulty", ultimateBingo.difficulty);
            config.set("card-size", ultimateBingo.cardSize);
            config.set("unique-card", ultimateBingo.uniqueCard);
            config.set("console-logs", ultimateBingo.consoleLogs);

            config.save(configFile);
        } catch (IOException e) {
            ultimateBingo.getLogger().log(Level.SEVERE, "An error occurred while saving the config file", e);
        }
    }

}
