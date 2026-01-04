package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

final class MirrorWorldPigAggressiveAction {
    private MirrorWorldPigAggressiveAction() {}

    static void register() {
        ActionRegistrar.register("mirror_world_pig_aggressive", MirrorWorldPigAggressiveAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            if (ctx.entity() instanceof Pig pig) {
                AttributeInstance damage = pig.getAttribute(org.bukkit.Registry.ATTRIBUTE.get(org.bukkit.NamespacedKey.minecraft("generic.attack_damage")));
                if (damage != null) {
                    damage.setBaseValue(4.0);
                } else {
                    // Pigs don't have attack damage by default, we might need to register it?
                    // Bukkit handles this if we set it? Or maybe not if the attribute isn't registered on the entity type.
                    // We can try to register it via NMS if needed, but usually setting base value works if the attribute exists.
                    // If it returns null, we can't set it easily via Bukkit API.
                }

                try {
                    net.minecraft.world.entity.animal.Pig nmsPig = ((CraftPig) pig).getHandle();
                    
                    // Add attack goals
                    // 1. MeleeAttackGoal (priority 1 or 2)
                    nmsPig.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(nmsPig, 1.0D, false));
                    
                    // 2. NearestAttackableTargetGoal (priority 1 or 2)
                    nmsPig.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(nmsPig, net.minecraft.world.entity.player.Player.class, true));
                    
                    // Ensure attributes are present (NMS way if Bukkit failed)
                    // nmsPig.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
                    
                } catch (Throwable ignored) {
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
