package com.threecolumnsstudio.villagerphrases.events;

import com.threecolumnsstudio.villagerphrases.chat.PhraseMessageFormatter;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;
import com.threecolumnsstudio.villagerphrases.dialogue.PhraseSelector;
import com.threecolumnsstudio.villagerphrases.dialogue.VillagerPhrasesData;
import com.threecolumnsstudio.villagerphrases.state.VillagerPhrasesState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public final class VillagerPhrasesClientEvents {

    private static final int PROXIMITY_INTERVAL_TICKS = 100;
    private static final double PROXIMITY_CHANCE = 0.5;
    private static final double SITUATIONAL_CHANCE = 0.6;
    private static final double HIT_CHANCE = 0.7;
    private static final int DEATH_TRACK_TICKS = 100;
    private static final long NIGHT_START = 13000L;
    private static final long NIGHT_END = 23000L;

    private VillagerPhrasesClientEvents() {}

    public static void onInteract(Player player, Level level, InteractionHand hand, Entity target) {
        if (!level.isClientSide()) return;
        if (hand != InteractionHand.MAIN_HAND) return;
        if (!(target instanceof Villager villager)) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfigLoader.getInstance();
        if (config == null || !config.isAnyEnabled()) return;
        if (VillagerPhrasesState.isGlobalMessageCooldown(level, config.globalMessageCooldownTicks)) return;

        String key = PhraseSelector.nextInteractKey(VillagerPhrasesData.professionId(villager), config);
        if (key != null) {
            VillagerPhrasesState.markInteract(level);
            player.displayClientMessage(PhraseMessageFormatter.formatMessage(villager, key, player), false);
            VillagerPhrasesState.markAnyMessage(level);
        }
    }

    public static void onAttack(Player player, Entity target) {
        if (!player.level().isClientSide()) return;
        if (!(target instanceof Villager villager)) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfigLoader.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        VillagerPhrasesState.markHit(villager);

        if (!config.enableHitPhrases) return;
        if (VillagerPhrasesState.isGlobalMessageCooldown(player.level(), config.globalMessageCooldownTicks)) return;
        if (player.getRandom().nextFloat() >= HIT_CHANCE) return;

        String key = PhraseSelector.nextHitKey(VillagerPhrasesData.professionId(villager), config);
        if (key != null) {
            player.displayClientMessage(PhraseMessageFormatter.formatMessage(villager, key, player), false);
            VillagerPhrasesState.markAnyMessage(player.level());
        }
    }

    public static void onClientTick(Level level, Player player) {
        VillagerPhrasesConfig config = VillagerPhrasesConfigLoader.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        checkDeaths(level, player, config);

        if (level.getGameTime() % PROXIMITY_INTERVAL_TICKS != 0) return;
        if (VillagerPhrasesState.isInteractCooldown(level)) return;
        if (VillagerPhrasesState.isGlobalMessageCooldown(level, config.globalMessageCooldownTicks)) return;

        List<Villager> nearby = level.getEntitiesOfClass(
            Villager.class,
            player.getBoundingBox().inflate(6)
        );

        if (nearby.isEmpty() || level.getRandom().nextFloat() >= PROXIMITY_CHANCE) return;

        Villager villager = nearby.get(level.getRandom().nextInt(nearby.size()));
        String profession = VillagerPhrasesData.professionId(villager);
        String key = situationalKey(level, config, profession);

        if (key != null) {
            player.displayClientMessage(PhraseMessageFormatter.formatMessage(villager, key, player), false);
            VillagerPhrasesState.markAnyMessage(level);
        }
    }

    private static String situationalKey(Level level, VillagerPhrasesConfig config, String profession) {
        long dayTime = level.getDayTime() % 24000L;
        if (level.dimensionType().hasSkyLight() && dayTime > NIGHT_START && dayTime < NIGHT_END
            && config.enableNightPhrases && level.getRandom().nextFloat() < SITUATIONAL_CHANCE) {
            String key = PhraseSelector.nextNightKey(profession, config);
            return key != null ? key : PhraseSelector.nextProximityKey(profession, config);
        }
        if (level.isRaining() && config.enableRainPhrases && level.getRandom().nextFloat() < SITUATIONAL_CHANCE) {
            String key = PhraseSelector.nextRainKey(profession, config);
            return key != null ? key : PhraseSelector.nextProximityKey(profession, config);
        }
        return PhraseSelector.nextProximityKey(profession, config);
    }

    private static void checkDeaths(Level level, Player player, VillagerPhrasesConfig config) {
        if (!config.enableDeathPhrases) return;
        long now = level.getGameTime();

        for (int entityId : VillagerPhrasesState.recentlyHitIds()) {
            if (now - VillagerPhrasesState.hitTick(entityId) > DEATH_TRACK_TICKS) {
                VillagerPhrasesState.removeRecentlyHit(entityId);
                continue;
            }

            Entity entity = level.getEntity(entityId);
            if (entity instanceof Villager villager && villager.isDeadOrDying()) {
                String key = PhraseSelector.nextDeathKey(VillagerPhrasesData.professionId(villager), config);
                if (key != null) {
                    player.displayClientMessage(PhraseMessageFormatter.formatMessage(villager, key, player), false);
                    VillagerPhrasesState.markAnyMessage(level);
                }
                VillagerPhrasesState.removeRecentlyHit(entityId);
            }
        }
    }
}
