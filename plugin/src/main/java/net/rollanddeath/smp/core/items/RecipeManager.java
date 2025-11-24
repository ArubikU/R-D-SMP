package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeManager {

    private final RollAndDeathSMP plugin;

    public RecipeManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        registerHealingBandage();
        registerSharpeningStone();
        registerMoldyBread();
        registerEternalTorch();
        registerSharpStick();
        registerReinforcedLeatherBoots();
        registerMysteriousSoup();
        registerThornShield();
        registerBoneBow();
        registerGlassPickaxe();
        registerSmallBackpack();
        registerGrapplingHook();
        registerArmoredWings();
        registerInvisibilityCloak();
        registerTrueSightHelmet();
        registerResurrectionOrb();
    }

    private void registerHealingBandage() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.HEALING_BANDAGE).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "healing_bandage"), item);
        recipe.addIngredient(Material.PAPER);
        recipe.addIngredient(Material.WHITE_WOOL);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerSharpeningStone() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.SHARPENING_STONE).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "sharpening_stone"), item);
        recipe.addIngredient(Material.STONE);
        recipe.addIngredient(Material.FLINT);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerMoldyBread() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.MOLDY_BREAD).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "moldy_bread"), item);
        recipe.addIngredient(Material.BREAD);
        recipe.addIngredient(Material.BROWN_MUSHROOM);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerEternalTorch() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.ETERNAL_TORCH).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "eternal_torch"), item);
        recipe.addIngredient(Material.TORCH);
        recipe.addIngredient(Material.GLOWSTONE_DUST);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerSharpStick() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.SHARP_STICK).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "sharp_stick"), item);
        recipe.shape("F", "S");
        recipe.setIngredient('F', Material.FLINT);
        recipe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerReinforcedLeatherBoots() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.REINFORCED_LEATHER_BOOTS).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "reinforced_leather_boots"), item);
        recipe.shape("I I", "L L");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('L', Material.LEATHER_BOOTS);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerMysteriousSoup() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.MYSTERIOUS_SOUP).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "mysterious_soup"), item);
        recipe.addIngredient(Material.BOWL);
        recipe.addIngredient(Material.NETHER_WART);
        recipe.addIngredient(Material.SPIDER_EYE);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerThornShield() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.THORN_SHIELD).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "thorn_shield"), item);
        recipe.addIngredient(Material.SHIELD);
        recipe.addIngredient(Material.CACTUS);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerBoneBow() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.BONE_BOW).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "bone_bow"), item);
        recipe.shape(" BS", "B S", " BS");
        recipe.setIngredient('B', Material.BONE);
        recipe.setIngredient('S', Material.STRING);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerGlassPickaxe() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.GLASS_PICKAXE).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "glass_pickaxe"), item);
        recipe.shape("GGG", " S ", " S ");
        recipe.setIngredient('G', Material.GLASS);
        recipe.setIngredient('S', Material.STICK);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerSmallBackpack() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.SMALL_BACKPACK).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "small_backpack"), item);
        recipe.shape("LCL", "L L", "LLL");
        recipe.setIngredient('L', Material.LEATHER);
        recipe.setIngredient('C', Material.CHEST);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerGrapplingHook() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.GRAPPLING_HOOK).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "grappling_hook"), item);
        recipe.shape(" II", " SI", "S  ");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('S', Material.STRING);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerArmoredWings() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.ARMORED_WINGS).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "armored_wings"), item);
        recipe.shape("NEN", "N N", "N N");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('E', Material.ELYTRA);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerInvisibilityCloak() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.INVISIBILITY_CLOAK).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "invisibility_cloak"), item);
        recipe.shape("WWW", "WGW", "WWW");
        recipe.setIngredient('W', Material.BLACK_WOOL);
        recipe.setIngredient('G', Material.GLASS);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerTrueSightHelmet() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.TRUE_SIGHT_HELMET).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "true_sight_helmet"), item);
        recipe.shape("GEG", "G G");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('E', Material.ENDER_EYE);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerResurrectionOrb() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.RESURRECTION_ORB).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "resurrection_orb"), item);
        recipe.shape("DSD", "SOS", "DSD");
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('S', Material.NETHER_STAR);
        recipe.setIngredient('O', Material.GOLD_BLOCK);
        plugin.getServer().addRecipe(recipe);
    }
}
