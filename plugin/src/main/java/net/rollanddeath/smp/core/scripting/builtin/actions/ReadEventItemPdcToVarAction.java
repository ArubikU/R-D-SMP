package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

final class ReadEventItemPdcToVarAction {
    private ReadEventItemPdcToVarAction() {
    }

    static void register() {
        ActionRegistrar.register("read_item_pdc_to_var", ReadEventItemPdcToVarAction::parse, "read_pdc_to_var");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object pdcKeySpec = Resolvers.plain(raw, "pdc_key", "key", "namespaced_key");
        Object dataTypeSpec = Resolvers.plain(raw, "data_type", "type", "pdc_type");
        Object storeKeySpec = Resolvers.plain(raw, "store_key", "var", "target_key");
        if (pdcKeySpec == null || storeKeySpec == null) return null;
        return ctx -> execute(ctx, pdcKeySpec, dataTypeSpec, storeKeySpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object pdcKeySpec, Object dataTypeSpec, Object storeKeySpec) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        String pdcKey = Resolvers.string(ctx, pdcKeySpec);
        if (pdcKey == null || pdcKey.isBlank()) return ActionResult.ALLOW;
        String storeKey = Resolvers.string(ctx, storeKeySpec);
        if (storeKey == null || storeKey.isBlank()) return ActionResult.ALLOW;

        ItemStack item = ctx.item();
        if (item == null) {
            try {
                var pie = ctx.nativeEvent(org.bukkit.event.player.PlayerInteractEvent.class);
                if (pie != null) item = pie.getItem();
            } catch (Exception ignored) {
            }
        }
        if (item == null) {
            try {
                var pce = ctx.nativeEvent(org.bukkit.event.player.PlayerItemConsumeEvent.class);
                if (pce != null) item = pce.getItem();
            } catch (Exception ignored) {
            }
        }
        if (item == null) return ActionResult.ALLOW;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return ActionResult.ALLOW;

        NamespacedKey key;
        String rawKey = pdcKey.trim();
        if (rawKey.isBlank()) return ActionResult.ALLOW;
        if (rawKey.contains(":")) {
            key = NamespacedKey.fromString(rawKey);
        } else {
            key = new NamespacedKey(plugin, rawKey);
        }
        if (key == null) return ActionResult.ALLOW;

        String dtRaw = Resolvers.string(ctx, dataTypeSpec);
        String dt = dtRaw != null ? dtRaw.trim().toUpperCase(Locale.ROOT) : null;
        Object out = null;

        try {
            var pdc = meta.getPersistentDataContainer();

            if (dt == null || dt.isBlank()) {
                Double d = pdc.get(key, PersistentDataType.DOUBLE);
                if (d != null) out = d;
                if (out == null) {
                    Integer i = pdc.get(key, PersistentDataType.INTEGER);
                    if (i != null) out = i.doubleValue();
                }
                if (out == null) {
                    Long l = pdc.get(key, PersistentDataType.LONG);
                    if (l != null) out = l.doubleValue();
                }
                if (out == null) {
                    String s = pdc.get(key, PersistentDataType.STRING);
                    if (s != null) out = s;
                }
                if (out == null) {
                    Byte b = pdc.get(key, PersistentDataType.BYTE);
                    if (b != null) out = b != 0;
                }
            } else {
                switch (dt) {
                    case "DOUBLE" -> out = pdc.get(key, PersistentDataType.DOUBLE);
                    case "INT", "INTEGER" -> {
                        Integer i = pdc.get(key, PersistentDataType.INTEGER);
                        out = i != null ? i.doubleValue() : null;
                    }
                    case "LONG" -> {
                        Long l = pdc.get(key, PersistentDataType.LONG);
                        out = l != null ? l.doubleValue() : null;
                    }
                    case "STRING", "STR" -> out = pdc.get(key, PersistentDataType.STRING);
                    case "BOOLEAN", "BOOL" -> {
                        Byte b = pdc.get(key, PersistentDataType.BYTE);
                        out = b != null ? b != 0 : null;
                    }
                    default -> {
                        try {
                            out = pdc.get(key, PersistentDataType.STRING);
                        } catch (Exception ignored) {
                            out = null;
                        }
                    }
                }
            }

            if (out != null) {
                ctx.setGenericVarCompat(storeKey, out);
            }
        } catch (Exception ignored) {
        }

        return ActionResult.ALLOW;
    }
}