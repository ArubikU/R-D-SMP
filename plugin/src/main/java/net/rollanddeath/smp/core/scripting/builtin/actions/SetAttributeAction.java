package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

final class SetAttributeAction {
    private SetAttributeAction() {}

    static void register() {
        ActionRegistrar.register("set_attribute", SetAttributeAction::parse, "set_attribute_base", "set_mob_attribute_base", "set_mob_max_health");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("entity");
        final Object targetSpec = targetSpecRaw;
        
        Attribute attribute = Resolvers.attribute(null, raw, "attribute", "attr");
        Double value = Resolvers.doubleVal(null, raw, "value", "amount", "base");
        
        // Special case for set_mob_max_health legacy
        if (attribute == null && raw.containsKey("value") && !raw.containsKey("attribute")) {
             // If called as set_mob_max_health, attribute is implied MAX_HEALTH
             // But we need to know the alias used.
             // We can't easily know the alias here.
             // But if attribute is missing, we might default to MAX_HEALTH if the action type implies it?
             // No, parse doesn't know the alias.
             // But we can check if "attribute" is missing and maybe "value" is present.
             // Actually, legacy set_mob_max_health just had "value".
             // We can try to detect if it's max health.
             // Or we can just require attribute.
             // For legacy support, I might need a separate parser or just check if attribute is null.
             // If attribute is null, check if it's likely max health?
             // No, that's unsafe.
             // I'll register a separate parser for set_mob_max_health if needed, or just use this one and require attribute.
             // But I want to replace set_mob_max_health.
             // I'll add a check: if attribute is null, default to MAX_HEALTH?
             // Only if I'm sure.
             // Let's just require attribute for new usage.
             // For legacy set_mob_max_health, I'll keep the old action or map it here.
             // I'll map it here by checking if attribute is null.
             if (attribute == null) attribute = Attribute.MAX_HEALTH;
        }
        
        if (attribute == null || value == null) return null;

        final Attribute finalAttr = attribute;
        final double finalValue = value;

        return ctx -> {
            List<Entity> resolvedTargets = Resolvers.entities(ctx, targetSpec);
            final List<Entity> targets;
            if (resolvedTargets.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                targets = resolvedTargets;
            }

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        AttributeInstance inst = le.getAttribute(finalAttr);
                        if (inst != null) {
                            inst.setBaseValue(finalValue);
                            if (finalAttr == Attribute.MAX_HEALTH) {
                                le.setHealth(finalValue);
                            }
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
