package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/** Rolls a daily reward item using stored luck on the held item. */
public final class GiveDailyRollRewardAction {
    private GiveDailyRollRewardAction() {
    }

    static void register() {
        ActionRegistrar.register("give_daily_roll_reward", GiveDailyRollRewardAction::parse, "daily_roll_reward");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String luckKey = Resolvers.string(null, raw, "luck_key", "pdc_luck_key");
        Double defaultLuck = Resolvers.doubleVal(null, raw, "default_luck", "luck");
        String message = Resolvers.string(null, raw, "message");
        Double pitchPos = Resolvers.doubleVal(null, raw, "pitch_positive", "pitch_pos");
        Double pitchNeg = Resolvers.doubleVal(null, raw, "pitch_negative", "pitch_neg");

        double defLuck = defaultLuck != null ? defaultLuck : 0.0;
        return ctx -> execute(ctx, luckKey, defLuck, message, pitchPos, pitchNeg);
    }

    private static ActionResult execute(ScriptContext ctx, String luckKeyRaw, double defaultLuck, String message, Double pitchPositive, Double pitchNegative) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null || player == null) return ActionResult.ALLOW;

        double luck = defaultLuck;
        if (luckKeyRaw != null && !luckKeyRaw.isBlank()) {
            try {
                ItemStack item = ctx.item(ItemStack.class);
                if (item == null) {
                    PlayerInteractEvent pie = ctx.nativeEvent(PlayerInteractEvent.class);
                    item = pie != null ? pie.getItem() : null;
                }
                if (item != null && item.getItemMeta() != null) {
                    NamespacedKey key;
                    String raw = luckKeyRaw.trim();
                    if (raw.contains(":")) key = NamespacedKey.fromString(raw);
                    else key = new NamespacedKey(plugin, raw);
                    if (key != null) {
                        Double stored = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.DOUBLE);
                        if (stored != null) {
                            luck = stored;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        var mgr = plugin.getDailyRollManager();
        if (mgr == null) return ActionResult.ALLOW;

        double luckFinal = luck;
        float pitchPos = pitchPositive != null ? pitchPositive.floatValue() : 1.2f;
        float pitchNeg = pitchNegative != null ? pitchNegative.floatValue() : 0.8f;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                ItemStack reward = mgr.rollItemWithLuck(luckFinal).clone();
                player.getInventory().addItem(reward);

                Component name = reward.displayName() != null ? reward.displayName() : Component.text(reward.getType().name());
                String baseMsg = message != null && !message.isBlank()
                    ? message
                    : "<green>Lanzaste el Dado de Loki y obtuviste: </green>";
                player.sendMessage(MiniMessage.miniMessage().deserialize(baseMsg).append(name));

                float pitch = luckFinal >= 0.0 ? pitchPos : pitchNeg;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, pitch);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }
}
