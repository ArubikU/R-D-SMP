package net.rollanddeath.smp.core.scripting;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder mínimo para variables internas del motor (solo __subject/__target/__projectile/__event/__item).
 *
 * No expone API pública de scripts: solo ayuda a reducir boilerplate de listeners.
 */
public final class ScriptVars {

    private final Map<String, Object> vars = new HashMap<>();

    private ScriptVars() {
    }

    public static ScriptVars create() {
        return new ScriptVars();
    }

    public ScriptVars subject(Entity subject) {
        if (subject != null) vars.put("__subject", subject);
        return this;
    }

    public ScriptVars target(Entity target) {
        if (target != null) vars.put("__target", target);
        return this;
    }

    public ScriptVars projectile(Entity projectile) {
        if (projectile != null) vars.put("__projectile", projectile);
        return this;
    }

    public ScriptVars item(ItemStack item) {
        if (item != null) vars.put("__item", item);
        return this;
    }

    public ScriptVars event(Object event) {
        if (event != null) vars.put("__event", event);
        return this;
    }

    public ScriptVars putInternal(String key, Object value) {
        if (key == null || key.isBlank()) return this;
        vars.put(key, value);
        return this;
    }

    public ScriptVars merge(Map<String, Object> base) {
        if (base != null && !base.isEmpty()) vars.putAll(base);
        return this;
    }

    public Map<String, Object> build() {
        return vars;
    }
}
