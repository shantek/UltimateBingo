package io.shantek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import io.shantek.UltimateBingo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RespawnListener implements Listener {

    UltimateBingo ultimateBingo;

    public RespawnListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        boolean isActivePlayer = true;

        // Check if multi world bingo is enabled and they're in the bingo world
        if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
            isActivePlayer = false;
        }

        if (isActivePlayer || !ultimateBingo.multiWorldServer) {

            // Only teleport the player back to bingo spawn if enabled in the settings
            if (ultimateBingo.bingoStarted && ultimateBingo.respawnTeleport) {
                // Delay the sendTitle message by 10 ticks
                Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
                    player.sendTitle(ChatColor.YELLOW + "TELEPORTING", ChatColor.WHITE + "One Moment", 10, 40, 10);
                }, 10L); // Delay showing the title by 10 ticks

                // Delayed teleport to handle any asynchronous issues, total delay 3.5 seconds (70 ticks from respawn)
                Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
                    player.teleport(ultimateBingo.bingoSpawnLocation);
                    ultimateBingo.bingoFunctions.giveBingoCard(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

                    boolean keepInventory = player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);

                    // Equip them with fresh loadout gear after respawning, if keep inventory is off
                    if (!keepInventory) {
                        ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);
                    }

                    // Also give them night vision
                    if (ultimateBingo.currentGameMode.equals("speedrun")) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
                    }

                }, 70L); // Delay teleportation by 3.5 seconds (70 ticks) after respawn
            } else {

                // If they previously had a bingo card and died after the game was active, give them another card
                if (ultimateBingo.bingoManager.checkHasBingoCard(player)) {
                    ultimateBingo.bingoFunctions.giveBingoCard(player);
                }
            }
        }
    }
}