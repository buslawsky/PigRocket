package com.buslawsky.pigrocket;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PigRocketListener implements Listener {

    private final PigRocketPlugin plugin;
    private final Set<UUID> empoweredPlayers = new HashSet<>();
    private final Set<UUID> flyingPigs = new HashSet<>();

    public PigRocketListener(PigRocketPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_CARROT) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();

            empoweredPlayers.add(playerUUID);
            player.sendMessage("§e[PigRocket] §aZjadles zlota marchewke! Masz 30 sekund na nakarmienie swini ziemniakiem... 🧐");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (empoweredPlayers.contains(playerUUID)) {
                        empoweredPlayers.remove(playerUUID);
                        player.sendMessage("§e[PigRocket] §cMoc zlotej marchewki wygasla!");
                    }
                }
            }.runTaskLater(plugin, 600L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (clickedEntity instanceof Pig && empoweredPlayers.contains(player.getUniqueId())) {
            Pig pig = (Pig) clickedEntity;
            ItemStack itemInHand = player.getInventory().getItem(event.getHand());

            if (itemInHand != null && itemInHand.getType() == Material.POTATO) {
                event.setCancelled(true);

                itemInHand.setAmount(itemInHand.getAmount() - 1);
                empoweredPlayers.remove(player.getUniqueId());
                flyingPigs.add(pig.getUniqueId());

                player.sendMessage("§e[PigRocket] §dPROSIAK 5... 4... 3... START! 🚀");

                new BukkitRunnable() {
                    int ticks = 0;

                    @Override
                    public void run() {
                        if (pig.isDead() || ticks >= 100) {
                            flyingPigs.remove(pig.getUniqueId());
                            pig.getWorld().playSound(pig.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.0f);
                            this.cancel();
                            return;
                        }

                        pig.setVelocity(new Vector(0, 0.4, 0));
                        pig.getWorld().spawnParticle(Particle.FLAME, pig.getLocation(), 5, 0.1, 0.1, 0.1, 0.02);
                        pig.getWorld().spawnParticle(Particle.SMOKE_NORMAL, pig.getLocation(), 3, 0.1, 0.1, 0.1, 0.02);

                        if (ticks % 5 == 0) {
                            pig.getWorld().playSound(pig.getLocation(), Sound.ENTITY_CAT_HISS, 0.5f, 1.5f);
                        }

                        ticks++;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (flyingPigs.contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                flyingPigs.remove(event.getEntity().getUniqueId());
            }
        }
    }
}