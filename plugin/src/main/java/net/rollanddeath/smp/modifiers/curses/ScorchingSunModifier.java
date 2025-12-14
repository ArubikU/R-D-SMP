package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class ScorchingSunModifier extends Modifier {

    private BukkitRunnable task;
    private int tickCounter = 0;

    public ScorchingSunModifier(JavaPlugin plugin) {
        super(plugin, "Sol Abrasador", ModifierType.CURSE, "El sol quema a los jugadores expuestos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tickCounter++;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!isExposedToSun(player)) continue;

                    ItemStack helmet = player.getInventory().getHelmet();
                    if (helmet != null && isProtectiveHelmet(helmet.getType())) {
                        // Apply durability loss only every 4 seconds (~80 ticks) so it lasts longer
                        if (tickCounter % 4 == 0) {
                            tickHelmetDurability(player, helmet);
                        }
                        continue;
                    }
                    player.setFireTicks(60); // Burn for 3 seconds (refreshed every 1s)
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private boolean isExposedToSun(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        
        long time = world.getTime();
        boolean isDay = time < 12300 || time > 23850;
        if (!isDay) return false;

        if (world.hasStorm()) return false;

        Block block = player.getLocation().getBlock();
        return block.getLightFromSky() == 15;
    }

    private void tickHelmetDurability(Player player, ItemStack helmet) {
        var meta = helmet.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return; // Unbreakable or not damageable
        }

        int max = helmet.getType().getMaxDurability();
        if (max <= 0) return;

        int unbreaking = helmet.getEnchantmentLevel(Enchantment.UNBREAKING);

        int currentDamage = damageable.getDamage();
        int remaining = max - currentDamage;

        if (remaining <= 1) {
            // Drop with last pip of durability and clear slot
            ItemStack drop = helmet.clone();
            drop.editMeta(m -> {
                if (m instanceof Damageable d) {
                    d.setDamage(Math.max(max - 1, 0));
                }
            });
            player.getInventory().setHelmet(null);
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
            return;
        }

        // Vanilla Unbreaking check: chance to negate damage is level/(level+1)
        boolean takeDamage = true;
        if (unbreaking > 0) {
            takeDamage = ThreadLocalRandom.current().nextInt(unbreaking + 1) == 0;
        }

        if (takeDamage) {
            damageable.setDamage(Math.min(max - 1, currentDamage + 1));
            helmet.setItemMeta(meta);
        }
    }

    private boolean isProtectiveHelmet(Material type) {
        if (type == Material.AIR) return false;
        String name = type.name();
        return name.endsWith("_HELMET");
    }
}
