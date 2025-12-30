package net.rollanddeath.smp.core.scripting.scope;

import net.rollanddeath.smp.RollAndDeathSMP;

/**
 * Provider para construir containers snapshot por tipo de scope/base.
 *
 * Si no hay provider específico, el factory debe caer a un container reflectivo (read-only) en STATE.
 */
interface ScopeContainerProvider {

    boolean supports(ScopeId id, Object base);

    ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin);
}
