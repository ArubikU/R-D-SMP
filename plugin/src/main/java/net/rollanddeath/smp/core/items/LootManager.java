package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class LootManager implements Listener {

    private final RollAndDeathSMP plugin;
    private final Random random = new Random();

    public LootManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity instanceof Zombie) {
            if (random.nextDouble() < 0.05) { // 5%
                dropItem(event, CustomItemType.HERMES_BOOTS);
            }
            if (entity.customName() != null && PlainTextComponentSerializer.plainText().serialize(entity.customName()).contains("Minero") && random.nextDouble() < 0.01) {
                dropItem(event, CustomItemType.GREED_PICKAXE);
            }
        }

        if (entity instanceof Witch) {
            if (random.nextDouble() < 0.10) {
                dropItem(event, CustomItemType.OBLIVION_POTION);
            }
            if (random.nextDouble() < 0.02) {
                dropItem(event, CustomItemType.DISCORD_APPLE);
            }
        }

        if (entity instanceof MagmaCube) {
            if (random.nextDouble() < 0.10) {
                dropItem(event, CustomItemType.XP_MAGNET);
            }
        }

        if (entity instanceof Drowned || entity instanceof Guardian) {
            if (random.nextDouble() < 0.01) {
                dropItem(event, CustomItemType.POSEIDON_TRIDENT);
            }
        }

        if (entity instanceof IronGolem) {
            if (random.nextDouble() < 0.05) {
                dropItem(event, CustomItemType.WAR_HAMMER);
            }
        }

        if (entity instanceof WitherSkeleton) {
            if (random.nextDouble() < 0.001) { // 0.1%
                dropItem(event, CustomItemType.VAMPIRE_SWORD);
            }
        }

        if (entity instanceof EnderDragon) {
            dropItem(event, CustomItemType.REAL_DRAGON_EGG);
        }
        
        if (entity instanceof Warden) {
             dropItem(event, CustomItemType.NOTCH_HEART);
        }
        
        if (entity instanceof Enderman) {
             if (random.nextDouble() < 0.005) {
                 dropItem(event, CustomItemType.VOID_CALL);
             }
        }
    }

    private void dropItem(EntityDeathEvent event, CustomItemType type) {
        CustomItem item = plugin.getItemManager().getItem(type);
        if (item != null) {
            event.getDrops().add(item.getItemStack());
        }
    }
}
