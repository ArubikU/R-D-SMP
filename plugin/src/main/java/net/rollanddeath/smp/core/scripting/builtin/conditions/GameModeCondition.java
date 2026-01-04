package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class GameModeCondition implements Condition {

    private final List<String> allowedModes;
    private final Object targetSpec;
    private final boolean invert;

    public static void register() {
        ConditionRegistrar.register("game_mode_in", GameModeCondition::new, "player_game_mode_in");
    }

    public GameModeCondition(Map<?, ?> spec) {
        Object valuesObj = spec.get("values");
        if (valuesObj instanceof List<?> list) {
            this.allowedModes = list.stream().map(String::valueOf).toList();
        } else {
            String single = Resolvers.string(null, spec, "value");
            this.allowedModes = single != null ? List.of(single) : List.of();
        }
        this.targetSpec = spec.get("target");
        this.invert = Resolvers.bool(null, spec.get("invert")) == Boolean.TRUE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (allowedModes.isEmpty()) return false;

        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                return false;
            }
        }

        boolean allMatch = true;
        for (Entity e : targets) {
            if (!(e instanceof Player p)) {
                allMatch = false;
                break;
            }
            GameMode gm = p.getGameMode();
            boolean in = false;
            if (gm != null) {
                String name = gm.name();
                for (String s : allowedModes) {
                    if (name.equalsIgnoreCase(s.trim())) {
                        in = true;
                        break;
                    }
                }
            }
            if (!in) {
                allMatch = false;
                break;
            }
        }
        return invert ? !allMatch : allMatch;
    }
}
