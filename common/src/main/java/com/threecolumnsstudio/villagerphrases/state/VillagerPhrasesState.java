package com.threecolumnsstudio.villagerphrases.state;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class VillagerPhrasesState {

    private static final int INTERACT_COOLDOWN_TICKS = 60;

    private static final Map<String, Integer> PROXIMITY_COUNTERS = new HashMap<>();
    private static final Map<Integer, Long> RECENTLY_HIT = new HashMap<>();
    private static Level currentLevel;
    private static long lastInteractGameTime = Long.MIN_VALUE;
    private static long lastAnyMessageGameTime = Long.MIN_VALUE;
    private static long lastIronGolemMessageGameTime = Long.MIN_VALUE;
    private static long lastProximityCheckGameTime = Long.MIN_VALUE;

    private VillagerPhrasesState() {}

    public static void ensureLevel(Level level) {
        if (level == currentLevel) return;

        currentLevel = level;
        PROXIMITY_COUNTERS.clear();
        RECENTLY_HIT.clear();
        lastInteractGameTime = Long.MIN_VALUE;
        lastAnyMessageGameTime = Long.MIN_VALUE;
        lastIronGolemMessageGameTime = Long.MIN_VALUE;
        lastProximityCheckGameTime = Long.MIN_VALUE;
    }

    public static int proximityCount(String profession) {
        return PROXIMITY_COUNTERS.getOrDefault(profession, 0);
    }

    public static void incrementProximityCount(String profession) {
        PROXIMITY_COUNTERS.put(profession, proximityCount(profession) + 1);
    }

    public static void resetProximityCount(String profession) {
        PROXIMITY_COUNTERS.put(profession, 0);
    }

    public static void markHit(Entity entity) {
        RECENTLY_HIT.put(entity.getId(), entity.level().getGameTime());
    }

    public static Iterator<Map.Entry<Integer, Long>> recentlyHitIterator() {
        return RECENTLY_HIT.entrySet().iterator();
    }

    public static void markInteract(Level level) {
        lastInteractGameTime = level.getGameTime();
    }

    public static boolean isInteractCooldown(Level level) {
        if (lastInteractGameTime == Long.MIN_VALUE) return false;

        long diff = level.getGameTime() - lastInteractGameTime;
        return diff >= 0 && diff < INTERACT_COOLDOWN_TICKS;
    }

    public static boolean isProximityCheckDue(Level level, int intervalTicks) {
        long now = level.getGameTime();
        if (now == lastProximityCheckGameTime) return false;

        if (lastProximityCheckGameTime != Long.MIN_VALUE
            && now - lastProximityCheckGameTime < intervalTicks) {
            return false;
        }

        lastProximityCheckGameTime = now;
        return true;
    }

    public static void markAnyMessage(Level level) {
        lastAnyMessageGameTime = level.getGameTime();
    }

    public static boolean isGlobalMessageCooldown(Level level, int cooldownTicks) {
        if (lastAnyMessageGameTime == Long.MIN_VALUE) return false;

        long diff = level.getGameTime() - lastAnyMessageGameTime;
        return diff >= 0 && diff < cooldownTicks;
    }

    public static void markIronGolemMessage(Level level) {
        lastIronGolemMessageGameTime = level.getGameTime();
    }

    public static boolean isIronGolemMessageCooldown(Level level, int cooldownTicks) {
        if (lastIronGolemMessageGameTime == Long.MIN_VALUE) return false;

        long diff = level.getGameTime() - lastIronGolemMessageGameTime;
        return diff >= 0 && diff < cooldownTicks;
    }
}
