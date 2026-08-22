package com.threecolumnsstudio.villagerphrases.state;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class VillagerPhrasesState {

    private static final int INTERACT_COOLDOWN_TICKS = 60;

    private static final Map<String, Integer> PROXIMITY_COUNTERS = new HashMap<>();
    private static final Map<UUID, Long> RECENTLY_HIT = new HashMap<>();
    private static long lastInteractGameTime = Long.MIN_VALUE;
    private static long lastAnyMessageGameTime = Long.MIN_VALUE;

    private VillagerPhrasesState() {}

    public static int proximityCount(String profession) {
        return PROXIMITY_COUNTERS.getOrDefault(profession, 0);
    }

    public static void incrementProximityCount(String profession) {
        PROXIMITY_COUNTERS.put(profession, proximityCount(profession) + 1);
    }

    public static void resetProximityCount(String profession) {
        PROXIMITY_COUNTERS.put(profession, 0);
    }

    public static void markHit(Villager villager) {
        RECENTLY_HIT.put(villager.getUUID(), villager.level().getGameTime());
    }

    public static Set<UUID> recentlyHitUuids() {
        return Set.copyOf(RECENTLY_HIT.keySet());
    }

    public static long hitTick(UUID uuid) {
        return RECENTLY_HIT.getOrDefault(uuid, 0L);
    }

    public static void removeRecentlyHit(UUID uuid) {
        RECENTLY_HIT.remove(uuid);
    }

    public static void markInteract(Level level) {
        lastInteractGameTime = level.getGameTime();
    }

    public static boolean isInteractCooldown(Level level) {
        long diff = level.getGameTime() - lastInteractGameTime;
        return diff >= 0 && diff < INTERACT_COOLDOWN_TICKS;
    }

    public static void markAnyMessage(Level level) {
        lastAnyMessageGameTime = level.getGameTime();
    }

    public static boolean isGlobalMessageCooldown(Level level, int cooldownTicks) {
        long diff = level.getGameTime() - lastAnyMessageGameTime;
        return diff >= 0 && diff < cooldownTicks;
    }
}
