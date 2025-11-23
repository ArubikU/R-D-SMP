package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RegenerationTotem extends CustomItem {

    public RegenerationTotem(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.REGENERATION_TOTEM);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.TOTEM_OF_UNDYING);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Cura a todos los aliados en 10 bloques.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        event.setCancelled(true);
        
        // Consume item
        item.setAmount(item.getAmount() - 1);
        
        // Apply effects
        PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 2);
        player.addPotionEffect(regen);
        
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof Player) {
                ((Player) entity).addPotionEffect(regen);
                ((Player) entity).sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Has sido curado por el Tótem de Regeneración!"));
            }
        }
        
        player.getWorld().playSound(player.getLocation(), "item.totem.use", 1, 1);
    }
}
