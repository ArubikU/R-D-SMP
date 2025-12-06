package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class ChanceTotem extends CustomItem {

    private final Random random = new Random();
    private final PotionEffectType[] effects = {
        PotionEffectType.SPEED,
        PotionEffectType.STRENGTH,
        PotionEffectType.REGENERATION,
        PotionEffectType.ABSORPTION,
        PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.INVISIBILITY,
        PotionEffectType.NIGHT_VISION,
        PotionEffectType.JUMP_BOOST,
        PotionEffectType.SLOWNESS,
        PotionEffectType.WEAKNESS,
        PotionEffectType.POISON,
        PotionEffectType.BLINDNESS
    };

    public ChanceTotem(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.CHANCE_TOTEM);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.TOTEM_OF_UNDYING);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Te salva de morir + efecto random.");
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        
        // Detecta el tótem en las manos; el evento se dispara antes de que vanilla lo consuma.
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();

        ItemStack resurrectionItem = null;
        if (main.getType() == Material.TOTEM_OF_UNDYING && isItem(main)) {
            resurrectionItem = main;
        } else if (off.getType() == Material.TOTEM_OF_UNDYING && isItem(off)) {
            resurrectionItem = off;
        }

        if (resurrectionItem != null) {
            // Vanilla maneja la resurrección; aquí aplicamos el efecto aleatorio más largo para que se note.
            PotionEffectType type = effects[random.nextInt(effects.length)];
            player.addPotionEffect(new PotionEffect(type, 20 * 60, 1));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>¡El Tótem del Azar te ha dado " + type.getKey().getKey() + "!"));
        }
    }
}
