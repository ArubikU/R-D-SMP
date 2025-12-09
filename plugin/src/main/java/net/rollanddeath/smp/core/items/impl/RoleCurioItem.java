package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoleCurioItem extends CustomItem {

    private final RoleType requiredRole;
    private final Material baseMaterial;
    private final List<PotionEffect> effects;
    private final List<PotionEffect> altEffects;
    private final List<String> extraLore;
    private final Sound sound;
    private final float pitch;
    private final Random random = new Random();

    public RoleCurioItem(RollAndDeathSMP plugin,
                         CustomItemType type,
                         RoleType requiredRole,
                         Material baseMaterial,
                         List<PotionEffect> effects,
                         List<PotionEffect> altEffects,
                         List<String> extraLore,
                         Sound sound,
                         float pitch) {
        super(plugin, type);
        this.requiredRole = requiredRole;
        this.baseMaterial = baseMaterial;
        this.effects = effects != null ? effects : Collections.emptyList();
        this.altEffects = altEffects != null ? altEffects : Collections.emptyList();
        this.extraLore = extraLore != null ? extraLore : Collections.emptyList();
        this.sound = sound;
        this.pitch = pitch;
    }

    @Override
    public RoleType getRequiredRoleType() {
        return requiredRole;
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(baseMaterial);
    }

    @Override
    protected List<String> getLore() {
        List<String> lore = new ArrayList<>(extraLore);
        lore.add("Consumible");
        return lore;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack stack = super.getItemStack();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            List<String> baseLore = getLore();
            if (!baseLore.isEmpty()) {
                meta.lore(baseLore.stream()
                        .map(line -> MiniMessage.miniMessage().deserialize("<!i><gray>" + line))
                        .toList());
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        event.setCancelled(true);

        List<PotionEffect> selectedEffects = effects;
        if (!altEffects.isEmpty() && random.nextBoolean()) {
            selectedEffects = altEffects;
        }

        for (PotionEffect effect : selectedEffects) {
            player.addPotionEffect(effect);
        }

        item.setAmount(item.getAmount() - 1);
        player.playSound(player.getLocation(), sound, 1f, pitch);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Has usado " + type.getDisplayName() + "."));
    }
}