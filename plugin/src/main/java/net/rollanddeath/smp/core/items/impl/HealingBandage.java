package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class HealingBandage extends CustomItem {

    public HealingBandage(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.HEALING_BANDAGE);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.PAPER);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Usa click derecho para curarte", "Restaura 4 corazones", "Consumible");
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (event.getItem() != null && isItem(event.getItem())) {
            if (event.getAction().isRightClick()) {
                Player player = event.getPlayer();
                player.setHealth(Math.min(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue(), player.getHealth() + 8.0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
        }
    }
}
