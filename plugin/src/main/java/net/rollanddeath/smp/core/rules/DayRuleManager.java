package net.rollanddeath.smp.core.rules;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.GameRule;
import org.bukkit.entity.EntityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DayRuleManager {

    private final RollAndDeathSMP plugin;
    private final List<DayRule> rules;
    private static final int BASE_SLEEP_PERCENTAGE = 30;

    public DayRuleManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.rules = new ArrayList<>();
        loadRules();
    }

    private void loadRules() {
        rules.add(new DayRule(1, "Calma Inicial", "Inicio sin cambios adicionales.", RuleType.NONE));
        rules.add(new DayRule(2, "Golpes Más Fuertes I", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(3, "Golpes Más Fuertes II", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(4, "Golpes Más Fuertes III", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(5, "Golpes Más Fuertes IV", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(6, "Patrullas Triples", "Se suma otro mob extra por spawn hostil.", RuleType.TRIPLE_MOB_SPAWN, 2.0));
        rules.add(new DayRule(7, "Golpes Más Fuertes V", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(8, "Golpes Más Fuertes VI", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(9, "Golpes Más Fuertes VII", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(10, "Noche en Serio", "100% para dormir.", RuleType.SLEEP_PERCENTAGE, 100.0));
        rules.add(new DayRule(10, "Noche en Serio (Arañas)", "Arañas con 1-2 efectos.", RuleType.SPIDER_BUFF, 1.0));
        rules.add(new DayRule(11, "Golpes Más Fuertes VIII", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(12, "Patrullas Dobles", "Aparece un mob extra por spawn hostil.", RuleType.DOUBLE_MOB_SPAWN, 1.0));
        rules.add(new DayRule(13, "Golpes Más Fuertes IX", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(14, "Toque de Queda Suave", "60% de jugadores para saltar la noche.", RuleType.SLEEP_PERCENTAGE, 60.0));
        rules.add(new DayRule(15, "No se Duerme", "Dormir deja de saltar la noche.", RuleType.NO_SLEEP));
        rules.add(new DayRule(16, "Golpes Más Fuertes X", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(17, "Arañas Potenciadas II", "Arañas con 2-3 efectos.", RuleType.SPIDER_BUFF, 2.0));
        rules.add(new DayRule(18, "Loot Racionado II", "Sin loot de Wither Skeleton, Guardian, Magma Cube y Drowned.", RuleType.LOOT_RESTRICTION));
        rules.add(new DayRule(19, "Ravagers Codiciosos I", "3% de probabilidad de tótem en Ravager.", RuleType.RAVAGER_BUFF, 0.03));
        rules.add(new DayRule(20, "Loot Racionado I", "Sin loot de Blaze, Bruja, Enderman y Ghast.", RuleType.LOOT_RESTRICTION));
        rules.add(new DayRule(21, "Golpes Más Fuertes XI", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(22, "Golpes Más Fuertes XII", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(23, "Ravagers Codiciosos II", "6% de probabilidad de tótem en Ravager.", RuleType.RAVAGER_BUFF, 0.06));
        rules.add(new DayRule(24, "Arañas Potenciadas III", "Arañas con 3 efectos garantizados.", RuleType.SPIDER_BUFF, 3.0));
        rules.add(new DayRule(25, "Golpes Más Fuertes XIII", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(26, "Loot Racionado III", "Sin loot de Piglin Zombie, Slime ni Golem de Hierro.", RuleType.LOOT_RESTRICTION));
        rules.add(new DayRule(27, "Golpes Más Fuertes XIV", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(28, "Ravagers Codiciosos III", "8% de probabilidad de tótem en Ravager.", RuleType.RAVAGER_BUFF, 0.08));
        rules.add(new DayRule(29, "Golpes Más Fuertes XV", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
        rules.add(new DayRule(30, "Insomnio Total", "Dormir bloqueado por completo.", RuleType.NO_SLEEP));
        rules.add(new DayRule(31, "Golpes Más Fuertes XVI", "+2% daño de mobs hostiles.", RuleType.MOB_DAMAGE_BOOST, 0.02));
    }

    public List<DayRule> getActiveRules(int currentDay) {
        return rules.stream()
                .filter(rule -> rule.getDay() == currentDay)
                .collect(Collectors.toList());
    }

    public boolean isRuleActive(int currentDay, RuleType type) {
        return rules.stream()
                .anyMatch(rule -> rule.getDay() == currentDay && rule.getType() == type);
    }

    public double getCumulativeValue(int currentDay, RuleType type) {
        return rules.stream()
                .filter(rule -> rule.getDay() <= currentDay && rule.getType() == type)
                .mapToDouble(DayRule::getValue)
                .sum();
    }

    public double getLatestValue(int currentDay, RuleType type) {
        return rules.stream()
                .filter(rule -> rule.getDay() <= currentDay && rule.getType() == type)
                .max((r1, r2) -> Integer.compare(r1.getDay(), r2.getDay()))
                .map(DayRule::getValue)
                .orElse(0.0);
    }

    public void addRule(DayRule rule) {
        rules.add(rule);
    }

    public void refreshForDay(int day) {
        List<DayRule> active = getActiveRules(day);
        
        // Reset default sleep percentage (baseline 30%) unless a day rule overrides it.
        plugin.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, BASE_SLEEP_PERCENTAGE));

        if (!active.isEmpty()) {
            for (DayRule rule : active) {
                plugin.getServer().broadcast(net.kyori.adventure.text.Component.text("Regla del Día: " + rule.getName(), net.kyori.adventure.text.format.NamedTextColor.GOLD));
                plugin.getServer().broadcast(net.kyori.adventure.text.Component.text(rule.getDescription(), net.kyori.adventure.text.format.NamedTextColor.YELLOW));
                
                if (rule.getType() == RuleType.SLEEP_PERCENTAGE) {
                    int percentage = (int) rule.getValue();
                    plugin.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, percentage));
                }
            }
        }
    }

    public Set<EntityType> getRestrictedMobs(int day) {
        Set<EntityType> restricted = new HashSet<>();
        if (isRuleActive(day, RuleType.LOOT_RESTRICTION)) {
            if (day == 20) {
                restricted.addAll(Arrays.asList(EntityType.BLAZE, EntityType.WITCH, EntityType.ENDERMAN, EntityType.GHAST));
            } else if (day == 18) {
                restricted.addAll(Arrays.asList(EntityType.WITHER_SKELETON, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.DROWNED));
            } else if (day == 26) {
                restricted.addAll(Arrays.asList(EntityType.ZOMBIFIED_PIGLIN, EntityType.SLIME, EntityType.IRON_GOLEM));
            }
        }
        return restricted;
    }
}
