package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MidasRole extends Role {

    private static final UUID BONUS_HEALTH_MODIFIER_ID = UUID.fromString("b2c7483c-742c-4e16-9c7f-6c6a9c5db8f0");
    private final Map<UUID, Integer> bonusHearts = new HashMap<>();

    public MidasRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.MIDAS);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        consumeGold(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 600L, 600L); // Every 30 seconds (600 ticks)
    }

    private void consumeGold(Player player) {
        UUID uuid = player.getUniqueId();
        int currentBonusHearts = bonusHearts.getOrDefault(uuid, 0);
        boolean consumedBlock = false;
        boolean consumedAny = false;
        int regenDuration = 0;
        int regenAmplifier = 0;

        if (removeItem(player, Material.GOLD_BLOCK, 1)) {
            consumedAny = true;
            consumedBlock = true;
            currentBonusHearts = Math.min(4, currentBonusHearts + 1);
            regenDuration = 400; // 20s
            regenAmplifier = 1; // Regen II
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Has consumido 1 Bloque de Oro. Salud extra: " + currentBonusHearts + " ❤"));
        } else {
            // Si no comió bloque en este ciclo, pierde los corazones extra
            currentBonusHearts = 0;
        }

        if (!consumedBlock) {
            if (removeItem(player, Material.GOLD_NUGGET, 1)) {
                consumedAny = true;
                regenDuration = 120; // 6s
                regenAmplifier = 0; // Regen I
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Has consumido 1 Pepita de Oro para sobrevivir."));
            } else if (removeItem(player, Material.GOLD_INGOT, 1)) {
                consumedAny = true;
                regenDuration = 240; // 12s
                regenAmplifier = 1; // Regen II
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Has consumido 1 Lingote de Oro para sobrevivir."));
            }
        }

        if (consumedAny && regenDuration > 0) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, regenDuration, regenAmplifier, false, false));
        } else if (!consumedAny) {
            player.damage(2.0); // 1 corazón
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Necesitas oro para vivir! Te debilitas..."));
        }

        bonusHearts.put(uuid, currentBonusHearts);
        applyBonusHearts(player, currentBonusHearts);
    }

    private boolean removeItem(Player player, Material type, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != type) continue;
            int current = item.getAmount();
            if (current >= amount) {
                item.setAmount(current - amount);
                return true;
            } else {
                item.setAmount(0);
                amount -= current;
            }
        }
        return false;
    }

    private void applyBonusHearts(Player player, int hearts) {
        AttributeInstance instance = player.getAttribute(Attribute.MAX_HEALTH);
        if (instance == null) return;

        // Clear previous modifier
        AttributeModifier existing = instance.getModifier(BONUS_HEALTH_MODIFIER_ID);
        if (existing != null) {
            instance.removeModifier(existing);
        }

        if (hearts > 0) {
            AttributeModifier bonus = new AttributeModifier(
                    BONUS_HEALTH_MODIFIER_ID,
                    "midas_gold_bonus_hearts",
                    hearts * 2.0,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            instance.addModifier(bonus);
        }

        double newMax = instance.getValue();
        if (player.getHealth() > newMax) {
            player.setHealth(newMax);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onNaturalRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            // Revert the exhaustion added by vanilla regen so comida no baje al cancelar
            float currentExhaustion = player.getExhaustion();
            player.setExhaustion(Math.max(0f, currentExhaustion - 6f));
            event.setCancelled(true); // sin regeneración natural
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasRole(player)) {
                ItemStack item = event.getItem().getItemStack();
                if (item.getType() == Material.IRON_INGOT || item.getType() == Material.COPPER_INGOT) {
                    event.getItem().setItemStack(new ItemStack(Material.GOLD_INGOT, item.getAmount()));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>¡Tu toque convierte el metal en oro!"));
                } else if (item.getType() == Material.COBBLESTONE || item.getType() == Material.STONE) {
                    if (Math.random() < 0.1) {
                        event.getItem().setItemStack(new ItemStack(Material.GOLD_NUGGET, item.getAmount()));
                    }
                }
            }
        }
    }
}
