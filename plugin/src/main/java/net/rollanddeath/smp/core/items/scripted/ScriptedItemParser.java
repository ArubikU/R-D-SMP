package net.rollanddeath.smp.core.items.scripted;

import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScriptedItemParser {

    private ScriptedItemParser() {
    }

    public static Map<String, ScriptedItemDefinition> parseAll(ConfigurationSection root) {
        Map<String, ScriptedItemDefinition> out = new HashMap<>();
        if (root == null) return out;

        ConfigurationSection items = root.getConfigurationSection("items");
        if (items == null) return out;

        for (String id : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(id);
            if (sec == null) continue;

            if (!sec.getBoolean("enabled", true)) {
                continue;
            }

            // String typeRaw = sec.getString("type", id);
            // Ignoramos type, usamos id como identificador único.

            String baseMatRaw = sec.getString("base_material");
            if (baseMatRaw == null || baseMatRaw.isBlank()) continue;

            Material baseMat;
            try {
                baseMat = Material.valueOf(baseMatRaw.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                continue;
            }

            String displayName = sec.getString("name");
            Integer cmd = sec.getInt("custom_model_data", -1);
            if (cmd != null && cmd < 0) cmd = null;

            Integer maxStackSize = sec.getInt("max_stack_size", -1);
            if (maxStackSize != null && maxStackSize < 1) maxStackSize = null;

            Integer maxDamage = sec.getInt("max_damage", -1);
            if (maxDamage != null && maxDamage < 1) maxDamage = null;

            List<String> lore = sec.getStringList("lore");

            String leatherColor = sec.getString("leather_color");

            List<ScriptedItemDefinition.PdcSpec> pdc = new ArrayList<>();
            for (Map<?, ?> raw : sec.getMapList("pdc")) {
                ScriptedItemDefinition.PdcSpec spec = parsePdc(raw);
                if (spec != null) pdc.add(spec);
            }

            List<ScriptedItemDefinition.EnchantmentSpec> enchants = parseEnchantments(sec);

            List<ScriptedItemDefinition.AttributeSpec> attrs = new ArrayList<>();
            for (Map<?, ?> raw : sec.getMapList("attributes")) {
                ScriptedItemDefinition.AttributeSpec spec = parseAttribute(raw);
                if (spec != null) attrs.add(spec);
            }

            Map<String, ModifierRule> events = new HashMap<>();
            ConfigurationSection eventsSec = sec.getConfigurationSection("events");
            if (eventsSec != null) {
                for (String eventName : eventsSec.getKeys(false)) {
                    ConfigurationSection eventSec = eventsSec.getConfigurationSection(eventName);
                    if (eventSec == null) continue;
                    ModifierRule rule = parseRule(eventSec);
                    if (rule != null) {
                        events.put(eventName.trim().toLowerCase(Locale.ROOT), rule);
                    }
                }
            }

            out.put(id, new ScriptedItemDefinition(id, baseMat, displayName, cmd, maxStackSize, maxDamage, lore, leatherColor, pdc, enchants, attrs, events));
        }

        return out;
    }

    private static List<ScriptedItemDefinition.EnchantmentSpec> parseEnchantments(ConfigurationSection sec) {
        if (sec == null) return List.of();

        Object rawObj = sec.get("enchantments");
        if (rawObj == null) return List.of();

        List<ScriptedItemDefinition.EnchantmentSpec> out = new ArrayList<>();

        // Soporta:
        // enchantments:
        //   - { enchantment: THORNS, level: 3 }
        //   - { enchant: UNBREAKING, level: 3 }
        // y también:
        // enchantments:
        //   THORNS: 3
        //   UNBREAKING: 3
        if (rawObj instanceof List<?> list) {
            for (Object o : list) {
                if (!(o instanceof Map<?, ?> m)) continue;
                String enchRaw = m.get("enchantment") instanceof String s ? s : (m.get("enchant") instanceof String s2 ? s2 : null);
                Object lvlObj = m.get("level");
                if (enchRaw == null || enchRaw.isBlank() || lvlObj == null) continue;

                int lvl;
                if (lvlObj instanceof Number n) {
                    lvl = n.intValue();
                } else {
                    try {
                        lvl = Integer.parseInt(String.valueOf(lvlObj).trim());
                    } catch (Exception ignored) {
                        continue;
                    }
                }
                if (lvl < 1) continue;

                Enchantment ench;
                try {
                    ench = Enchantment.getByName(enchRaw.trim().toUpperCase(Locale.ROOT));
                } catch (Exception ignored) {
                    ench = null;
                }
                if (ench == null) continue;

                out.add(new ScriptedItemDefinition.EnchantmentSpec(ench, lvl));
            }
            return out;
        }

        if (rawObj instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (!(e.getKey() instanceof String enchRaw) || enchRaw.isBlank()) continue;
                Object lvlObj = e.getValue();
                if (lvlObj == null) continue;

                int lvl;
                if (lvlObj instanceof Number n) {
                    lvl = n.intValue();
                } else {
                    try {
                        lvl = Integer.parseInt(String.valueOf(lvlObj).trim());
                    } catch (Exception ignored) {
                        continue;
                    }
                }
                if (lvl < 1) continue;

                Enchantment ench;
                try {
                    ench = Enchantment.getByName(enchRaw.trim().toUpperCase(Locale.ROOT));
                } catch (Exception ignored) {
                    ench = null;
                }
                if (ench == null) continue;

                out.add(new ScriptedItemDefinition.EnchantmentSpec(ench, lvl));
            }
        }

        return out;
    }

    private static ScriptedItemDefinition.AttributeSpec parseAttribute(Map<?, ?> raw) {
        if (raw == null) return null;

        Object attrObj = raw.get("attribute");
        Object amountObj = raw.get("amount");
        if (!(attrObj instanceof String attrRaw) || attrRaw.isBlank() || amountObj == null) return null;

        Double amount;
        if (amountObj instanceof Number n) {
            amount = n.doubleValue();
        } else {
            try {
                amount = Double.parseDouble(String.valueOf(amountObj));
            } catch (Exception ignored) {
                return null;
            }
        }

        Attribute attribute;
        try {
            attribute = Attribute.valueOf(attrRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }

        String opRaw = raw.get("operation") instanceof String s ? s : "ADD_NUMBER";
        AttributeModifier.Operation op;
        try {
            op = AttributeModifier.Operation.valueOf(opRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            op = AttributeModifier.Operation.ADD_NUMBER;
        }

        String slotRaw = raw.get("slot") instanceof String s ? s : "MAINHAND";
        EquipmentSlotGroup slot = parseEquipmentSlotGroup(slotRaw);

        String key = raw.get("key") instanceof String s ? s : null;

        return new ScriptedItemDefinition.AttributeSpec(attribute, amount, op, slot, key);
    }

    private static ScriptedItemDefinition.PdcSpec parsePdc(Map<?, ?> raw) {
        if (raw == null) return null;
        Object keyObj = raw.get("key");
        Object valueObj = raw.get("value");
        if (!(keyObj instanceof String key) || key.isBlank() || valueObj == null) return null;
        String type = raw.get("type") instanceof String s ? s : (raw.get("data_type") instanceof String s2 ? s2 : null);
        return new ScriptedItemDefinition.PdcSpec(key.trim(), type != null ? type.trim() : null, valueObj);
    }

    private static EquipmentSlotGroup parseEquipmentSlotGroup(String name) {
        if (name == null || name.isBlank()) return EquipmentSlotGroup.MAINHAND;
        String n = name.trim().toUpperCase(Locale.ROOT);

        // Alias comunes
        if ("HAND".equals(n) || "MAIN_HAND".equals(n) || "MAIN".equals(n)) n = "MAINHAND";
        if ("HELMET".equals(n)) n = "HEAD";
        if ("CHESTPLATE".equals(n)) n = "CHEST";
        if ("LEGGINGS".equals(n)) n = "LEGS";
        if ("BOOTS".equals(n)) n = "FEET";

        // EquipmentSlotGroup no es enum en algunas APIs, así que evitamos valueOf.
        return switch (n) {
            case "ANY" -> EquipmentSlotGroup.ANY;
            case "MAINHAND" -> EquipmentSlotGroup.MAINHAND;
            case "HEAD" -> EquipmentSlotGroup.HEAD;
            case "CHEST" -> EquipmentSlotGroup.CHEST;
            case "LEGS" -> EquipmentSlotGroup.LEGS;
            case "FEET" -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.MAINHAND;
        };
    }

    private static ModifierRule parseRule(ConfigurationSection sec) {
        boolean denyOnFail = sec.getBoolean("deny_on_fail", false);

        List<Condition> requireAll = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("require_all")) {
            Condition c = ConditionRegistrar.parse(raw);
            if (c != null) requireAll.add(c);
        }

        List<Action> onFail = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("on_fail")) {
            Action a = ActionRegistrar.parse(raw);
            if (a != null) onFail.add(a);
        }

        List<Action> onPass = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("on_pass")) {
            Action a = ActionRegistrar.parse(raw);
            if (a != null) onPass.add(a);
        }

        return new ModifierRule(denyOnFail, requireAll, onFail, onPass);
    }
}
