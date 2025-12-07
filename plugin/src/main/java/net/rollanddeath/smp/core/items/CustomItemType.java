package net.rollanddeath.smp.core.items;

public enum CustomItemType {
    HEALING_BANDAGE("Venda Curativa", 710001),
    SHARPENING_STONE("Piedra de Afilar", 710002),
    MOLDY_BREAD("Pan Mohoso", 710003),
    ETERNAL_TORCH("Antorcha Eterna", 710004),
    SHARP_STICK("Palo Afilado", 710005),
    REINFORCED_LEATHER_BOOTS("Botas de Cuero Reforzadas", 710006),
    MYSTERIOUS_SOUP("Sopa Misteriosa", 710007),
    HERMES_BOOTS("Botas de Hermes", 710008),
    OBLIVION_POTION("Poción de Olvido", 710009),
    THORN_SHIELD("Escudo de Espinas"),
    BONE_BOW("Arco de Hueso", 710010),
    GLASS_PICKAXE("Pico de Vidrio", 710011),
    XP_MAGNET("Imán de XP", 710012),
    SMALL_BACKPACK("Mochila Pequeña", 710013),
    GRAPPLING_HOOK("Gancho de Agarre", 710014),
    GREED_PICKAXE("Pico de la Codicia", 710015),
    DISCORD_APPLE("Manzana de la Discordia", 710016),
    POSEIDON_TRIDENT("Tridente de Poseidón", 710017),
    ARMORED_WINGS("Alas Blindadas", 710031),
    INVISIBILITY_CLOAK("Capa de Invisibilidad", 710018),
    WAR_HAMMER("Martillo de Guerra", 710019),
    REGENERATION_TOTEM("Tótem de Regeneración", 710020),
    CHANCE_TOTEM("Tótem del Azar", 710021),
    RESURRECTION_ORB("Orbe de Resurrección", 710022),
    VAMPIRE_SWORD("Espada Vampírica", 710023),
    WORLD_DESTROYER_PICKAXE("Pico Destructor de Mundos", 710024),
    TRUE_SIGHT_HELMET("Casco de la Visión Verdadera", 710025),
    NOTCH_HEART("Corazón de Notch", 710026),
    SOUL_CONTRACT("Contrato de Alma", 710027),
    VOID_CALL("Llamada del Vacío", 710028),
    REAL_DRAGON_EGG("Huevo de Dragón Real", 710029),
    HAND_OF_GOD("La Mano de Dios", 710030),
    LIFE_GAPPLE("Manzana de Vida", 710032),
    LOKI_DICE("Dado de Loki", 710033),
    PACIFIST_BALM("Bálsamo Pacifista"),
    VAMPIRE_VIAL("Vial de Sangre"),
    GLASS_CANNON_CHARGE("Carga de Cristal"),
    MIDAS_TALISMAN("Talismán Dorado"),
    NOMAD_COMPASS("Brújula Errante"),
    TANK_PLATING("Placa Reforzada"),
    ASSASSIN_POISON_KIT("Kit de Veneno"),
    ENGINEER_REPAIR_KIT("Kit de Reparación"),
    PYRO_TORCH("Mechero Potenciado"),
    CURSED_IDOL("Ídolo Torcido"),
    DRUID_SEED("Semilla Ancestral"),
    BERSERKER_MEAD("Hidromiel Rabiosa"),
    SNIPER_SCOPE("Mira Improvisada"),
    MERCHANT_LEDGER("Libro de Tratos"),
    GHOST_VEIL("Velo Etéreo"),
    AQUATIC_GILLS("Branquias de Coral"),
    MINER_CHARGE("Carga de Túnel"),
    TAMER_WHISTLE("Silbato Alfa"),
    ALCHEMIST_CATALYST("Catalizador Alquímico"),
    KNIGHT_BANNER("Estandarte de Orden"),
    THIEF_LOCKPICKS("Juego de Ganzúas"),
    GIANT_BRACER("Bracera Colosal"),
    DWARF_FORGE_HAMMER("Martillo de Forja"),
    ILLUSIONIST_MIRROR("Espejo Fatuo"),
    BARBARIAN_MEAD("Hidromiel Bárbara"),
    SAGE_SCROLL("Pergamino del Sabio"),
    CHAOTIC_SEAL("Sello del Caos"),
    GUARDIAN_SIGIL("Sígilo de Guardia"),
    EXPLORER_COMPASS("Brújula de Ruta"),
    COOK_SPICE_BLEND("Mezcla de Especias"),

    // Gear progresivo sin encantamientos
    STEEL_HELMET("Casco de Acero"),
    STEEL_CHESTPLATE("Pechera de Acero"),
    STEEL_LEGGINGS("Grebas de Acero"),
    STEEL_BOOTS("Botas de Acero"),
    OBSIDIAN_HELMET("Casco Obsidiana"),
    OBSIDIAN_CHESTPLATE("Pechera Obsidiana"),
    OBSIDIAN_LEGGINGS("Grebas Obsidiana"),
    OBSIDIAN_BOOTS("Botas Obsidiana"),
    VOID_HELMET("Casco del Vacío"),
    VOID_CHESTPLATE("Pechera del Vacío"),
    VOID_LEGGINGS("Grebas del Vacío"),
    VOID_BOOTS("Botas del Vacío"),
    STEEL_SWORD("Espada de Acero"),
    OBSIDIAN_SWORD("Espada Obsidiana"),
    VOID_SWORD("Espada del Vacío");

    private final String displayName;
    private final Integer customModelData;

    CustomItemType(String displayName) {
        this(displayName, null);
    }

    CustomItemType(String displayName, Integer customModelData) {
        this.displayName = displayName;
        this.customModelData = customModelData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }
}
