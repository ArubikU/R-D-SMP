package net.rollanddeath.smp.core.roles;

public enum RoleType {
    PACIFIST("El Pacifista", "No puede dañar a otros, pero tiene regeneración constante."),
    VAMPIRE("El Vampiro", "Se quema al sol, pero tiene fuerza y visión nocturna."),
    GLASS_CANNON("Glass Cannon", "Hace doble daño, pero recibe doble daño."),
    MIDAS("Rey Midas", "Convierte items en oro, pero necesita oro para vivir."),
    NOMAD("El Nómada", "Más velocidad si no duerme en la misma cama dos veces."),
    TANK("El Tanque", "Más vida y resistencia, pero muy lento."),
    ASSASSIN("El Asesino", "Invisible al agacharse, daño crítico por la espalda."),
    ENGINEER("El Ingeniero", "Puede craftear items especiales."),
    PYRO("El Pirotécnico", "Inmune al fuego, sus ataques queman."),
    CURSED("El Maldito", "Tiene mala suerte, pero los mobs lo ignoran a veces."),
    DRUID("El Druida", "Los animales lo siguen, bonificación en bosques."),
    BERSERKER("El Berserker", "Más daño cuanta menos vida tenga."),
    SNIPER("El Francotirador", "Más daño con arco a larga distancia."),
    MERCHANT("El Mercader", "Descuentos con aldeanos, consigue más esmeraldas."),
    GHOST("El Fantasma", "Puede atravesar puertas, pero tiene poca vida."),
    AQUATIC("El Acuático", "Respira bajo el agua, más rápido en agua."),
    MINER("El Minero", "Haste permanente, visión nocturna bajo tierra."),
    TAMER("El Domador", "Sus mascotas son más fuertes."),
    ALCHEMIST("El Alquimista", "Las pociones duran más y son más potentes."),
    KNIGHT("El Caballero", "Más daño con espada y escudo, menos con arco."),
    THIEF("El Ladrón", "Puede robar items (con cooldown), velocidad."),
    GIANT("El Gigante", "Más tamaño y vida, pero más hambre."),
    DWARF("El Enano", "Menos tamaño, puede entrar en huecos de 1 bloque."),
    ILLUSIONIST("El Ilusionista", "Puede crear copias de sí mismo."),
    BARBARIAN("El Bárbaro", "No puede usar armadura de diamante/netherite, pero pega muy fuerte."),
    SAGE("El Sabio", "Más experiencia, encanta mejor."),
    CHAOTIC("El Caótico", "Efectos aleatorios al golpear."),
    GUARDIAN("El Guardián", "Protege a otros, recibe daño por ellos."),
    EXPLORER("El Explorador", "No pierde hambre al correr, mapa siempre visible."),
    COOK("El Cocinero", "La comida da más saturación y efectos.");

    private final String name;
    private final String description;

    RoleType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
