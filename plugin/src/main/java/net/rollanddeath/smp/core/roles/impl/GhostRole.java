package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class GhostRole extends Role {

    public GhostRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                            if (player.getAttribute(Attribute.MAX_HEALTH).getValue() != 10.0) {
                                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(10.0); // 5 hearts
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 220, 0, false, false, false));
                    } else {
                             if (player.getAttribute(Attribute.MAX_HEALTH).getValue() == 10.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.GHOST) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                        }
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && hasRole(event.getPlayer())) {
            Block block = event.getClickedBlock();
            if (block != null && (block.getType().name().contains("DOOR") || block.getType() == Material.IRON_DOOR)) {
                if (event.getPlayer().isSneaking()) {
                    if (block.getBlockData() instanceof Door) {
                        Door door = (Door) block.getBlockData();
                        if (!door.isOpen()) {
                            door.setOpen(true);
                            block.setBlockData(door);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }
        if (!hasRole(player)) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }

        if (ThreadLocalRandom.current().nextDouble() > 0.3) {
            event.setCancelled(true);
            mob.setTarget(null);
        }
    }
}
