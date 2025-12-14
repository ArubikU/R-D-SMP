package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Map;

public class CraftingListener implements Listener {

    private final RollAndDeathSMP plugin;

    private static final Map<CustomItemType, Integer> MIN_DAY_REQUIREMENTS;
    private static final Map<String, CustomItemType> RECIPE_KEY_MAP;

    static {
        MIN_DAY_REQUIREMENTS = Map.ofEntries(
            Map.entry(CustomItemType.STEEL_HELMET, 10),
            Map.entry(CustomItemType.STEEL_CHESTPLATE, 10),
            Map.entry(CustomItemType.STEEL_LEGGINGS, 10),
            Map.entry(CustomItemType.STEEL_BOOTS, 10),
            Map.entry(CustomItemType.STEEL_SWORD, 10),
            Map.entry(CustomItemType.OBSIDIAN_HELMET, 20),
            Map.entry(CustomItemType.OBSIDIAN_CHESTPLATE, 20),
            Map.entry(CustomItemType.OBSIDIAN_LEGGINGS, 20),
            Map.entry(CustomItemType.OBSIDIAN_BOOTS, 20),
            Map.entry(CustomItemType.OBSIDIAN_SWORD, 20),
            Map.entry(CustomItemType.VOID_HELMET, 30),
            Map.entry(CustomItemType.VOID_CHESTPLATE, 30),
            Map.entry(CustomItemType.VOID_LEGGINGS, 30),
            Map.entry(CustomItemType.VOID_BOOTS, 30),
            Map.entry(CustomItemType.VOID_SWORD, 30)
        );

        RECIPE_KEY_MAP = Map.ofEntries(
            Map.entry("steel_helmet", CustomItemType.STEEL_HELMET),
            Map.entry("steel_chestplate", CustomItemType.STEEL_CHESTPLATE),
            Map.entry("steel_leggings", CustomItemType.STEEL_LEGGINGS),
            Map.entry("steel_boots", CustomItemType.STEEL_BOOTS),
            Map.entry("steel_sword", CustomItemType.STEEL_SWORD),

            Map.entry("obsidian_helmet", CustomItemType.OBSIDIAN_HELMET),
            Map.entry("obsidian_chestplate", CustomItemType.OBSIDIAN_CHESTPLATE),
            Map.entry("obsidian_leggings", CustomItemType.OBSIDIAN_LEGGINGS),
            Map.entry("obsidian_boots", CustomItemType.OBSIDIAN_BOOTS),
            Map.entry("obsidian_sword", CustomItemType.OBSIDIAN_SWORD),

            Map.entry("void_helmet", CustomItemType.VOID_HELMET),
            Map.entry("void_chestplate", CustomItemType.VOID_CHESTPLATE),
            Map.entry("void_leggings", CustomItemType.VOID_LEGGINGS),
            Map.entry("void_boots", CustomItemType.VOID_BOOTS),
            Map.entry("void_sword", CustomItemType.VOID_SWORD)
        );
    }

    public CraftingListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        CustomItemType type = resolveType(recipe, result);
        CustomItem item = type != null ? plugin.getItemManager().getItem(type) : getCustomItemFromStack(result);
        if (item == null) return;

        Player player = (Player) event.getView().getPlayer();
        RoleManager roleManager = plugin.getRoleManager();

        int currentDay = Math.max(1, plugin.getGameManager() != null ? plugin.getGameManager().getCurrentDay() : 1);
        Integer minDay = MIN_DAY_REQUIREMENTS.get(item.getType());
        if (minDay != null && currentDay < minDay) {
            event.getInventory().setResult(null);
            player.sendMessage(Component.text("Disponible para craftear desde el día " + minDay + ". Día actual: " + currentDay + ", usa la KillStore si ya está abierta.", NamedTextColor.RED));
            return;
        }

        RoleType requiredRole = item.getRequiredRoleType();
        RoleType playerRole = roleManager != null ? roleManager.getPlayerRole(player) : null;
        if (requiredRole != null && requiredRole != playerRole) {
            event.getInventory().setResult(null);
            player.sendMessage(Component.text("Solo el rol " + requiredRole.getName() + " puede craftear este ítem.", NamedTextColor.RED));
            return;
        }

        if (item.getType() == CustomItemType.RESURRECTION_ORB) {
            player.sendMessage(Component.text("⚠ ADVERTENCIA: Craftear este ítem te costará 1 VIDA.", NamedTextColor.RED));
        } else if (item.getType() == CustomItemType.SOUL_CONTRACT) {
            player.sendMessage(Component.text("⚠ ADVERTENCIA: Usar este ítem te BANEARÁ por 24 HORAS.", NamedTextColor.DARK_RED));
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() == null) return;
        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        CustomItemType type = resolveType(recipe, result);
        CustomItem item = type != null ? plugin.getItemManager().getItem(type) : getCustomItemFromStack(result);
        if (item == null) return;

        Player player = (Player) event.getWhoClicked();
        RoleManager roleManager = plugin.getRoleManager();

        int currentDay = Math.max(1, plugin.getGameManager() != null ? plugin.getGameManager().getCurrentDay() : 1);
        Integer minDay = MIN_DAY_REQUIREMENTS.get(item.getType());
        if (minDay != null && currentDay < minDay) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Aún no puedes craftear este ítem. Día mínimo: " + minDay, NamedTextColor.RED));
            return;
        }

        RoleType requiredRole = item.getRequiredRoleType();
        RoleType playerRole = roleManager != null ? roleManager.getPlayerRole(player) : null;
        if (requiredRole != null && requiredRole != playerRole) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Este crafteo requiere el rol " + requiredRole.getName() + ".", NamedTextColor.RED));
            return;
        }

        if (item.getType() == CustomItemType.RESURRECTION_ORB) {
            int lives = plugin.getLifeManager().getLives(player);
            if (lives <= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("No tienes suficientes vidas para craftear esto. Morirías permanentemente.", NamedTextColor.RED));
                return;
            }
            
            plugin.getLifeManager().removeLife(player);
            player.sendMessage(Component.text("Has sacrificado 1 vida para crear el Orbe de Resurrección.", NamedTextColor.RED));
        } 
        // Soul Contract no longer aplica baneos al craftear; se maneja al usar el ítem.
    }

    private CustomItem getCustomItemFromStack(ItemStack stack) {
        for (CustomItemType type : CustomItemType.values()) {
            CustomItem item = plugin.getItemManager().getItem(type);
            if (item != null && item.getItemStack().isSimilar(stack)) {
                return item;
            }
        }
        return null;
    }

    private CustomItemType resolveType(Recipe recipe, ItemStack result) {
        NamespacedKey key = (recipe instanceof Keyed keyed) ? keyed.getKey() : null;
        if (key != null) {
            CustomItemType byKey = RECIPE_KEY_MAP.get(key.getKey());
            if (byKey != null) {
                return byKey;
            }
        }
        CustomItem match = getCustomItemFromStack(result);
        return match != null ? match.getType() : null;
    }
}
