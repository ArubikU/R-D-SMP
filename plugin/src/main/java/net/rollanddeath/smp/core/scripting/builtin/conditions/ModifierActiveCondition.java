package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class ModifierActiveCondition implements Condition {

    private final String name;

    public static void register() {
        ConditionRegistrar.register("modifier_active", ModifierActiveCondition::new);
    }

    public ModifierActiveCondition(Map<?, ?> spec) {
        this.name = Resolvers.string(null, spec, "name", "modifier");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (name == null) return false;
        ModifierManager mm = ctx.plugin().getModifierManager();
        return mm != null && mm.isActive(name);
    }
}
