package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThiefRole extends Role {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public ThiefRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.THIEF);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false)); // Speed II
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onSteal(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player target) {
            Player thief = event.getPlayer();
            if (hasRole(thief) && thief.isSneaking()) {
                if (cooldowns.containsKey(thief.getUniqueId()) && cooldowns.get(thief.getUniqueId()) > System.currentTimeMillis()) {
                    long remaining = (cooldowns.get(thief.getUniqueId()) - System.currentTimeMillis()) / 1000;
                    thief.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cooldown: " + remaining + "s"));
                    return;
                }

                ItemStack[] contents = target.getInventory().getContents();
                int randomSlot = (int) (Math.random() * contents.length);
                ItemStack item = contents[randomSlot];

                if (item != null && item.getType() != Material.AIR) {
                    target.getInventory().setItem(randomSlot, null);
                    thief.getInventory().addItem(item);
                    thief.sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Has robado " + item.getType().name() + "!"));
                    target.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Te han robado algo!"));
                    cooldowns.put(thief.getUniqueId(), System.currentTimeMillis() + 300000); // 5 min
                } else {
                    thief.sendMessage(MiniMessage.miniMessage().deserialize("<red>No pudiste robar nada."));
                }
            }
        }
    }
}
