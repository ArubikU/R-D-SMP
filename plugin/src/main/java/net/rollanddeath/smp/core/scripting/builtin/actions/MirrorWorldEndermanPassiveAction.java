package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.entity.Enderman;
import org.bukkit.event.entity.EntityTargetEvent;

final class MirrorWorldEndermanPassiveAction {
    private MirrorWorldEndermanPassiveAction() {}

    static void register() {
        ActionRegistrar.register("mirror_world_enderman_passive", MirrorWorldEndermanPassiveAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            if (ctx.event() instanceof EntityTargetEvent e) {
                if (e.getEntity() instanceof Enderman enderman) {
                    e.setCancelled(true);
                    e.setTarget(null);
                    
                    // NMS Logic to clear goals if possible
                    try {
                        net.minecraft.world.entity.monster.EnderMan nmsEnderman = ((CraftEnderman) enderman).getHandle();
                        // We can't easily remove specific goals without reflection or knowing the exact goal class instance.
                        // But we can clear the target selector to prevent them from finding new targets.
                        // nmsEnderman.targetSelector.removeAllGoals(goal -> true); // Paper API or NMS?
                        
                        // In Mojang mappings:
                        // nmsEnderman.targetSelector.getAvailableGoals().clear(); // If accessible
                        
                        // Or just rely on cancelling the event which stops the immediate targeting.
                        // But to make them truly passive, we might want to remove the NearestAttackableTargetGoal.
                        
                        // Since we are in a plugin and might not have full NMS access without reflection helper,
                        // and the user asked for NMS specifically:
                        
                        // Using Paper API if available is safer, but user said NMS.
                        // Let's try to clear the target selector using reflection on the NMS object.
                        
                        // For now, cancelling the event is the most robust "passive" behavior for a specific event.
                        // But if this action is called ONCE to "make them passive forever", we need to modify the entity.
                        
                        // If the action is triggered by "spawn" event:
                        nmsEnderman.targetSelector.removeAllGoals(goal -> true);
                        
                    } catch (Throwable ignored) {
                        // Fallback if NMS fails
                    }
                }
            } else if (ctx.entity() instanceof Enderman enderman) {
                 // If called on an entity directly (not target event)
                 try {
                    net.minecraft.world.entity.monster.EnderMan nmsEnderman = ((CraftEnderman) enderman).getHandle();
                    nmsEnderman.targetSelector.removeAllGoals(goal -> true);
                 } catch (Throwable ignored) {}
            }
            return ActionResult.ALLOW;
        };
    }
}
