package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;
import java.util.regex.Pattern;

public class VarMatchesRegexCondition implements Condition {

    private final String key;
    private final Pattern pattern;

    public static void register() {
        ConditionRegistrar.register("var_matches_regex", VarMatchesRegexCondition::new);
    }

    public VarMatchesRegexCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
        String regex = Resolvers.string(null, spec, "pattern", "regex");
        boolean caseInsensitive = Resolvers.bool(null, spec.get("case_insensitive")) != Boolean.FALSE;
        
        Pattern p = null;
        if (regex != null) {
            try {
                p = Pattern.compile(regex, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
            } catch (Exception ignored) {}
        }
        this.pattern = p;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null || pattern == null) return false;
        Object val = ctx.getValue(key);
        String s = val != null ? String.valueOf(val) : "";
        return pattern.matcher(s).matches();
    }
}
