package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LokiRoleDice extends CustomItem {

    private final RoleManager roleManager;
    private final Random random = new Random();
    private final MiniMessage mm = MiniMessage.miniMessage();

    public LokiRoleDice(RollAndDeathSMP plugin, RoleManager roleManager) {
        super(plugin, CustomItemType.LOKI_ROLE_DICE);
        this.roleManager = roleManager;
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    protected List<String> getLore() {
        return List.of(
                "Click derecho: cambia tu rol al azar",
                "No respeta cooldowns de reroll",
                "Consumible"
        );
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.name());
            Integer cmd = type.getCustomModelData();
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack hand = event.getItem();
        if (!isItem(hand)) return;
        event.setCancelled(true);

        if (roleManager == null) {
            event.getPlayer().sendMessage(mm.deserialize("<red>No se pudo cambiar el rol ahora."));
            return;
        }

        RoleType current = roleManager.getPlayerRole(event.getPlayer());
        RoleType[] pool = RoleType.values();
        if (pool.length == 0) {
            event.getPlayer().sendMessage(mm.deserialize("<red>No hay roles disponibles."));
            return;
        }

        RoleType chosen = pool[random.nextInt(pool.length)];
        if (pool.length > 1) {
            // Intenta evitar repetir el mismo rol
            for (int i = 0; i < pool.length * 2 && chosen == current; i++) {
                chosen = pool[random.nextInt(pool.length)];
            }
        }

        roleManager.setPlayerRole(event.getPlayer(), chosen);
        hand.setAmount(hand.getAmount() - 1);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.1f);
        event.getPlayer().sendMessage(mm.deserialize("<gold>Lanzaste el dado de rol y ahora eres <bold>" + chosen.getName() + "</bold>."));
    }
}
