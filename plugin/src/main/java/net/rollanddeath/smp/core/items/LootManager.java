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
        // Nuevo diseño: todos los drops especiales se definen por mob custom (mobs.yml / ScriptedMobs).
        // Para evitar que mobs vanilla dropeen ítems custom, no inyectamos drops aquí.
    }

    private void dropItem(EntityDeathEvent event, String customItemId) {
        CustomItem item = plugin.getItemManager().getItem(customItemId);
        if (item != null) {
            event.getDrops().add(item.getItemStack());
        }
    }
}
