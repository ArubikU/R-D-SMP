package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.recipes.RecipeRulePhase;
import net.rollanddeath.smp.core.items.recipes.RecipeRuleSet;
import net.rollanddeath.smp.core.items.recipes.UpgradeRecipe;
import net.rollanddeath.smp.core.items.scripted.ScriptedItem;
import net.rollanddeath.smp.core.items.scripted.ScriptedItemDefinition;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeAndAnvilListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final NamespacedKey customKey;

    public UpgradeAndAnvilListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.customKey = new NamespacedKey(plugin, "custom_item_id");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        var inv = event.getInventory();
        UpgradeRecipe recipe = findUpgrade(UpgradeRecipe.Station.SMITHING, inv.getItem(0), inv.getItem(1), inv.getItem(2));
        if (recipe == null) return;

        Player player = event.getView().getPlayer() instanceof Player p ? p : null;
        if (player == null) {
            event.setResult(null);
            return;
        }

        if (denyPrepare(recipe, player, event)) {
            event.setResult(null);
            return;
        }

        event.setResult(recipe.resultCopy());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmithItem(SmithItemEvent event) {
        var inv = event.getInventory();
        UpgradeRecipe recipe = findUpgrade(UpgradeRecipe.Station.SMITHING, inv.getItem(0), inv.getItem(1), inv.getItem(2));
        if (recipe == null) return;

        Player player = event.getWhoClicked() instanceof Player p ? p : null;
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (denyCraft(recipe, player, event)) {
            event.setCancelled(true);
            return;
        }

        event.setCurrentItem(recipe.resultCopy());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack left = inv.getItem(0);
        ItemStack right = inv.getItem(1);

        UpgradeRecipe recipe = findUpgrade(UpgradeRecipe.Station.ANVIL, null, left, right);
        if (recipe != null) {
            Player player = event.getView().getPlayer() instanceof Player p ? p : null;
            if (player == null || denyPrepare(recipe, player, event)) {
                event.setResult(null);
                return;
            }
            inv.setRepairCost(0);
            event.setResult(recipe.resultCopy());
            return;
        }

        // Política de reparación personalizada para items scripted.
        ScriptedItemDefinition def = getScriptedDefinition(left);
        if (def == null) return;

        ScriptedItemDefinition.AnvilRepairSpec spec = matchRepairSpec(def, right);
        if (spec != null) {
            ItemStack repaired = buildRepairResult(left, def, spec, right == null ? 1 : right.getAmount(), inv.getRenameText());
            if (repaired != null) {
                inv.setRepairCost(0);
                event.setResult(repaired);
            }
            return;
        }

        if (!def.allowAnvilRepair()) {
            event.setResult(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onAnvilResultClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        AnvilInventory inv = (AnvilInventory) event.getInventory();
        UpgradeRecipe recipe = findUpgrade(UpgradeRecipe.Station.ANVIL, null, inv.getItem(0), inv.getItem(1));
        if (recipe == null) return;

        Player player = event.getWhoClicked() instanceof Player p ? p : null;
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (denyCraft(recipe, player, event)) {
            event.setCancelled(true);
            event.setCurrentItem(null);
            return;
        }

        event.setCurrentItem(recipe.resultCopy());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSmithingResultClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.SMITHING) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        var inv = event.getInventory();
        UpgradeRecipe recipe = findUpgrade(UpgradeRecipe.Station.SMITHING, inv.getItem(0), inv.getItem(1), inv.getItem(2));
        if (recipe == null) return;

        Player player = event.getWhoClicked() instanceof Player p ? p : null;
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        if (denyCraft(recipe, player, event)) {
            event.setCancelled(true);
            event.setCurrentItem(null);
            return;
        }

        event.setCurrentItem(recipe.resultCopy());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMending(PlayerItemMendEvent event) {
        ScriptedItemDefinition def = getScriptedDefinition(event.getItem());
        if (def != null && !def.allowMending()) {
            event.setCancelled(true);
        }
    }

    private UpgradeRecipe findUpgrade(UpgradeRecipe.Station station, ItemStack template, ItemStack base, ItemStack addition) {
        List<UpgradeRecipe> upgrades = plugin.getRecipeManager().getUpgradeRecipes();
        for (UpgradeRecipe recipe : upgrades) {
            if (recipe.station() != station) continue;
            if (!matchesChoice(recipe.base(), base, false)) continue;
            if (!matchesChoice(recipe.addition(), addition, false)) continue;

            if (station == UpgradeRecipe.Station.SMITHING) {
                if (recipe.template() == null) {
                    if (template != null && template.getType() != Material.AIR) continue;
                } else if (!matchesChoice(recipe.template(), template, true)) {
                    continue;
                }
            }
            return recipe;
        }
        return null;
    }

    private boolean matchesChoice(RecipeChoice choice, ItemStack stack, boolean allowEmpty) {
        if (choice == null) return allowEmpty && (stack == null || stack.getType() == Material.AIR);
        if (stack == null) return false;

        // Permitir upgrades encadenados en ítems custom ignorando diferencias de PDC (tier).
        if (choice instanceof RecipeChoice.ExactChoice ec && !ec.getChoices().isEmpty()) {
            String wanted = resolveCustomId(ec.getChoices().get(0));
            String got = resolveCustomId(stack);
            if (wanted != null && wanted.equalsIgnoreCase(got)) {
                return true;
            }
        }

        try {
            return choice.test(stack);
        } catch (NoSuchMethodError ignored) {
            // Fallback for APIs sin RecipeChoice#test
            if (choice instanceof RecipeChoice.MaterialChoice mc) {
                return mc.getChoices().contains(stack.getType());
            }
            if (choice instanceof RecipeChoice.ExactChoice ec) {
                for (ItemStack candidate : ec.getChoices()) {
                    if (candidate.isSimilar(stack)) return true;
                }
                return false;
            }
            return false;
        }
    }

    private boolean denyPrepare(UpgradeRecipe recipe, Player player, Object nativeEvent) {
        RecipeRuleSet rules = plugin.getRecipeManager().getRuleSet(recipe.key());
        if (rules == null || rules.prepare() == null) return false;
        return applyRulesPhase(rules.prepare(), player, recipe, ScriptPhase.PREPARE, nativeEvent);
    }

    private boolean denyCraft(UpgradeRecipe recipe, Player player, Object nativeEvent) {
        RecipeRuleSet rules = plugin.getRecipeManager().getRuleSet(recipe.key());
        if (rules == null || rules.craft() == null) return false;
        return applyRulesPhase(rules.craft(), player, recipe, ScriptPhase.CRAFT, nativeEvent);
    }

    private boolean applyRulesPhase(RecipeRulePhase phase, Player player, UpgradeRecipe recipe, ScriptPhase scriptPhase, Object nativeEvent) {
        Map<String, Object> ev = new HashMap<>();
        ev.put("type", scriptPhase == ScriptPhase.PREPARE ? "prepare_upgrade" : "craft_upgrade");
        ev.put("__native", nativeEvent);
        ev.put("recipe", recipe.key().toString());
        ev.put("recipeKey", recipe.key().getKey());
        ev.put("recipeNamespace", recipe.key().getNamespace());

        Map<String, Object> vars = ScriptVars.create()
            .subject(player)
            .item(recipe.resultCopy())
            .event(ev)
            .build();

        ScriptContext ctx = new ScriptContext(plugin, player, recipe.key().toString(), scriptPhase, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, phase.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, phase.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, phase.onFail());
        return phase.denyOnFail() || (r != null && r.deny());
    }

    private ScriptedItemDefinition getScriptedDefinition(ItemStack stack) {
        String id = resolveCustomId(stack);
        if (id == null) return null;
        CustomItem custom = plugin.getItemManager().getItem(id);
        if (custom instanceof ScriptedItem scripted) {
            return scripted.getDefinition();
        }
        return null;
    }

    private String resolveCustomId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        ItemMeta meta = stack.getItemMeta();
        return meta.getPersistentDataContainer().get(customKey, PersistentDataType.STRING);
    }

    private ScriptedItemDefinition.AnvilRepairSpec matchRepairSpec(ScriptedItemDefinition def, ItemStack addition) {
        if (addition == null || def.anvilItems() == null || def.anvilItems().isEmpty()) return null;
        String additionId = resolveCustomId(addition);
        Material type = addition.getType();

        for (ScriptedItemDefinition.AnvilRepairSpec spec : def.anvilItems()) {
            if (spec == null) continue;
            if (spec.isMaterial() && spec.material() == type) return spec;
            if (spec.isCustom() && additionId != null && spec.customId().equalsIgnoreCase(additionId)) return spec;
        }
        return null;
    }

    private ItemStack buildRepairResult(ItemStack base, ScriptedItemDefinition def, ScriptedItemDefinition.AnvilRepairSpec spec, int additionCount, String renameText) {
        if (!(base.getItemMeta() instanceof Damageable damageable)) return null;
        int maxDamage = def.maxDamage() != null && def.maxDamage() > 0 ? def.maxDamage() : base.getType().getMaxDurability();
        if (maxDamage <= 0) return null;

        int perItem = (int) Math.ceil(maxDamage * (spec.percent() / 100.0));
        if (perItem < 1) perItem = 1;
        int total = perItem * Math.max(1, additionCount);

        ItemStack repaired = base.clone();
        Damageable meta = (Damageable) repaired.getItemMeta();
        int newDamage = Math.max(0, damageable.getDamage() - total);
        meta.setDamage(newDamage);

        if (renameText != null && !renameText.isBlank()) {
            try {
                meta.setDisplayName(renameText);
            } catch (Exception ignored) {
            }
        }

        repaired.setItemMeta(meta);
        return repaired;
    }
}
