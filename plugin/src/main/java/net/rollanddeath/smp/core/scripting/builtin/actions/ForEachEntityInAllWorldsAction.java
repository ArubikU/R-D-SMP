package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Optional;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class ForEachEntityInAllWorldsAction {
    private ForEachEntityInAllWorldsAction() {
    }

    static void register() {
        ActionRegistrar.register("for_each_entity_in_all_worlds", ForEachEntityInAllWorldsAction::parse, "for_each_entity_all_worlds");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;

        Object typesObj = raw.get("entity_types");
        List<String> entityTypes = null;
        if (typesObj instanceof List<?> typesList && !typesList.isEmpty()) {
            entityTypes = typesList.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }

        String varName = Optional.ofNullable(Resolvers.string(null, raw, "entity_var", "var", "as")).orElse("caster");
        return BuiltInActions.forEachEntityInAllWorlds(entityTypes, varName, actions);
    }
}
