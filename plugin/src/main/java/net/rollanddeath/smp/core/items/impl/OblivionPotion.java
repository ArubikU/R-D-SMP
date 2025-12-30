package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.List;

public class OblivionPotion extends CustomItem {

    private static final long FORGET_MILLIS = 30_000L;
    private final Map<UUID, Long> protectedPlayers = new HashMap<>();

    public OblivionPotion(RollAndDeathSMP plugin) {
        super(plugin, "OBLIVION_POTION");
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.POTION);
    }

    @Override
    public String getDisplayName() {
        return "Poción de Olvido";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710009;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Borra tu memoria (y tu XP)", "Te deja ciego y débil por un tiempo");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.BLACK);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 0), true);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 1), true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (isItem(event.getItem())) {
            Player player = event.getPlayer();
            player.setLevel(0);
            player.setExp(0);

            long expiresAt = System.currentTimeMillis() + FORGET_MILLIS;
            protectedPlayers.put(player.getUniqueId(), expiresAt);

            // Immediately clear nearby mob targets.
            for (LivingEntity entity : player.getLocation().getNearbyLivingEntities(25)) {
                if (entity instanceof Mob mob && player.equals(mob.getTarget())) {
                    mob.setTarget(null);
                }
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        Long expires = protectedPlayers.get(player.getUniqueId());
        if (expires == null) return;
        if (System.currentTimeMillis() > expires) {
            protectedPlayers.remove(player.getUniqueId());
            return;
        }
        event.setCancelled(true);
    }
}
