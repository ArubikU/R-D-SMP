package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VampireSword extends CustomItem {

    public VampireSword(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.VAMPIRE_SWORD);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.NETHERITE_SWORD);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Roba vida, quema al sol.");
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!isItem(item)) return;

        // Heal 10% of damage dealt
        double heal = event.getFinalDamage() * 0.1;
        double newHealth = Math.min(player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue(), player.getHealth() + heal);
        player.setHealth(newHealth);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (isItem(item)) {
            if (player.getWorld().getTime() < 12300 || player.getWorld().getTime() > 23850) { // Day time
                if (player.getLocation().getBlock().getLightFromSky() == 15) {
                    player.setFireTicks(20);
                }
            }
        }
    }
}
