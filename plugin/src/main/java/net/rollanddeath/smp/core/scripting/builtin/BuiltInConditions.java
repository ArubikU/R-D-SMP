package net.rollanddeath.smp.core.scripting.builtin;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.library.CallGuard;
import net.rollanddeath.smp.core.scripting.library.ScriptLibrary;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class BuiltInConditions {

    private static final ConcurrentHashMap<String, Function<Map<?, ?>, Condition>> REGISTERED = new ConcurrentHashMap<>();

    private BuiltInConditions() {
    }

    public static void register(String type, Function<Map<?, ?>, Condition> parser, String... aliases) {
        if (type == null || type.isBlank() || parser == null) return;
        String t = type.trim().toLowerCase(Locale.ROOT);
        REGISTERED.put(t, parser);
        if (aliases != null) {
            for (String a : aliases) {
                if (a == null || a.isBlank()) continue;
                REGISTERED.put(a.trim().toLowerCase(Locale.ROOT), parser);
            }
        }
    }

    public static Condition parse(Map<?, ?> raw) {
        String type = BuiltInArgs.lowerType(raw);
        if (type == null) return null;

        Function<Map<?, ?>, Condition> override = REGISTERED.get(type);
        if (override != null) {
            return override.apply(raw);
        }

        return switch (type) {
            case "call", "cond_call", "call_condition", "macro" -> {
                final String refFinal = BuiltInArgs.refOrId(raw);
                if (refFinal == null) yield null;

                final Map<String, Object> withFinal = BuiltInArgs.stringObjectMap(raw.get("with"));
                boolean allowMissing = BuiltInArgs.bool(raw, "allow_missing", false);

                yield ctx -> {
                    if (ctx == null || ctx.plugin() == null) return false;
                    ScriptLibrary lib = ctx.plugin().getScriptLibrary();
                    if (lib == null) return allowMissing;

                    Condition c = lib.getCondition(refFinal);
                    if (c == null) return allowMissing;

                    String guardKey = "cond:" + refFinal;
                    if (!CallGuard.enter(guardKey)) {
                        ctx.plugin().getLogger().warning("[Script] call(condition) recursivo/cíclico: " + refFinal + " (subject=" + ctx.subjectId() + ")");
                        return false;
                    }

                    try {
                        BuiltInArgs.applyWithArgs(ctx, withFinal);
                        return c.test(ctx);
                    } finally {
                        CallGuard.exit(guardKey);
                    }
                };
            }
            case "var_truthy" -> {
                String key = getString(raw, "key");
                if (key == null || key.isBlank()) yield null;
                boolean invert = raw.get("invert") instanceof Boolean b ? b : false;
                yield ctx -> {
                    Object v = ctx.getValue(key);
                    boolean truthy;
                    if (v == null) truthy = false;
                    else if (v instanceof Boolean b) truthy = b;
                    else if (v instanceof Number n) truthy = Double.compare(n.doubleValue(), 0.0) != 0;
                    else if (v instanceof String s) {
                        String t = s.trim().toLowerCase(Locale.ROOT);
                        if (t.isBlank()) truthy = false;
                        else if (t.equals("true") || t.equals("yes") || t.equals("y") || t.equals("1") || t.equals("on")) truthy = true;
                        else if (t.equals("false") || t.equals("no") || t.equals("n") || t.equals("0") || t.equals("off")) truthy = false;
                        else truthy = true;
                    } else {
                        truthy = true;
                    }
                    return invert ? !truthy : truthy;
                };
            }
            case "game_mode_in", "player_game_mode_in" -> {
                Object valuesObj = raw.get("values");
                List<String> values = null;
                if (valuesObj instanceof List<?> list && !list.isEmpty()) {
                    values = list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
                }
                String single = getString(raw, "value");
                if ((values == null || values.isEmpty()) && (single == null || single.isBlank())) yield null;
                boolean invert = BuiltInArgs.invert(raw);
                final List<String> allowed = values != null ? values : List.of(single);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

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
                            for (String s : allowed) {
                                if (s == null) continue;
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
                };
            }
            case "in_water", "player_in_water", "player_is_in_water" -> {
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                boolean invert = BuiltInArgs.invert(raw);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        boolean in;
                        try {
                            in = e.isInWater();
                        } catch (Exception ignored) {
                            in = false;
                        }
                        if (in != expected) {
                            allMatch = false;
                            break;
                        }
                    }
                    return invert ? !allMatch : allMatch;
                };
            }
            case "has_cooldown", "player_has_cooldown" -> {
                String matRaw = getString(raw, "material");
                if (matRaw == null || matRaw.isBlank()) yield null;
                Material mat;
                try {
                    mat = Material.valueOf(matRaw.trim().toUpperCase(Locale.ROOT));
                } catch (Exception ignored) {
                    mat = null;
                }
                if (mat == null) yield null;

                boolean expected = BuiltInArgs.expectedBool(raw, true);
                boolean invert = BuiltInArgs.invert(raw);
                Object targetSpec = raw.get("target");

                final Material matFinal = mat;
                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (!(e instanceof Player p)) {
                            allMatch = false;
                            break;
                        }
                        boolean has;
                        try {
                            has = p.hasCooldown(matFinal);
                        } catch (Exception ignored) {
                            has = false;
                        }
                        if (has != expected) {
                            allMatch = false;
                            break;
                        }
                    }
                    return invert ? !allMatch : allMatch;
                };
            }
            case "var_is_missing" -> {
                String key = getString(raw, "key");
                if (key == null || key.isBlank()) yield null;
                yield ctx -> {
                    Object v = ctx.getValue(key);
                    if (v == null) return true;
                    if (v instanceof String s) return s.isBlank();
                    return false;
                };
            }
            case "time_between", "world_time_between" -> {
                Integer min = getInt(raw, "min");
                Integer max = getInt(raw, "max");
                if (min == null || max == null) yield null;
                int a = ((min % 24000) + 24000) % 24000;
                int b = ((max % 24000) + 24000) % 24000;
                boolean inclusive = raw.get("inclusive") instanceof Boolean bi ? bi : true;
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;
                    
                    boolean allMatch = true;
                    for (Entity e : targets) {
                        World w = e.getWorld();
                        long t0 = w.getTime();
                        int t = (int) (t0 % 24000);
                        boolean match;
                        if (a <= b) {
                            match = inclusive ? (t >= a && t <= b) : (t > a && t < b);
                        } else {
                            match = inclusive ? (t >= a || t <= b) : (t > a || t < b);
                        }
                        if (!match) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "sky_light_at_least", "player_sky_light_at_least" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                int v = Math.max(0, Math.min(15, value));
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        try {
                            if (e.getLocation().getBlock().getLightFromSky() < v) {
                                allMatch = false;
                                break;
                            }
                        } catch (Exception ignored) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "inventory_full", "player_inventory_full" -> {
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (!(e instanceof org.bukkit.inventory.InventoryHolder holder)) {
                            allMatch = false;
                            break;
                        }
                        boolean full = true;
                        try {
                            for (ItemStack it : holder.getInventory().getStorageContents()) {
                                if (it == null || it.getType().isAir()) {
                                    full = false;
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                            full = false;
                        }
                        if (full != expected) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "has_storm", "world_has_storm" -> {
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (e.getWorld().hasStorm() != expected) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "environment_is", "world_environment_is" -> {
                String value = getString(raw, "value");
                if (value == null || value.isBlank()) yield null;
                World.Environment env;
                try {
                    env = World.Environment.valueOf(value.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    yield null;
                }
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (e.getWorld().getEnvironment() != env) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "any_of" -> {
                Object condsObj = raw.get("conditions");
                if (!(condsObj instanceof List<?> list) || list.isEmpty()) yield null;

                List<Condition> conds = new ArrayList<>();
                for (Object o : list) {
                    if (!(o instanceof Map<?, ?> m)) continue;
                    Condition c = BuiltInConditions.parse(m);
                    if (c != null) conds.add(c);
                }
                if (conds.isEmpty()) yield null;
                yield ctx -> {
                    for (Condition c : conds) {
                        if (c.test(ctx)) return true;
                    }
                    return false;
                };
            }
            case "all_of" -> {
                Object condsObj = raw.get("conditions");
                if (!(condsObj instanceof List<?> list) || list.isEmpty()) yield null;

                List<Condition> conds = new ArrayList<>();
                for (Object o : list) {
                    if (!(o instanceof Map<?, ?> m)) continue;
                    Condition c = BuiltInConditions.parse(m);
                    if (c != null) conds.add(c);
                }
                if (conds.isEmpty()) yield null;
                yield ctx -> {
                    for (Condition c : conds) {
                        if (!c.test(ctx)) return false;
                    }
                    return true;
                };
            }
            case "not" -> {
                Object innerObj = raw.get("condition");
                if (!(innerObj instanceof Map<?, ?> innerMap)) yield null;
                Condition inner = BuiltInConditions.parse(innerMap);
                if (inner == null) yield null;
                yield ctx -> !inner.test(ctx);
            }
            case "random_chance" -> {
                Double probability = null;
                Object pObj = raw.get("probability");
                if (pObj instanceof Number n) probability = n.doubleValue();
                if (pObj instanceof String s) {
                    try {
                        probability = Double.parseDouble(s.trim());
                    } catch (Exception ignored) {
                        probability = null;
                    }
                }
                if (probability == null) {
                    Integer num = getInt(raw, "numerator");
                    Integer den = getInt(raw, "denominator");
                    if (num != null && den != null && den > 0) {
                        probability = Math.max(0.0, Math.min(1.0, num.doubleValue() / den.doubleValue()));
                    }
                }
                if (probability == null) yield null;
                double p = Math.max(0.0, Math.min(1.0, probability));
                yield ctx -> ThreadLocalRandom.current().nextDouble() < p;
            }
            case "min_day" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                yield ctx -> {
                    RollAndDeathSMP p = ctx.plugin();
                    int currentDay = Math.max(1, p.getGameManager() != null ? p.getGameManager().getCurrentDay() : 1);
                    return currentDay >= value;
                };
            }
            case "lives_at_least" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                yield ctx -> ctx.plugin().getLifeManager().getLives(ctx.player()) >= value;
            }
            case "role_is" -> {
                String value = getString(raw, "value");
                if (value == null || value.isBlank()) yield null;
                RoleType required;
                try {
                    required = RoleType.valueOf(value.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    yield null;
                }
                yield ctx -> ctx.plugin().getRoleManager() != null
                    && Objects.equals(ctx.plugin().getRoleManager().getPlayerRole(ctx.player()), required);
            }
            case "modifier_active" -> {
                String name = getString(raw, "name");
                if (name == null || name.isBlank()) yield null;
                yield ctx -> {
                    ModifierManager mm = ctx.plugin().getModifierManager();
                    return mm != null && mm.isActive(name);
                };
            }
            case "placeholder_compare" -> {
                String placeholder = getString(raw, "placeholder");
                String operator = Optional.ofNullable(getString(raw, "operator")).map(String::trim).orElse("==");
                String expected = getString(raw, "value");
                if (placeholder == null || placeholder.isBlank() || expected == null) yield null;

                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    RollAndDeathSMP p = ctx.plugin();
                    Player player = ctx.player();

                    String actual = PlaceholderUtil.resolvePlaceholders(p, player, placeholder);
                    String exp = PlaceholderUtil.resolvePlaceholders(p, player, expected);
                    if (actual == null) actual = "";
                    if (exp == null) exp = "";

                    String a = caseInsensitive ? actual.toLowerCase(Locale.ROOT) : actual;
                    String e = caseInsensitive ? exp.toLowerCase(Locale.ROOT) : exp;

                    Double an = tryParseDouble(a);
                    Double en = tryParseDouble(e);

                    if (an != null && en != null && isNumericOp(operator)) {
                        return switch (operator) {
                            case ">" -> an > en;
                            case ">=" -> an >= en;
                            case "<" -> an < en;
                            case "<=" -> an <= en;
                            case "==" -> Double.compare(an, en) == 0;
                            case "!=" -> Double.compare(an, en) != 0;
                            default -> false;
                        };
                    }

                    return switch (operator) {
                        case "==" -> a.equals(e);
                        case "!=" -> !a.equals(e);
                        case "contains" -> a.contains(e);
                        default -> false;
                    };
                };
            }
            case "var_equals" -> {
                String key = getString(raw, "key");
                String value = getString(raw, "value");
                if (key == null || key.isBlank() || value == null) yield null;
                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    String actual = ctx.stringVar(key);
                    if (actual == null) actual = "";
                    String expected = value;

                    if (caseInsensitive) {
                        actual = actual.toLowerCase(Locale.ROOT);
                        expected = expected.toLowerCase(Locale.ROOT);
                    }

                    return actual.equals(expected);
                };
            }
            case "var_in" -> {
                String key = getString(raw, "key");
                Object valuesObj = raw.get("values");
                if (key == null || key.isBlank() || !(valuesObj instanceof List<?> values) || values.isEmpty()) yield null;
                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    String actual = ctx.stringVar(key);
                    if (actual == null) actual = "";
                    if (caseInsensitive) {
                        actual = actual.toLowerCase(Locale.ROOT);
                    }

                    for (Object o : values) {
                        if (!(o instanceof String s)) continue;
                        String v = caseInsensitive ? s.toLowerCase(Locale.ROOT) : s;
                        if (actual.equals(v)) return true;
                    }
                    return false;
                };
            }
            case "var_compare" -> {
                String key = getString(raw, "key");
                String operator = Optional.ofNullable(getString(raw, "operator")).map(String::trim).orElse("==");
                Object valueObj = raw.get("value");
                String otherKey = getString(raw, "other_key");
                if (key == null || key.isBlank()) yield null;
                if (valueObj == null && (otherKey == null || otherKey.isBlank())) yield null;

                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    Object leftObj = ctx.getValue(key);
                    Object rightObj = otherKey != null && !otherKey.isBlank() ? ctx.getValue(otherKey) : valueObj;

                    String left = leftObj != null ? String.valueOf(leftObj) : "";
                    String right = rightObj != null ? String.valueOf(rightObj) : "";

                    String l = caseInsensitive ? left.toLowerCase(Locale.ROOT) : left;
                    String r = caseInsensitive ? right.toLowerCase(Locale.ROOT) : right;

                    Double ln = tryParseDouble(l);
                    Double rn = tryParseDouble(r);

                    if (ln != null && rn != null && isNumericOp(operator)) {
                        return switch (operator) {
                            case ">" -> ln > rn;
                            case ">=" -> ln >= rn;
                            case "<" -> ln < rn;
                            case "<=" -> ln <= rn;
                            case "==" -> Double.compare(ln, rn) == 0;
                            case "!=" -> Double.compare(ln, rn) != 0;
                            default -> false;
                        };
                    }

                    return switch (operator) {
                        case "==" -> l.equals(r);
                        case "!=" -> !l.equals(r);
                        case "contains" -> l.contains(r);
                        default -> false;
                    };
                };
            }
            case "has_permission", "player_has_permission" -> {
                String perm = getString(raw, "permission");
                if (perm == null || perm.isBlank()) yield null;
                boolean invert = BuiltInArgs.invert(raw);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (!e.hasPermission(perm)) {
                            allMatch = false;
                            break;
                        }
                    }
                    return invert ? !allMatch : allMatch;
                };
            }
            case "now_ms_gte_var" -> {
                String key = getString(raw, "key");
                if (key == null || key.isBlank()) yield null;
                yield nowMsGteVar(key);
            }
            case "is_thundering", "world_is_thundering" -> {
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                Object targetSpec = raw.get("target");

                yield ctx -> {
                    List<Entity> targets = resolveTargets(ctx, targetSpec);
                    if (targets.isEmpty()) return false;

                    boolean allMatch = true;
                    for (Entity e : targets) {
                        if (e.getWorld().isThundering() != expected) {
                            allMatch = false;
                            break;
                        }
                    }
                    return allMatch;
                };
            }
            case "material_in_tag" -> {
                String key = getString(raw, "key");
                String tagName = getString(raw, "tag");
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                if (key == null || key.isBlank() || tagName == null || tagName.isBlank()) yield null;

                Tag<Material> tag = switch (tagName.trim().toUpperCase(Locale.ROOT)) {
                    case "LOGS" -> Tag.LOGS;
                    case "LOGS_THAT_BURN" -> Tag.LOGS_THAT_BURN;
                    case "PLANKS" -> Tag.PLANKS;
                    case "LEAVES" -> Tag.LEAVES;
                    default -> null;
                };
                if (tag == null) yield null;

                yield ctx -> {
                    Object v = ctx.getValue(key);
                    if (v == null) return expected == false;
                    Material m;
                    try {
                        m = Material.valueOf(String.valueOf(v).trim().toUpperCase(Locale.ROOT));
                    } catch (Exception ignored) {
                        m = null;
                    }
                    if (m == null) return expected == false;
                    return tag.isTagged(m) == expected;
                };
            }

            case "material_is_ore" -> {
                String key = getString(raw, "key");
                boolean expected = BuiltInArgs.expectedBool(raw, true);
                if (key == null || key.isBlank()) yield null;

                yield ctx -> {
                    Object v = ctx.getValue(key);
                    if (v == null) return expected == false;

                    String name = String.valueOf(v).trim().toUpperCase(Locale.ROOT);
                    if (name.isBlank()) return expected == false;

                    Material m;
                    try {
                        m = Material.valueOf(name);
                    } catch (Exception ignored) {
                        m = null;
                    }

                    boolean isOre;
                    if (m != null) {
                        isOre = Tag.COAL_ORES.isTagged(m)
                            || Tag.COPPER_ORES.isTagged(m)
                            || Tag.DIAMOND_ORES.isTagged(m)
                            || Tag.EMERALD_ORES.isTagged(m)
                            || Tag.GOLD_ORES.isTagged(m)
                            || Tag.IRON_ORES.isTagged(m)
                            || Tag.LAPIS_ORES.isTagged(m)
                            || Tag.REDSTONE_ORES.isTagged(m)
                            || m == Material.NETHER_GOLD_ORE
                            || m == Material.NETHER_QUARTZ_ORE
                            || m == Material.ANCIENT_DEBRIS;
                    } else {
                        isOre = name.endsWith("_ORE") || name.equals("ANCIENT_DEBRIS");
                    }

                    return isOre == expected;
                };
            }
            case "var_matches_regex" -> {
                String key = getString(raw, "key");
                String pattern = getString(raw, "pattern");
                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;
                if (key == null || key.isBlank() || pattern == null) yield null;

                Pattern compiled;
                try {
                    compiled = Pattern.compile(pattern, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
                } catch (Exception e) {
                    compiled = null;
                }
                if (compiled == null) yield null;

                final Pattern compiledFinal = compiled;

                yield ctx -> {
                    Object v = ctx.getValue(key);
                    String s = v != null ? String.valueOf(v) : "";
                    return compiledFinal.matcher(s).matches();
                };
            }
            default -> null;
        };
    }

    private static List<Entity> resolveTargets(ScriptContext ctx, Object targetSpec) {
        if (targetSpec != null) {
            return Resolvers.entities(ctx, targetSpec);
        } else {
            Player p = ctx.player();
            return p != null ? List.of(p) : List.of();
        }
    }

    private static Condition nowMsGteVar(String key) {
        return ctx -> {
            Object v = ctx.getValue(key);
            if (v == null) return true;
            long target;
            if (v instanceof Number n) {
                target = n.longValue();
            } else {
                try {
                    target = Long.parseLong(String.valueOf(v));
                } catch (Exception ignored) {
                    return true;
                }
            }
            return System.currentTimeMillis() >= target;
        };
    }

    private static boolean isNumericOp(String op) {
        return ">".equals(op) || ">=".equals(op) || "<".equals(op) || "<=".equals(op) || "==".equals(op) || "!=".equals(op);
    }

    private static Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getString(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        return (v instanceof String s) ? s : null;
    }

    private static Integer getInt(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
