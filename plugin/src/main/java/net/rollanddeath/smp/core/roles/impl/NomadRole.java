package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class NomadRole extends Role {

    private final NamespacedKey lastBedKey;
    private final NamespacedKey nomadDebuffKey;

    public NomadRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.NOMAD);
        this.lastBedKey = new NamespacedKey(plugin, "nomad_last_bed");
        this.nomadDebuffKey = new NamespacedKey(plugin, "nomad_debuff");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (!player.getPersistentDataContainer().has(nomadDebuffKey, PersistentDataType.BYTE)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        
        Player player = event.getPlayer();
        if (!hasRole(player)) return;

        Location bedLoc = event.getBed().getLocation();
        String locString = bedLoc.getWorld().getName() + "," + bedLoc.getBlockX() + "," + bedLoc.getBlockY() + "," + bedLoc.getBlockZ();
        
        String lastLoc = player.getPersistentDataContainer().get(lastBedKey, PersistentDataType.STRING);

        if (lastLoc != null && lastLoc.equals(locString)) {
            player.getPersistentDataContainer().set(nomadDebuffKey, PersistentDataType.BYTE, (byte) 1);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Has dormido en la misma cama! Pierdes tu velocidad de Nómada."));
            player.getWorld().createExplosion(bedLoc, 2.5f, false, false, player);
        } else {
            player.getPersistentDataContainer().set(lastBedKey, PersistentDataType.STRING, locString);
            player.getPersistentDataContainer().remove(nomadDebuffKey);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Cama nueva! Tu espíritu nómada se fortalece."));
        }
    }
}
