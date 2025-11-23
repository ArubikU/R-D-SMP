package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class BoneBow extends CustomItem {

    private final Random random = new Random();

    public BoneBow(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.BONE_BOW);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.BOW);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Dispara flechas torcidas pero hace más daño.");
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!isItem(event.getBow())) return;

        if (event.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getProjectile();
            Vector velocity = arrow.getVelocity();
            double spread = 0.2;
            velocity.add(new Vector(
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread,
                (random.nextDouble() - 0.5) * spread
            ));
            arrow.setVelocity(velocity);
            arrow.addScoreboardTag("rd_bone_bow_arrow");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getScoreboardTags().contains("rd_bone_bow_arrow")) {
                event.setDamage(event.getDamage() * 1.5);
            }
        }
    }
}
