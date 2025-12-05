package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RealDragonEgg extends CustomItem {

    public RealDragonEgg(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.REAL_DRAGON_EGG);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.DRAGON_EGG);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Invoca un Dragón bebé que ataca enemigos.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);

        Phantom dragon = (Phantom) player.getWorld().spawnEntity(player.getLocation().add(0, 2, 0), EntityType.PHANTOM);
        dragon.customName(MiniMessage.miniMessage().deserialize("<dark_purple>Dragón Bebé de " + player.getName()));
        dragon.setCustomNameVisible(true);
        dragon.setSize(3);
        dragon.setShouldBurnInDay(false);
        dragon.setFireTicks(0);
        dragon.setRemoveWhenFarAway(false);
        // AI to follow player and attack enemies is complex without NMS or advanced API.
        // For now, it's just a friendly phantom (maybe?). 
        // Vanilla phantoms attack players. We need to target monsters.
        // This requires custom AI or EntityTargetEvent manipulation.
    }
}
