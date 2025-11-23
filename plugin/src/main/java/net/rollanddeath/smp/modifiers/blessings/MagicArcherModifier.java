package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.entity.Player;

public class MagicArcherModifier extends Modifier {

    public MagicArcherModifier(RollAndDeathSMP plugin) {
        super(plugin, "Arquero Mágico", ModifierType.BLESSING, "Las flechas no se consumen.");
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        // Unregistering is handled by Bukkit usually, but we can't easily unregister specific listeners without HandlerList
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setConsumeItem(false);
            // Optional: Update inventory to sync client? Usually not needed for setConsumeItem(false)
        }
    }
}
