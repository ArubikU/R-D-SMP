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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VampireSword extends CustomItem {

    private static final long BURN_COOLDOWN_MS = 4000L;
    private final Map<UUID, Long> lastSunBurn = new HashMap<>();

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

        // Heal 30% of damage dealt for meaningful sustain
        double heal = event.getFinalDamage() * 0.3;
        double newHealth = Math.min(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue(), player.getHealth() + heal);
        player.setHealth(newHealth);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (isItem(item)) {
            if (player.getWorld().getTime() < 12300 || player.getWorld().getTime() > 23850) { // Day time
                if (player.getLocation().getBlock().getLightFromSky() == 15) {
                    long now = System.currentTimeMillis();
                    UUID id = player.getUniqueId();
                    long last = lastSunBurn.getOrDefault(id, 0L);
                    if (now - last >= BURN_COOLDOWN_MS) {
                        player.setFireTicks(40); // milder, ticks down while on cooldown
                        lastSunBurn.put(id, now);
                    }
                }
            }
        }
    }
}
