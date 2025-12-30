package net.rollanddeath.smp.core.scripting.scope;

public enum ScopeId {
    // Entidades/objetos del evento, normalizados cross-módulo.
    // SUBJECT ~= "caster" en vars legacy; TARGET ~= "target"; PROJECTILE ~= "projectile".
    SUBJECT,
    TARGET,
    PROJECTILE,
    ITEM,
    PLAYER,
    LOCATION,
    WORLD,
    CHUNK,
    GLOBAL,
    TEAM,
    EVENT;
}
