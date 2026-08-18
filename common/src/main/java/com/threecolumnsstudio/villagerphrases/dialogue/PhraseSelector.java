package com.threecolumnsstudio.villagerphrases.dialogue;

import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.state.VillagerPhrasesState;

import java.util.List;
import java.util.Random;

public final class PhraseSelector {

    private static final int HUMOR_EVERY = 3;
    private static final Random RANDOM = new Random();

    private PhraseSelector() {}

    public static String nextInteractKey(String profession, VillagerPhrasesConfig config) {
        String tag = resolveTag(profession, config);
        return tag != null ? pick(profession, tag) : null;
    }

    public static String nextProximityKey(String profession, VillagerPhrasesConfig config) {
        return nextInteractKey(profession, config);
    }

    public static String nextNightKey(String profession, VillagerPhrasesConfig config) {
        return pick(profession, "night");
    }

    public static String nextHitKey(String profession, VillagerPhrasesConfig config) {
        return pick(profession, "hit");
    }

    public static String nextRainKey(String profession, VillagerPhrasesConfig config) {
        return pick(profession, "rain");
    }

    public static String nextDeathKey(String profession, VillagerPhrasesConfig config) {
        return pick(profession, "death");
    }

    private static String resolveTag(String profession, VillagerPhrasesConfig config) {
        boolean normal = config.enableNormalPhrases;
        boolean humor = config.enableHumorPhrases;
        if (!normal && !humor) return null;

        if (normal && humor) {
            if (VillagerPhrasesState.proximityCount(profession) >= HUMOR_EVERY) {
                VillagerPhrasesState.resetProximityCount(profession);
                return "humor";
            }
            VillagerPhrasesState.incrementProximityCount(profession);
            return "normal";
        }
        return normal ? "normal" : "humor";
    }

    private static String pick(String profession, String tag) {
        List<Phrase> pool = VillagerPhrasesData.phrasesFor(profession, tag);
        if (pool == null || pool.isEmpty()) return null;
        return pool.get(RANDOM.nextInt(pool.size())).key();
    }
}
