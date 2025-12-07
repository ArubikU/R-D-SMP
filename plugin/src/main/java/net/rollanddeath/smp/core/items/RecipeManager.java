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
        registerLifeGapple();
        registerSteelGear();
        registerObsidianGear();
        registerVoidGear();
        registerCustomSwords();
        registerRoleItems();
        registerLokiDice();
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
        recipe.shape("GEG", "G G", "   ");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('E', Material.ENDER_PEARL);
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

    private void registerLifeGapple() {
        ItemStack item = plugin.getItemManager().getItem(CustomItemType.LIFE_GAPPLE).getItemStack();
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "life_gapple"), item);
        recipe.shape("HTH", "TNT", "HTH");
        recipe.setIngredient('H', Material.PLAYER_HEAD);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        recipe.setIngredient('N', Material.ENCHANTED_GOLDEN_APPLE);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerSteelGear() {
        ItemStack helmet = plugin.getItemManager().getItem(CustomItemType.STEEL_HELMET).getItemStack();
        ShapedRecipe helmetRecipe = new ShapedRecipe(new NamespacedKey(plugin, "steel_helmet"), helmet);
        helmetRecipe.shape("BFB", "C C", "I I");
        helmetRecipe.setIngredient('B', Material.IRON_BLOCK);
        helmetRecipe.setIngredient('F', Material.BLAST_FURNACE);
        helmetRecipe.setIngredient('C', Material.CHAIN);
        helmetRecipe.setIngredient('I', Material.IRON_INGOT);
        plugin.getServer().addRecipe(helmetRecipe);

        ItemStack chest = plugin.getItemManager().getItem(CustomItemType.STEEL_CHESTPLATE).getItemStack();
        ShapedRecipe chestRecipe = new ShapedRecipe(new NamespacedKey(plugin, "steel_chestplate"), chest);
        chestRecipe.shape("BFB", "BCB", "BIB");
        chestRecipe.setIngredient('B', Material.IRON_BLOCK);
        chestRecipe.setIngredient('F', Material.BLAST_FURNACE);
        chestRecipe.setIngredient('C', Material.CHAINMAIL_CHESTPLATE);
        chestRecipe.setIngredient('I', Material.IRON_INGOT);
        plugin.getServer().addRecipe(chestRecipe);

        ItemStack leggings = plugin.getItemManager().getItem(CustomItemType.STEEL_LEGGINGS).getItemStack();
        ShapedRecipe legRecipe = new ShapedRecipe(new NamespacedKey(plugin, "steel_leggings"), leggings);
        legRecipe.shape("BFB", "C C", "BIB");
        legRecipe.setIngredient('B', Material.IRON_BLOCK);
        legRecipe.setIngredient('F', Material.BLAST_FURNACE);
        legRecipe.setIngredient('C', Material.CHAIN);
        legRecipe.setIngredient('I', Material.IRON_INGOT);
        plugin.getServer().addRecipe(legRecipe);

        ItemStack boots = plugin.getItemManager().getItem(CustomItemType.STEEL_BOOTS).getItemStack();
        ShapedRecipe bootsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "steel_boots"), boots);
        bootsRecipe.shape("C C", "BFB", "I I");
        bootsRecipe.setIngredient('C', Material.CHAIN);
        bootsRecipe.setIngredient('B', Material.IRON_BLOCK);
        bootsRecipe.setIngredient('F', Material.BLAST_FURNACE);
        bootsRecipe.setIngredient('I', Material.IRON_INGOT);
        plugin.getServer().addRecipe(bootsRecipe);
    }

    private void registerObsidianGear() {
        ItemStack helmet = plugin.getItemManager().getItem(CustomItemType.OBSIDIAN_HELMET).getItemStack();
        ShapedRecipe helmetRecipe = new ShapedRecipe(new NamespacedKey(plugin, "obsidian_helmet"), helmet);
        helmetRecipe.shape("DND", "OSO", "   ");
        helmetRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        helmetRecipe.setIngredient('N', Material.NETHERITE_SCRAP);
        helmetRecipe.setIngredient('O', Material.OBSIDIAN);
        helmetRecipe.setIngredient('S', Material.SMITHING_TABLE);
        plugin.getServer().addRecipe(helmetRecipe);

        ItemStack chest = plugin.getItemManager().getItem(CustomItemType.OBSIDIAN_CHESTPLATE).getItemStack();
        ShapedRecipe chestRecipe = new ShapedRecipe(new NamespacedKey(plugin, "obsidian_chestplate"), chest);
        chestRecipe.shape("DND", "OSO", "DOD");
        chestRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        chestRecipe.setIngredient('N', Material.NETHERITE_SCRAP);
        chestRecipe.setIngredient('O', Material.OBSIDIAN);
        chestRecipe.setIngredient('S', Material.SMITHING_TABLE);
        plugin.getServer().addRecipe(chestRecipe);

        ItemStack leggings = plugin.getItemManager().getItem(CustomItemType.OBSIDIAN_LEGGINGS).getItemStack();
        ShapedRecipe legRecipe = new ShapedRecipe(new NamespacedKey(plugin, "obsidian_leggings"), leggings);
        legRecipe.shape("DND", "O O", "DOD");
        legRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        legRecipe.setIngredient('N', Material.NETHERITE_SCRAP);
        legRecipe.setIngredient('O', Material.OBSIDIAN);
        plugin.getServer().addRecipe(legRecipe);

        ItemStack boots = plugin.getItemManager().getItem(CustomItemType.OBSIDIAN_BOOTS).getItemStack();
        ShapedRecipe bootsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "obsidian_boots"), boots);
        bootsRecipe.shape("O O", "DND", "O O");
        bootsRecipe.setIngredient('O', Material.OBSIDIAN);
        bootsRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        bootsRecipe.setIngredient('N', Material.NETHERITE_SCRAP);
        plugin.getServer().addRecipe(bootsRecipe);
    }

    private void registerVoidGear() {
        ItemStack helmet = plugin.getItemManager().getItem(CustomItemType.VOID_HELMET).getItemStack();
        ShapedRecipe helmetRecipe = new ShapedRecipe(new NamespacedKey(plugin, "void_helmet"), helmet);
        helmetRecipe.shape("CNC", "SES", "   ");
        helmetRecipe.setIngredient('C', Material.CRYING_OBSIDIAN);
        helmetRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        helmetRecipe.setIngredient('S', Material.ECHO_SHARD);
        helmetRecipe.setIngredient('E', Material.END_CRYSTAL);
        plugin.getServer().addRecipe(helmetRecipe);

        ItemStack chest = plugin.getItemManager().getItem(CustomItemType.VOID_CHESTPLATE).getItemStack();
        ShapedRecipe chestRecipe = new ShapedRecipe(new NamespacedKey(plugin, "void_chestplate"), chest);
        chestRecipe.shape("CNC", "SES", "CNC");
        chestRecipe.setIngredient('C', Material.CRYING_OBSIDIAN);
        chestRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        chestRecipe.setIngredient('S', Material.ECHO_SHARD);
        chestRecipe.setIngredient('E', Material.END_CRYSTAL);
        plugin.getServer().addRecipe(chestRecipe);

        ItemStack leggings = plugin.getItemManager().getItem(CustomItemType.VOID_LEGGINGS).getItemStack();
        ShapedRecipe legRecipe = new ShapedRecipe(new NamespacedKey(plugin, "void_leggings"), leggings);
        legRecipe.shape("CNC", "S S", "CNC");
        legRecipe.setIngredient('C', Material.CRYING_OBSIDIAN);
        legRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        legRecipe.setIngredient('S', Material.ECHO_SHARD);
        plugin.getServer().addRecipe(legRecipe);

        ItemStack boots = plugin.getItemManager().getItem(CustomItemType.VOID_BOOTS).getItemStack();
        ShapedRecipe bootsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "void_boots"), boots);
        bootsRecipe.shape("S S", "CNC", "N N");
        bootsRecipe.setIngredient('S', Material.ECHO_SHARD);
        bootsRecipe.setIngredient('C', Material.CRYING_OBSIDIAN);
        bootsRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        plugin.getServer().addRecipe(bootsRecipe);
    }

    private void registerCustomSwords() {
        ItemStack steel = plugin.getItemManager().getItem(CustomItemType.STEEL_SWORD).getItemStack();
        ShapedRecipe steelRecipe = new ShapedRecipe(new NamespacedKey(plugin, "steel_sword"), steel);
        steelRecipe.shape(" B ", " F ", " I ");
        steelRecipe.setIngredient('B', Material.IRON_BLOCK);
        steelRecipe.setIngredient('F', Material.BLAST_FURNACE);
        steelRecipe.setIngredient('I', Material.IRON_INGOT);
        plugin.getServer().addRecipe(steelRecipe);

        ItemStack obsidian = plugin.getItemManager().getItem(CustomItemType.OBSIDIAN_SWORD).getItemStack();
        ShapedRecipe obsRecipe = new ShapedRecipe(new NamespacedKey(plugin, "obsidian_sword"), obsidian);
        obsRecipe.shape(" O ", " D ", " N ");
        obsRecipe.setIngredient('O', Material.OBSIDIAN);
        obsRecipe.setIngredient('D', Material.DIAMOND_SWORD);
        obsRecipe.setIngredient('N', Material.NETHERITE_SCRAP);
        plugin.getServer().addRecipe(obsRecipe);

        ItemStack voidSword = plugin.getItemManager().getItem(CustomItemType.VOID_SWORD).getItemStack();
        ShapedRecipe voidRecipe = new ShapedRecipe(new NamespacedKey(plugin, "void_sword"), voidSword);
        voidRecipe.shape(" C ", "NS ", " E ");
        voidRecipe.setIngredient('C', Material.CRYING_OBSIDIAN);
        voidRecipe.setIngredient('N', Material.NETHERITE_SWORD);
        voidRecipe.setIngredient('S', Material.ECHO_SHARD);
        voidRecipe.setIngredient('E', Material.END_CRYSTAL);
        plugin.getServer().addRecipe(voidRecipe);
    }

    private void registerRoleItems() {
        addShapeless(CustomItemType.PACIFIST_BALM, "pacifist_balm",
                Material.HONEY_BOTTLE, Material.GHAST_TEAR, Material.MILK_BUCKET);
        addShapeless(CustomItemType.VAMPIRE_VIAL, "vampire_vial",
                Material.GLASS_BOTTLE, Material.FERMENTED_SPIDER_EYE, Material.NETHER_WART, Material.REDSTONE);
        addShapeless(CustomItemType.GLASS_CANNON_CHARGE, "glass_cannon_charge",
                Material.AMETHYST_SHARD, Material.TNT, Material.GLOWSTONE_DUST);
        addShapeless(CustomItemType.MIDAS_TALISMAN, "midas_talisman",
                Material.GOLD_BLOCK, Material.COPPER_INGOT, Material.EMERALD);
        addShapeless(CustomItemType.NOMAD_COMPASS, "nomad_compass",
                Material.COMPASS, Material.PHANTOM_MEMBRANE, Material.RABBIT_FOOT);
        addShapeless(CustomItemType.TANK_PLATING, "tank_plating",
                Material.IRON_BLOCK, Material.SHIELD, Material.TURTLE_HELMET);
        addShapeless(CustomItemType.ASSASSIN_POISON_KIT, "assassin_poison_kit",
                Material.FERMENTED_SPIDER_EYE, Material.ARROW, Material.BLACK_DYE);
        addShapeless(CustomItemType.ENGINEER_REPAIR_KIT, "engineer_repair_kit",
                Material.REDSTONE, Material.IRON_INGOT, Material.ANVIL);
        addShapeless(CustomItemType.PYRO_TORCH, "pyro_torch",
                Material.FLINT_AND_STEEL, Material.BLAZE_POWDER, Material.MAGMA_CREAM);
        addShapeless(CustomItemType.CURSED_IDOL, "cursed_idol",
                Material.WITHER_ROSE, Material.ENDER_PEARL, Material.ROTTEN_FLESH);
        addShapeless(CustomItemType.DRUID_SEED, "druid_seed",
                Material.MOSS_BLOCK, Material.WHEAT_SEEDS, Material.BONE_MEAL);
        addShapeless(CustomItemType.BERSERKER_MEAD, "berserker_mead",
                Material.HONEY_BOTTLE, Material.BLAZE_POWDER, Material.COOKED_BEEF);
        addShapeless(CustomItemType.SNIPER_SCOPE, "sniper_scope",
                Material.SPYGLASS, Material.FEATHER, Material.STRING);
        addShapeless(CustomItemType.MERCHANT_LEDGER, "merchant_ledger",
                Material.BOOK, Material.EMERALD, Material.GOLD_INGOT);
        addShapeless(CustomItemType.GHOST_VEIL, "ghost_veil",
                Material.WHITE_STAINED_GLASS_PANE, Material.PHANTOM_MEMBRANE, Material.SNOWBALL);
        addShapeless(CustomItemType.AQUATIC_GILLS, "aquatic_gills",
                Material.PRISMARINE_SHARD, Material.KELP, Material.TROPICAL_FISH);
        addShapeless(CustomItemType.MINER_CHARGE, "miner_charge",
                Material.TNT, Material.IRON_PICKAXE, Material.TORCH);
        addShapeless(CustomItemType.TAMER_WHISTLE, "tamer_whistle",
                Material.BONE, Material.LEAD, Material.COOKED_CHICKEN);
        addShapeless(CustomItemType.ALCHEMIST_CATALYST, "alchemist_catalyst",
                Material.NETHER_WART, Material.REDSTONE, Material.GLOWSTONE_DUST);
        addShapeless(CustomItemType.KNIGHT_BANNER, "knight_banner",
                Material.WHITE_BANNER, Material.IRON_SWORD, Material.SHIELD);
        addShapeless(CustomItemType.THIEF_LOCKPICKS, "thief_lockpicks",
                Material.TRIPWIRE_HOOK, Material.IRON_NUGGET, Material.ENDER_PEARL);
        addShapeless(CustomItemType.GIANT_BRACER, "giant_bracer",
                Material.IRON_BLOCK, Material.LEATHER, Material.BEETROOT_SOUP);
        addShapeless(CustomItemType.DWARF_FORGE_HAMMER, "dwarf_forge_hammer",
                Material.IRON_PICKAXE, Material.COAL, Material.ANVIL);
        addShapeless(CustomItemType.ILLUSIONIST_MIRROR, "illusionist_mirror",
                Material.GLASS_PANE, Material.LAPIS_LAZULI, Material.ENDER_PEARL);
        addShapeless(CustomItemType.BARBARIAN_MEAD, "barbarian_mead",
                Material.COOKED_PORKCHOP, Material.HONEY_BOTTLE, Material.BLAZE_POWDER);
        addShapeless(CustomItemType.SAGE_SCROLL, "sage_scroll",
                Material.PAPER, Material.LAPIS_LAZULI, Material.AMETHYST_SHARD);
        addShapeless(CustomItemType.CHAOTIC_SEAL, "chaotic_seal",
                Material.ECHO_SHARD, Material.REDSTONE, Material.GUNPOWDER);
        addShapeless(CustomItemType.GUARDIAN_SIGIL, "guardian_sigil",
                Material.SHIELD, Material.IRON_INGOT, Material.HEART_OF_THE_SEA);
        addShapeless(CustomItemType.EXPLORER_COMPASS, "explorer_compass",
                Material.MAP, Material.FEATHER, Material.SWEET_BERRIES);
        addShapeless(CustomItemType.COOK_SPICE_BLEND, "cook_spice_blend",
                Material.BOWL, Material.CARROT, Material.POTATO, Material.BEETROOT);

        registerLokiRoleDice();
    }

    private void addShapeless(CustomItemType type, String key, Material... ingredients) {
        ItemStack item = plugin.getItemManager().getItem(type).getItemStack();
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, key), item);
        for (Material m : ingredients) {
            recipe.addIngredient(m);
        }
        plugin.getServer().addRecipe(recipe);
    }

    private void registerLokiDice() {
        if (!(plugin.getItemManager().getItem(CustomItemType.LOKI_DICE) instanceof net.rollanddeath.smp.core.items.impl.LokiDice dice)) {
            return;
        }

        ItemStack luckyDice = dice.getItemStack(0.20);
        ShapelessRecipe luckyRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "loki_dice_lucky"), luckyDice);
        luckyRecipe.addIngredient(Material.EMERALD_BLOCK);
        luckyRecipe.addIngredient(Material.NETHER_STAR);
        luckyRecipe.addIngredient(Material.TOTEM_OF_UNDYING);
        luckyRecipe.addIngredient(Material.NETHERITE_INGOT);
        luckyRecipe.addIngredient(Material.ENCHANTED_GOLDEN_APPLE);
        luckyRecipe.addIngredient(Material.DRAGON_BREATH);
        luckyRecipe.addIngredient(Material.ECHO_SHARD);
        luckyRecipe.addIngredient(Material.GHAST_TEAR);
        luckyRecipe.addIngredient(Material.END_CRYSTAL);
        plugin.getServer().addRecipe(luckyRecipe);

        ItemStack cursedDice = dice.getItemStack(-0.20);
        ShapelessRecipe cursedRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "loki_dice_cursed"), cursedDice);
        cursedRecipe.addIngredient(Material.EMERALD_BLOCK);
        cursedRecipe.addIngredient(Material.WITHER_ROSE);
        cursedRecipe.addIngredient(Material.FERMENTED_SPIDER_EYE);
        cursedRecipe.addIngredient(Material.POISONOUS_POTATO);
        cursedRecipe.addIngredient(Material.ROTTEN_FLESH);
        cursedRecipe.addIngredient(Material.SPIDER_EYE);
        cursedRecipe.addIngredient(Material.SOUL_SOIL);
        cursedRecipe.addIngredient(Material.SOUL_TORCH);
        cursedRecipe.addIngredient(Material.MAGMA_CREAM);
        plugin.getServer().addRecipe(cursedRecipe);
    }

        private void registerLokiRoleDice() {
                var item = plugin.getItemManager().getItem(CustomItemType.LOKI_ROLE_DICE);
                if (item == null) return;

                ItemStack dice = item.getItemStack();
                ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "loki_role_dice"), dice);
                recipe.addIngredient(Material.EMERALD_BLOCK);
                recipe.addIngredient(Material.NETHERITE_SCRAP);
                recipe.addIngredient(Material.ECHO_SHARD);
                recipe.addIngredient(Material.GHAST_TEAR);
                recipe.addIngredient(Material.TOTEM_OF_UNDYING);
                plugin.getServer().addRecipe(recipe);
        }
}
