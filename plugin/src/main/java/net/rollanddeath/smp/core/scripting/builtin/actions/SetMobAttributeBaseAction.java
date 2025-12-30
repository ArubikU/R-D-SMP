package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

final class SetMobAttributeBaseAction {
    private SetMobAttributeBaseAction() {}

    static void register() {
        ActionRegistrar.register("set_mob_attribute_base", SetMobAttributeBaseAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String attributeName = Resolvers.string(null, raw, "attribute");
        if (attributeName == null || attributeName.isBlank()) return null;
        
        Double value = Resolvers.doubleVal(null, raw, "value");
        String valueKey = Resolvers.string(null, raw, "value_key");
        if (value == null && (valueKey == null || valueKey.isBlank())) return null;

        return ctx -> {
            LivingEntity entity = ctx.subjectOrEventEntity(LivingEntity.class);
            if (entity == null) return ActionResult.ALLOW;

            Attribute attr;
            try {
                attr = Attribute.valueOf(attributeName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }

            double val;
            if (value != null) {
                val = value;
            } else {
                Double d = Resolvers.resolveDouble(ctx.getValue(valueKey));
                if (d == null) return ActionResult.ALLOW;
                val = d;
            }

            ActionUtils.runSync(ctx.plugin(), () -> {
                AttributeInstance inst = entity.getAttribute(attr);
                if (inst != null) {
                    inst.setBaseValue(val);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
