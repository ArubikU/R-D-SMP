package net.rollanddeath.smp.core.mobs;

public enum MobType {
    CAVE_RAT("Rata de Cueva"),
    LESSER_PHANTOM("Fantasma Menor"),
    MAGMA_SLIME("Slime de Magma"),
    JUMPING_SPIDER("Araña Saltadora"),
    MINER_ZOMBIE("Zombie Minero"),
    WANDERING_SKELETON("Esqueleto Vagabundo"),
    WET_CREEPER("Creeper Húmedo"),
    ARMORED_SKELETON("Esqueleto Blindado"),
    SPEED_ZOMBIE("Zombie Veloz"),
    THE_HIVE("La Colmena"),
    VENGEFUL_SPIRIT("Espíritu Vengativo"),
    CORRUPTED_GOLEM("Golem Corrupto"),
    ICE_CREEPER("Creeper de Hielo"),
    GIANT_PHANTOM("Fantasma Gigante"),
    SWAMP_WITCH("Bruja del Pantano"),
    THE_STALKER("El Acechador"),
    BONE_TURRET("Torreta de Hueso"),
    SHADOW("Sombra"),
    BLUE_BLAZE("Blaze Azul"),
    MIMIC_SHULKER("Shulker Mímico"),
    ELITE_SPIDER_JOCKEY("Jinete de Araña Elite"),
    MAD_EVOKER("Evoker Loco"),
    APOCALYPSE_KNIGHT("Caballero del Apocalipsis"),
    LEVIATHAN("Leviatán"),
    RAT_KING("Rey Rata"),
    AWAKENED_WARDEN("El Warden Despierto"),
    ALPHA_DRAGON("Dragón Alpha"),
    THE_REAPER("El Segador"),
    SLIME_KING("Rey Slime"),
    BANSHEE("Banshee"),
    VOID_WALKER("Caminante del Vacío");

    private final String displayName;

    MobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isBoss() {
        return this == APOCALYPSE_KNIGHT || this == LEVIATHAN || this == RAT_KING ||
               this == AWAKENED_WARDEN || this == ALPHA_DRAGON || this == THE_REAPER ||
               this == SLIME_KING || this == BANSHEE || this == VOID_WALKER;
    }
}
