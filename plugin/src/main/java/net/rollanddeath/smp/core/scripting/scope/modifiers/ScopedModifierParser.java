package net.rollanddeath.smp.core.scripting.scope.modifiers;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import net.rollanddeath.smp.core.scripting.scope.ScopeId;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScopedModifierParser {

    private ScopedModifierParser() {
    }

    public static Map<ScopeId, List<ScopedModifier>> parseAll(ConfigurationSection root) {
        Map<ScopeId, List<ScopedModifier>> out = new EnumMap<>(ScopeId.class);
        if (root == null) return out;

        ConfigurationSection sec = root.getConfigurationSection("scoped_modifiers");
        if (sec == null) return out;

        for (String scopeKey : sec.getKeys(false)) {
            ScopeId scope;
            try {
                scope = ScopeId.valueOf(scopeKey.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                continue;
            }

            List<Map<?, ?>> list = sec.getMapList(scopeKey);
            if (list == null || list.isEmpty()) continue;

            List<ScopedModifier> mods = new ArrayList<>();
            for (Map<?, ?> raw : list) {
                ScopedModifier mod = parseOne(scope, raw);
                if (mod != null) mods.add(mod);
            }

            if (!mods.isEmpty()) {
                mods.sort((a, b) -> Integer.compare(b.priority(), a.priority()));
                out.put(scope, mods);
            }
        }

        return out;
    }

    private static ScopedModifier parseOne(ScopeId scope, Map<?, ?> raw) {
        if (raw == null) return null;
        Object idObj = raw.get("id");
        Object prObj = raw.get("priority");
        Object whenObj = raw.get("when");
        Object effectsObj = raw.get("effects");

        if (!(idObj instanceof String id) || id.isBlank()) return null;
        int priority = 0;
        if (prObj instanceof Number n) {
            priority = n.intValue();
        } else if (prObj instanceof String s) {
            try {
                priority = Integer.parseInt(s.trim());
            } catch (Exception ignored) {
                priority = 0;
            }
        }

        Condition when = null;
        if (whenObj instanceof Map<?, ?> whenMap) {
            when = ConditionRegistrar.parse(whenMap);
        }
        if (when == null) {
            // when es obligatorio por spec (aunque puede ser always-true si no se define)
            when = ctx -> true;
        }

        List<Action> effects = new ArrayList<>();
        if (effectsObj instanceof List<?> list) {
            for (Object o : list) {
                if (!(o instanceof Map<?, ?> m)) continue;
                Action a = ActionRegistrar.parse(m);
                if (a == null) continue;

                // En scoped_modifiers solo se permiten efectos sobre variables custom.
                // Por ahora restringimos a set_var y add_var (otras acciones podrían tocar mundo/jugador).
                Object t = m.get("type");
                if (t instanceof String ts) {
                    String type = ts.trim().toLowerCase(Locale.ROOT);
                    if (!"set_var".equals(type) && !"add_var".equals(type)) {
                        continue;
                    }
                } else {
                    continue;
                }

                effects.add(a);
            }
        }

        if (effects.isEmpty()) return null;
        return new ScopedModifier(scope, id.trim(), priority, when, effects);
    }
}
