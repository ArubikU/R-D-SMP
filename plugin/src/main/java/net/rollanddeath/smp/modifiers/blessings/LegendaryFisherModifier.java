package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LegendaryFisherModifier extends Modifier {

    public LegendaryFisherModifier(JavaPlugin plugin) {
        super(plugin, "Pescador Legendario", ModifierType.BLESSING, "Pesca es instantánea.");
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (event.getHook() instanceof FishHook hook) {
                hook.setMinWaitTime(0);
                hook.setMaxWaitTime(20); // 1 second max wait
                hook.setApplyLure(true);
            }
        }
    }
}
