package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Map;

import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

final class SetBlockPropertyAction {
    private SetBlockPropertyAction() {}

    static void register() {
        ActionRegistrar.register("set_block_property", SetBlockPropertyAction::parse, "set_block", "block_prop");
    }

    private static Action parse(Map<?, ?> raw) {
        String property = Resolvers.string(null, raw, "property", "prop", "attribute");
        Object locationSpec = raw.get("location");
        if (locationSpec == null) locationSpec = raw.get("loc");
        if (locationSpec == null) locationSpec = raw.get("pos");
        Object valueSpec = raw.get("value");
        if (valueSpec == null) valueSpec = raw.get("val");
        if (valueSpec == null) valueSpec = raw.get("to");

        final String finalProperty = property;
        final Object finalLocationSpec = locationSpec;
        final Object finalValueSpec = valueSpec;

        return ctx -> {
            if (finalProperty == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(ctx, finalLocationSpec);
            if (loc == null && ctx.location() != null) {
                loc = ctx.location();
            }

            if (loc == null) return ActionResult.ALLOW;
            Block block = loc.getBlock();

            String prop = finalProperty.toLowerCase();
            Object value = Resolvers.resolve(ctx, finalValueSpec);

            switch (prop) {
                case "type", "material" -> {
                    Material mat = Resolvers.resolveMaterial(value);
                    if (mat != null && mat.isBlock()) {
                        block.setType(mat);
                    }
                }
                case "data", "block_data" -> {
                    if (value instanceof String s) {
                        try {
                            BlockData data = Bukkit.createBlockData(s);
                            block.setBlockData(data);
                        } catch (Exception ignored) {
                        }
                    }
                }
                case "biome" -> {
                    if (value instanceof String s) {
                        try {
                            String key = s.trim().toLowerCase();
                            if (!key.contains(":")) key = "minecraft:" + key;
                            org.bukkit.block.Biome biome = Registry.BIOME.get(NamespacedKey.fromString(key));
                            if (biome != null) block.setBiome(biome);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
