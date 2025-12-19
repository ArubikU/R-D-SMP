package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ScriptedModifier extends Modifier {

    private final RollAndDeathSMP rdsmp;
    private final Map<String, ModifierRule> events;

    public ScriptedModifier(JavaPlugin plugin, String name, ModifierType type, String description, Map<String, ModifierRule> events) {
        super(plugin, name, type, description);
        this.rdsmp = (RollAndDeathSMP) plugin;
        this.events = events;
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ModifierRule rule = events.get("player_regain_health");
        if (rule == null) return;

        Map<String, Object> vars = new HashMap<>();
        vars.put("event", "player_regain_health");
        vars.put("modifier", getName());
        vars.put("regain_reason", event.getRegainReason().name());
        vars.put("amount", event.getAmount());

        boolean deny = applyRule(rule, player, "player_regain_health", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_item_consume");
        if (rule == null) return;

        ItemStack item = event.getItem();

        Map<String, Object> vars = new HashMap<>();
        vars.put("event", "player_item_consume");
        vars.put("modifier", getName());
        vars.put("item_material", item.getType().name());

        boolean deny = applyRule(rule, player, "player_item_consume", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    private boolean applyRule(ModifierRule rule, Player player, String subject, Map<String, Object> vars) {
        // subjectId: <modifier_name>:<event>
        String subjectId = getName() + ":" + subject.toLowerCase(Locale.ROOT);
        ScriptContext ctx = new ScriptContext(rdsmp, player, subjectId, ScriptPhase.MODIFIER, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, rule.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onFail());
        return rule.denyOnFail() || (r != null && r.deny());
    }
}
