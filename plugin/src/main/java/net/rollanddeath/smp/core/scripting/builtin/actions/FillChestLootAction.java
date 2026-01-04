package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/** Fills a chest with provided loot definitions, optionally clearing first. */
public final class FillChestLootAction {
    private FillChestLootAction() {
    }

    static void register() {
        ActionRegistrar.register("fill_chest_loot", FillChestLootAction::parse, "fill_container_loot");
    }

    private record LootEntry(String material, String customItem, int min, int max, double chance) {
    }

    private static Action parse(Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key", "chest_key");
        if (where == null) return null;
        boolean clear = raw.get("clear") instanceof Boolean b ? b : false;
        boolean desort = raw.get("desort") instanceof Boolean b ? b : false;

        Object lootObj = raw.get("loot") != null ? raw.get("loot") : raw.get("items");
        List<LootEntry> entries = new ArrayList<>();
        if (lootObj instanceof List<?> list) {
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    LootEntry entry = toEntry(m);
                    if (entry != null) entries.add(entry);
                }
            }
        }
        if (entries.isEmpty()) return null;

        return ctx -> execute(ctx, where, entries, clear, desort);
    }

    private static LootEntry toEntry(Map<?, ?> m) {
        String material = m.get("material") instanceof String s ? s : null;
        String customItem = m.get("custom_item") instanceof String s ? s : null;
        Integer min = (m.get("min") instanceof Number n) ? n.intValue() : null;
        Integer max = (m.get("max") instanceof Number n) ? n.intValue() : null;
        Integer amount = (m.get("amount") instanceof Number n) ? n.intValue() : null;
        Double chance = (m.get("chance") instanceof Number n) ? n.doubleValue() : null;

        int lo;
        int hi;
        if (amount != null) {
            lo = Math.max(1, amount);
            hi = lo;
        } else {
            lo = min != null ? Math.max(1, min) : 1;
            hi = max != null ? Math.max(lo, max) : lo;
        }
        double p = chance != null ? Math.max(0.0, Math.min(1.0, chance)) : 1.0;
        return new LootEntry(material, customItem, lo, hi, p);
    }

    private static ActionResult execute(ScriptContext ctx, Object whereSpec, List<LootEntry> entries, boolean clear, boolean desort) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Location loc = Resolvers.location(ctx, whereSpec, ctx.player() != null ? ctx.player().getWorld() : null);
        if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Block block = loc.getBlock();
            if (!(block.getState() instanceof Chest chest)) return;

            if (clear) {
                try {
                    chest.getInventory().clear();
                } catch (Exception ignored) {
                }
            }

            for (LootEntry e : entries) {
                if (e == null) continue;
                if (e.chance < 1.0 && ThreadLocalRandom.current().nextDouble() >= e.chance) continue;

                int amt = e.min == e.max ? e.min : ThreadLocalRandom.current().nextInt(e.min, e.max + 1);
                if (amt <= 0) continue;

                if (e.customItem != null && !e.customItem.isBlank()) {
                    try {
                        var item = plugin.getItemManager() != null ? plugin.getItemManager().getItem(e.customItem) : null;
                        if (item != null) {
                            ItemStack stack = item.getItemStack();
                            if (stack != null) {
                                ItemStack toAdd = stack.clone();
                                toAdd.setAmount(Math.max(1, amt));
                                chest.getInventory().addItem(toAdd);
                                continue;
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (e.material == null || e.material.isBlank()) continue;
                Material mat;
                try {
                    mat = Material.valueOf(e.material.trim().toUpperCase(Locale.ROOT));
                } catch (Exception ignored) {
                    mat = null;
                }
                if (mat == null || mat == Material.AIR) continue;
                chest.getInventory().addItem(new ItemStack(mat, Math.max(1, amt)));
            }
        });

        return ActionResult.ALLOW;
    }

    private static Object firstNonNull(Map<?, ?> raw, String... keys) {
        for (String k : keys) {
            if (raw.containsKey(k) && raw.get(k) != null) {
                return raw.get(k);
            }
        }
        return null;
    }
}
