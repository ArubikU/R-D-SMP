package net.rollanddeath.smp.core.scripting.builtin.actions;

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
import org.bukkit.inventory.ItemStack;

/** Drops an item at a given location, supporting custom items and random stack sizes. */
public final class DropItemAtLocationAction {
    private DropItemAtLocationAction() {
    }

    static void register() {
        ActionRegistrar.register("drop_item_at_location", DropItemAtLocationAction::parse, "drop_item_at", "drop_item_naturally");
    }

    private static Action parse(Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key");
        if (where == null) return null;

        String material = Resolvers.string(null, raw, "material", "mat");
        String customItem = Resolvers.string(null, raw, "custom_item", "custom");
        Object itemsSpec = raw.get("items");
        Integer amount = Resolvers.integer(null, raw, "amount");
        Integer min = Resolvers.integer(null, raw, "min");
        Integer max = Resolvers.integer(null, raw, "max");
        boolean naturally = raw.get("naturally") instanceof Boolean b ? b : true;

        return ctx -> execute(ctx, where, material, customItem, itemsSpec, amount, min, max, naturally);
    }

    private static ActionResult execute(ScriptContext ctx, Object locationSpec, String materialRaw, String customItemRaw, Object itemsSpec, Integer amount, Integer min, Integer max, boolean naturally) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Location loc = Resolvers.location(ctx, locationSpec, ctx.player() != null ? ctx.player().getWorld() : null);
        if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

        java.util.List<ItemStack> stacksToDrop = new java.util.ArrayList<>();

        // 1. Single item from material/custom
        ItemStack singleStack = null;
        if (customItemRaw != null && !customItemRaw.isBlank()) {
            try {
                var item = plugin.getItemManager() != null ? plugin.getItemManager().getItem(customItemRaw) : null;
                if (item != null && item.getItemStack() != null) {
                    singleStack = item.getItemStack().clone();
                }
            } catch (Exception ignored) {
            }
        }

        if (singleStack == null && materialRaw != null && !materialRaw.isBlank()) {
            Material mat;
            try {
                mat = Material.valueOf(materialRaw.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                mat = null;
            }
            if (mat != null && mat != Material.AIR) {
                singleStack = new ItemStack(mat, 1);
            }
        }

        if (singleStack != null) {
            int lo;
            int hi;
            if (amount != null) {
                lo = Math.max(1, amount);
                hi = lo;
            } else {
                lo = min != null ? Math.max(1, min) : 1;
                hi = max != null ? Math.max(lo, max) : lo;
            }
            int finalAmt = lo == hi ? lo : ThreadLocalRandom.current().nextInt(lo, hi + 1);
            if (finalAmt > 0) {
                singleStack.setAmount(finalAmt);
                stacksToDrop.add(singleStack);
            }
        }

        // 2. List of items (e.g. from EVENT.drops)
        if (itemsSpec != null) {
            Object resolvedItems = Resolvers.resolve(ctx, itemsSpec);
            if (resolvedItems instanceof java.util.List<?> list) {
                for (Object o : list) {
                    if (o instanceof ItemStack is) {
                        stacksToDrop.add(is.clone());
                    }
                }
            } else if (resolvedItems instanceof ItemStack is) {
                stacksToDrop.add(is.clone());
            }
        }

        if (stacksToDrop.isEmpty()) return ActionResult.ALLOW;

        Location finalLoc = loc.clone();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                for (ItemStack s : stacksToDrop) {
                    if (s == null || s.getType() == Material.AIR || s.getAmount() <= 0) continue;
                    if (naturally) {
                        finalLoc.getWorld().dropItemNaturally(finalLoc, s);
                    } else {
                        finalLoc.getWorld().dropItem(finalLoc, s);
                    }
                }
            } catch (Exception ignored) {
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
