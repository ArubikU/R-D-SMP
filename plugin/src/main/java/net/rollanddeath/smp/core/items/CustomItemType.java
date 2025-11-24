package net.rollanddeath.smp.core.items;

public enum CustomItemType {
    HEALING_BANDAGE("Venda Curativa"),
    SHARPENING_STONE("Piedra de Afilar"),
    MOLDY_BREAD("Pan Mohoso"),
    ETERNAL_TORCH("Antorcha Eterna"),
    SHARP_STICK("Palo Afilado"),
    REINFORCED_LEATHER_BOOTS("Botas de Cuero Reforzadas"),
    MYSTERIOUS_SOUP("Sopa Misteriosa"),
    HERMES_BOOTS("Botas de Hermes"),
    OBLIVION_POTION("Poción de Olvido"),
    THORN_SHIELD("Escudo de Espinas"),
    BONE_BOW("Arco de Hueso"),
    GLASS_PICKAXE("Pico de Vidrio"),
    XP_MAGNET("Imán de XP"),
    SMALL_BACKPACK("Mochila Pequeña"),
    GRAPPLING_HOOK("Gancho de Agarre"),
    GREED_PICKAXE("Pico de la Codicia"),
    DISCORD_APPLE("Manzana de la Discordia"),
    POSEIDON_TRIDENT("Tridente de Poseidón"),
    ARMORED_WINGS("Alas Blindadas"),
    INVISIBILITY_CLOAK("Capa de Invisibilidad"),
    WAR_HAMMER("Martillo de Guerra"),
    REGENERATION_TOTEM("Tótem de Regeneración"),
    CHANCE_TOTEM("Tótem del Azar"),
    RESURRECTION_ORB("Orbe de Resurrección"),
    VAMPIRE_SWORD("Espada Vampírica"),
    WORLD_DESTROYER_PICKAXE("Pico Destructor de Mundos"),
    TRUE_SIGHT_HELMET("Casco de la Visión Verdadera"),
    NOTCH_HEART("Corazón de Notch"),
    SOUL_CONTRACT("Contrato de Alma"),
    VOID_CALL("Llamada del Vacío"),
    REAL_DRAGON_EGG("Huevo de Dragón Real"),
    HAND_OF_GOD("La Mano de Dios");

    private final String displayName;

    CustomItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
