package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class GlassPickaxe extends CustomItem {

    public GlassPickaxe(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.GLASS_PICKAXE);
        // Keep haste refreshed while holding so insta-mine stays reliable.
        org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
                ItemStack main = player.getInventory().getItemInMainHand();
                if (isItem(main)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, 255, false, false, false));
                }
            }
        }, 20L, 20L);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.DIAMOND_PICKAXE);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Mina instantáneo pero tiene 10 usos.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isItem(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            // Diamond Pickaxe max durability ~1561. 1561 / 10 = ~156 damage per hit.
            int newDamage = damageable.getDamage() + 156;
            if (newDamage >= item.getType().getMaxDurability()) {
                player.getInventory().setItemInMainHand(null);
                player.playSound(player.getLocation(), "entity.item.break", 1, 1);
            } else {
                damageable.setDamage(newDamage);
                item.setItemMeta(meta);
            }
        }
    }
    
    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (isItem(newItem)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 60, 255, false, false, false));
        } else {
            if (player.hasPotionEffect(PotionEffectType.HASTE)) {
                 PotionEffect effect = player.getPotionEffect(PotionEffectType.HASTE);
                 if (effect != null && effect.getAmplifier() > 10) {
                     player.removePotionEffect(PotionEffectType.HASTE);
                 }
            }
        }
    }
}
