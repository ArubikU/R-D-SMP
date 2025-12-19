package net.rollanddeath.smp.core.scripting;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.Player;

import java.util.Map;

public final class ScriptContext {

    private final RollAndDeathSMP plugin;
    private final Player player;
    private final String subjectId;
    private final ScriptPhase phase;
    private final Map<String, Object> variables;

    public ScriptContext(RollAndDeathSMP plugin, Player player, String subjectId, ScriptPhase phase, Map<String, Object> variables) {
        this.plugin = plugin;
        this.player = player;
        this.subjectId = subjectId;
        this.phase = phase;
        this.variables = variables;
    }

    public RollAndDeathSMP plugin() {
        return plugin;
    }

    public Player player() {
        return player;
    }

    public String subjectId() {
        return subjectId;
    }

    public ScriptPhase phase() {
        return phase;
    }

    public Map<String, Object> variables() {
        return variables;
    }

    public String stringVar(String key) {
        Object v = variables.get(key);
        return v != null ? String.valueOf(v) : null;
    }
}
