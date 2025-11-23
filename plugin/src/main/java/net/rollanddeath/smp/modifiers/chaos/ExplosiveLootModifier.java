package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class ExplosiveLootModifier extends Modifier {

    private final Random random = new Random();

    public ExplosiveLootModifier(RollAndDeathSMP plugin) {
        super(plugin, "Botín Explosivo", ModifierType.CHAOS, "20% chance de que cofres exploten.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Material type = event.getClickedBlock().getType();
            if (type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.BARREL || type.name().contains("SHULKER_BOX")) {
                if (Math.random() < 0.2) { // 20% chance
                    event.setCancelled(true);
                    event.getClickedBlock().getWorld().createExplosion(event.getClickedBlock().getLocation(), 2.0f, false, false);
                    event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡El cofre tenía una trampa explosiva!"));
                }
            }
        }
    }
}
