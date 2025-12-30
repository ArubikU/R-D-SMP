package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.rollanddeath.smp.core.scripting.Action;

/** Registers built-in action parsers into the shared registry. */
public final class ActionRegistrar {
    private static final Map<String, Function<Map<?, ?>, Action>> REGISTERED = new ConcurrentHashMap<>();

    private ActionRegistrar() {
    }

    public static void registerDefaults() {
        CallAction.register();
        DenyAction.register();
        AssignRandomRoleAction.register();
        GiveDailyRollRewardAction.register();
        ConsumeExtraHandItemAction.register();
        DropItemAtLocationAction.register();
        FillChestLootAction.register();
        ClearAnvilResultAction.register();
        MultiplyFoodLossAction.register();
        TakeLivesAction.register();
        AddLivesAction.register();
        SetLivesAction.register();
        SetVarAction.register();
        CopyVarAction.register();
        AddVarAction.register();
        StackPlayerAttributeModifierAction.register();
        AddPlayerFoodAction.register();
        ApplyEffectAction.register();
        ApplyRandomEffectAction.register();
        RemoveEffectAction.register();
        ConsumeEventItemAction.register();
        BroadcastAction.register();
        SetEventDamageAction.register();
        MultiplyEventDamageAction.register();
        AddVelocityAction.register();
        SetPlayerVelocityForwardAction.register();
        SetPlayerCooldownAction.register();
        SetPlayerRiptidingAction.register();
        ProjectileAddRandomSpreadAction.register();
        ReadEventItemPdcToVarAction.register();
        SetVarTargetBlockLocationAction.register();
        RunRepeatingAction.register();
        RunLaterAction.register();
        GravityPullNearLocationAction.register();
        PlaySoundAction.register();
        StopAllSoundsAction.register();
        SetViewDistanceAction.register();
        SetWorldTimeAction.register();
        SetGameRuleAllWorldsAction.register();
        AddVelocityRandomAction.register();
        RandomBoolToVarAction.register();
        SetVarNowPlusAction.register();
        ForEachOnlinePlayerAction.register();
        ForEachEntityInAllWorldsAction.register();
        SetCompassTargetRandomAction.register();
        SetCompassTargetSpawnAction.register();
        SetWeatherAllWorldsAction.register();
        SetNameTagVisibilityAction.register();
        SlipDropHandItemAction.register();
        SetBlockBreakDropItemsAction.register();
        SetPortalDestinationRandomAction.register();
        SetProtectionPurgeActiveAction.register();
        LocationOffsetAction.register();
        PlaceTorchesAroundAction.register();
        BroadcastWithLocationAction.register();
        DiscordAnnounceWithLocationAction.register();
        FreezeAreaAction.register();
        MathSetVarAction.register();
        AddEventDeathDropAction.register();
        ClearEventDeathDropsAction.register();
        MultiplyExplosionRadiusAction.register();
        CancelEventAction.register();
        SetEventUseInteractedBlockAction.register();
        SetEventUseItemInHandAction.register();
        RemoveEventItemEntityAction.register();
        GiveItemAction.register();
        DamageItemDurabilityAction.register();
        DamageHelmetDurabilityAction.register();
        UnequipItemAction.register();
        RotateOnlinePlayerPositionsAction.register();
        EnsurePlayerHasItemAction.register();
        SetPlayerInventoryHideTooltipAction.register();
        SetHideTooltipFromEventItemsAction.register();
        BowRefundConsumableAction.register();
        AddAttributeModifierAction.register();
        RemoveAttributeModifierAction.register();
        InflateVillagerPricesAllWorldsAction.register();
        DeflateVillagerPricesAllWorldsAction.register();
        InflateEventVillagerPricesAction.register();
        MultiplyVillagerTradeCostAction.register();
        SetSkeletonsBowIntervalAllWorldsAction.register();
        SetEventSkeletonBowIntervalAction.register();
        CursedEarthSpawnGiantWithLootAction.register();
        CursedEarthRestoreLootOnGiantDeathAction.register();
        SpawnParticleShapeAction.register();
        StartParticleSystemAction.register();
        StopParticleSystemAction.register();
        LaunchCurvedProjectileAction.register();
        SelectEntitiesAction.register();
        SpawnAction.register();
        LightningAction.register();
        ForInAction.register();
        DamageAction.register();
        HealAction.register();
        TeleportAction.register();
        SelectLocationsAction.register();
        SetBlockAction.register();
        ExplodeAction.register();
        SetAttributeAction.register();
        SetTargetAction.register();
        AddPassengerAction.register();
        SetEntityPropertyAction.register();
    }

    public static void register(String name, Function<Map<?, ?>, Action> factory, String... aliases) {
        if (name == null || name.isBlank() || factory == null) return;
        Function<Map<?, ?>, Action> wrapped = raw -> ActionConditions.wrap(raw, factory.apply(raw));
        
        String t = name.trim().toLowerCase(Locale.ROOT);
        REGISTERED.put(t, wrapped);
        if (aliases != null) {
            for (String a : aliases) {
                if (a == null || a.isBlank()) continue;
                REGISTERED.put(a.trim().toLowerCase(Locale.ROOT), wrapped);
            }
        }
    }

    public static Action parse(Map<?, ?> raw) {
        if (raw == null) return null;
        Object typeObj = raw.get("type");
        if (typeObj == null) return null;
        String type = String.valueOf(typeObj).trim().toLowerCase(Locale.ROOT);
        
        Function<Map<?, ?>, Action> factory = REGISTERED.get(type);
        if (factory != null) {
            return factory.apply(raw);
        }
        return null;
    }
}
