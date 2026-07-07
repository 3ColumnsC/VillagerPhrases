package com.threecolumnsstudio.villagerphrases;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class VillagerPhrasesData {

    private static final Map<String, Map<String, List<Phrase>>> PHRASES_BY_TAG = new HashMap<>();
    private static final Map<String, Integer> PROXIMITY_COUNTERS = new HashMap<>();
    private static final Map<UUID, Long> RECENTLY_HIT = new HashMap<>();
    private static final int HUMOR_EVERY = 3;
    private static final int DEATH_TRACK_TICKS = 100;
    private static final Random RANDOM = new Random();
    private static final Set<String> QUERY_TAGS = Set.of("normal", "humor", "night", "hit", "rain", "death");

    public record Phrase(String key, List<String> tags) {}

    public static void load(ResourceManager manager) {
        PHRASES_BY_TAG.clear();

        Identifier id = Identifier.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "dialogue/villager_phrases.json");

        try {
            var resource = manager.getResource(id)
                .orElseThrow(() -> new RuntimeException("Could not find " + id));
            JsonObject json = JsonParser.parseReader(
                new InputStreamReader(resource.open())
            ).getAsJsonObject();

            Map<String, List<Phrase>> raw = new HashMap<>();
            for (String profession : json.keySet()) {
                List<Phrase> list = new ArrayList<>();
                JsonArray arr = json.getAsJsonArray(profession);
                for (JsonElement element : arr) {
                    JsonObject obj = element.getAsJsonObject();
                    String key = obj.get("key").getAsString();
                    List<String> tags = new ArrayList<>();
                    obj.getAsJsonArray("tags").forEach(t -> tags.add(t.getAsString()));
                    list.add(new Phrase(key, tags));
                }
                raw.put(profession, list);
            }

            List<Phrase> genericPool = raw.get("generic");
            if (genericPool == null) genericPool = Collections.emptyList();

            for (String profession : raw.keySet()) {
                List<Phrase> professionPool = raw.get(profession);
                List<Phrase> combined = new ArrayList<>(professionPool);
                if (!profession.equals("generic")) {
                    combined.addAll(genericPool);
                }
                Map<String, List<Phrase>> byTag = new HashMap<>();
                byTag.put("all", combined);
                for (String tag : QUERY_TAGS) {
                    List<Phrase> filtered = new ArrayList<>();
                    for (Phrase p : combined) {
                        if (p.tags().contains(tag)) {
                            filtered.add(p);
                        }
                    }
                    byTag.put(tag, filtered);
                }
                PHRASES_BY_TAG.put(profession, byTag);
            }

            VillagerPhrases.LOGGER.info("Loaded {} professions from dialogue data", raw.size());
        } catch (Exception e) {
            VillagerPhrases.LOGGER.error("Failed to load villager phrases", e);
        }
    }

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

    public static void markHit(Villager villager) {
        RECENTLY_HIT.put(villager.getUUID(), villager.level().getGameTime());
    }

    public static void checkDeaths(Level level, Player player, VillagerPhrasesConfig config) {
        if (!config.enableDeathPhrases) return;
        long now = level.getGameTime();
        Iterator<Map.Entry<UUID, Long>> it = RECENTLY_HIT.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if (now - entry.getValue() > DEATH_TRACK_TICKS) {
                it.remove();
                continue;
            }
            net.minecraft.world.entity.Entity e = level.getEntity(entry.getKey());
            if (e instanceof Villager villager && villager.isDeadOrDying()) {
                String profession = villager.getVillagerData().profession()
                    .unwrapKey().map(k -> k.identifier().getPath()).orElse("generic");
                String key = pick(profession, "death");
                if (key != null) {
                    Component msg = formatMessage(villager, key, player);
                    player.sendSystemMessage(msg);
                }
                it.remove();
            }
        }
    }

    private static String resolveTag(String profession, VillagerPhrasesConfig config) {
        boolean normal = config.enableNormalPhrases;
        boolean humor = config.enableHumorPhrases;
        if (!normal && !humor) return null;

        if (normal && humor) {
            int count = PROXIMITY_COUNTERS.getOrDefault(profession, 0);
            if (count >= HUMOR_EVERY) {
                PROXIMITY_COUNTERS.put(profession, 0);
                return "humor";
            } else {
                PROXIMITY_COUNTERS.put(profession, count + 1);
                return "normal";
            }
        }
        return normal ? "normal" : "humor";
    }

    private static String pick(String profession, String tag) {
        Map<String, List<Phrase>> byTag = PHRASES_BY_TAG.get(profession);
        if (byTag == null) {
            byTag = PHRASES_BY_TAG.get("generic");
        }
        if (byTag == null) return null;

        List<Phrase> pool = byTag.get(tag);
        if (pool == null || pool.isEmpty()) {
            pool = byTag.get("all");
        }
        if (pool == null || pool.isEmpty()) return null;
        return pool.get(RANDOM.nextInt(pool.size())).key();
    }

    public static Component formatMessage(Villager villager, String key, Player player) {
        Component prefix;
        if (villager.hasCustomName()) {
            prefix = villager.getCustomName();
        } else {
            String profId = villager.getVillagerData().profession()
                .unwrapKey().map(k -> k.identifier().getPath()).orElse("none");
            if (!profId.equals("none")) {
                prefix = Component.translatable("entity.minecraft.villager." + profId);
            } else {
                prefix = Component.translatable("entity.minecraft.villager");
            }
        }
        return Component.literal("")
            .append(prefix)
            .append(Component.literal(": "))
            .append(Component.translatable(key, player.getName()));
    }
}
