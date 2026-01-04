package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

public class ObjectTagCondition implements Condition {

    private final String targetSpec;
    private final String tagSpec;
    private final boolean expected;

    public static void register() {
        ConditionRegistrar.register("object_tag", ObjectTagCondition::new, "is_tagged", "in_tag", "has_tag");
        ConditionRegistrar.register("material_in_tag", ObjectTagCondition::new);
        ConditionRegistrar.register("material_is_ore", m -> new ObjectTagCondition(m, "#ores"));
    }

    public ObjectTagCondition(Map<?, ?> spec) {
        this(spec, null);
    }

    public ObjectTagCondition(Map<?, ?> spec, String forcedTag) {
        this.targetSpec = Resolvers.string(null, spec, "target", "object", "key", "var", "val");
        this.tagSpec = forcedTag != null ? forcedTag : Resolvers.string(null, spec, "tag", "tags", "tag_name");
        this.expected = Resolvers.bool(null, spec.get("value")) != Boolean.FALSE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (targetSpec == null || tagSpec == null) return !expected;

        Object target = ctx.getValue(targetSpec);
        if (target == null) {
            // If not found in context, maybe the spec itself is the value (e.g. "DIAMOND")
            target = targetSpec;
        }

        boolean match = check(target, tagSpec);
        return match == expected;
    }

    private boolean check(Object target, String tagRaw) {
        if (target == null) return false;

        String tag = tagRaw.trim();
        boolean isTag = tag.startsWith("#");
        String tagName = isTag ? tag.substring(1) : tag;
        
        NamespacedKey key = null;
        if (isTag) {
            String lower = tagName.toLowerCase(Locale.ROOT);
            if (lower.contains(":")) {
                String[] parts = lower.split(":");
                if (parts.length == 2) {
                    key = new NamespacedKey(parts[0], parts[1]);
                }
            } else {
                key = NamespacedKey.minecraft(lower);
            }
        }

        if (target instanceof Entity e) {
            return checkEntity(e.getType(), key, tagName, isTag);
        }
        if (target instanceof EntityType et) {
            return checkEntity(et, key, tagName, isTag);
        }
        if (target instanceof Block b) {
            if (checkMaterial(b.getType(), key, tagName, isTag)) return true;
            return checkBiome(b.getBiome().name(), key, tagName, isTag);
        }
        if (target instanceof ItemStack is) {
            return checkMaterial(is.getType(), key, tagName, isTag);
        }
        if (target instanceof Material m) {
            return checkMaterial(m, key, tagName, isTag);
        }
        if (target instanceof Location l) {
            if (l.getWorld() == null) return false;
            if (checkMaterial(l.getBlock().getType(), key, tagName, isTag)) return true;
            return checkBiome(l.getBlock().getBiome().name(), key, tagName, isTag);
        }
        if (target instanceof String s) {
             // Try Material
             try {
                 Material m = Material.valueOf(s.toUpperCase(Locale.ROOT));
                 if (checkMaterial(m, key, tagName, isTag)) return true;
             } catch (IllegalArgumentException ignored) {}
             
             // Try EntityType
             try {
                 EntityType et = EntityType.valueOf(s.toUpperCase(Locale.ROOT));
                 if (checkEntity(et, key, tagName, isTag)) return true;
             } catch (IllegalArgumentException ignored) {}
             
             // Try Biome (as string)
             return checkBiome(s, key, tagName, isTag);
        }

        return false;
    }

    private boolean checkMaterial(Material mat, NamespacedKey key, String name, boolean isTag) {
        if (isTag && key != null) {
            // Custom "ores" tag support
            if (key.getKey().equalsIgnoreCase("ores")) {
                return isOre(mat);
            }

            // Try Block Tag
            Tag<Material> blockTag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
            if (blockTag != null && blockTag.isTagged(mat)) return true;

            // Try Item Tag
            Tag<Material> itemTag = Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
            if (itemTag != null && itemTag.isTagged(mat)) return true;
            
            // Fallback to hardcoded tags if Bukkit registry fails or for legacy support
            Tag<Material> legacy = getLegacyTag(key.getKey());
            if (legacy != null && legacy.isTagged(mat)) return true;
            
            return false;
        } else {
            return mat.name().equalsIgnoreCase(name);
        }
    }

    private boolean checkEntity(EntityType et, NamespacedKey key, String name, boolean isTag) {
        if (isTag && key != null) {
            Tag<EntityType> tag = Bukkit.getTag(Tag.REGISTRY_ENTITY_TYPES, key, EntityType.class);
            if (tag != null && tag.isTagged(et)) return true;
            return false;
        } else {
            return et.name().equalsIgnoreCase(name);
        }
    }

    private boolean checkBiome(String biomeName, NamespacedKey key, String name, boolean isTag) {
        if (isTag) {
            // Biome tags are not easily accessible via Bukkit API in a standard way across versions without NMS or newer API.
            // We can implement simple string matching for now or custom tags.
            // Example: #minecraft:is_forest -> checks if biome name contains FOREST
            
            String k = key != null ? key.getKey().toLowerCase(Locale.ROOT) : name.toLowerCase(Locale.ROOT);
            String b = biomeName.toLowerCase(Locale.ROOT);
            
            if (k.equals("forest") && b.contains("forest")) return true;
            if (k.equals("ocean") && b.contains("ocean")) return true;
            if (k.equals("plains") && b.contains("plains")) return true;
            if (k.equals("desert") && b.contains("desert")) return true;
            if (k.equals("snow") && (b.contains("snow") || b.contains("ice") || b.contains("frozen"))) return true;
            
            return false;
        } else {
            return biomeName.equalsIgnoreCase(name);
        }
    }

    private boolean isOre(Material m) {
        if (Tag.COAL_ORES.isTagged(m) ||
            Tag.COPPER_ORES.isTagged(m) ||
            Tag.DIAMOND_ORES.isTagged(m) ||
            Tag.EMERALD_ORES.isTagged(m) ||
            Tag.GOLD_ORES.isTagged(m) ||
            Tag.IRON_ORES.isTagged(m) ||
            Tag.LAPIS_ORES.isTagged(m) ||
            Tag.REDSTONE_ORES.isTagged(m) ||
            m == Material.NETHER_GOLD_ORE ||
            m == Material.NETHER_QUARTZ_ORE ||
            m == Material.ANCIENT_DEBRIS) {
            return true;
        }
        String name = m.name();
        return name.endsWith("_ORE");
    }

    private Tag<Material> getLegacyTag(String name) {
        if (name == null) return null;
        switch (name.toUpperCase(Locale.ROOT)) {
            case "LOGS": return Tag.LOGS;
            case "LOGS_THAT_BURN": return Tag.LOGS_THAT_BURN;
            case "PLANKS": return Tag.PLANKS;
            case "LEAVES": return Tag.LEAVES;
            case "WOOL": return Tag.WOOL;
            case "BUTTONS": return Tag.BUTTONS;
            case "DOORS": return Tag.DOORS;
            case "FENCES": return Tag.FENCES;
            case "FLOWERS": return Tag.FLOWERS;
            case "ICE": return Tag.ICE;
            case "SAND": return Tag.SAND;
            case "SLABS": return Tag.SLABS;
            case "STAIRS": return Tag.STAIRS;
            case "WALLS": return Tag.WALLS;
            default: return null;
        }
    }
}
