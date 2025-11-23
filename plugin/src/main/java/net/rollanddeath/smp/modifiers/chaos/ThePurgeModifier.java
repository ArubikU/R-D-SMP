package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ThePurgeModifier extends Modifier {

    public ThePurgeModifier(RollAndDeathSMP plugin) {
        super(plugin, "La Purga", ModifierType.CHAOS, "Se pueden robar cofres protegidos hoy.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (plugin instanceof RollAndDeathSMP smp) {
            smp.getProtectionManager().setPurgeActive(true);
        }
        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize("<dark_red><bold>¡LA PURGA HA COMENZADO! <red>Las protecciones están desactivadas."));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (plugin instanceof RollAndDeathSMP smp) {
            smp.getProtectionManager().setPurgeActive(false);
        }
        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize("<green>La Purga ha terminado. Las protecciones están activas de nuevo."));
    }
}
