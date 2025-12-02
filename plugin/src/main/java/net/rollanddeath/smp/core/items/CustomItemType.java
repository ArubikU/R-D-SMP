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
    ARMORED_WINGS("Alas Blindadas"),
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
    HAND_OF_GOD("La Mano de Dios", 710030);

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
