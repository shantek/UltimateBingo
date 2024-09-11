package io.shantek.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import io.shantek.UltimateBingo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamageListener implements Listener {

    UltimateBingo ultimateBingo;

    public EntityDamageListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                // Only teleport the player back to bingo spawn if enabled in the settings
                if (ultimateBingo.bingoStarted && ultimateBingo.respawnTeleport) {

                    // Check if the damage would kill the player
                    if (player.getHealth() - event.getFinalDamage() <= 0) {

                        // Prevent the death by canceling the damage event
                        event.setCancelled(true);

                        // Clear their inventory and teleport them back to the spawn area
                        ultimateBingo.bingoFunctions.resetIndividualPlayer(player, true);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        player.teleport(ultimateBingo.bingoSpawnLocation);

                        // Give them a bingo card and the bingo loadout
                        ultimateBingo.bingoFunctions.giveBingoCard(player);
                        ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);

                        // Also give them night vision
                        if (ultimateBingo.currentGameMode.equals("speedrun")) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
                        }

                        // Broadcast message to all active players
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(
                                ChatColor.RED + player.getName() + " died and has been sent back to the Bingo spawn."
                        );

                    }

                }


            }
        }
    }
}
