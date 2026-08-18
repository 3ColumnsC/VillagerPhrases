package com.threecolumnsstudio.villagerphrases.dialogue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.npc.Villager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class VillagerPhrasesData {

    private static final Set<String> QUERY_TAGS = Set.of("normal", "humor", "night", "hit", "rain", "death");

    private static final Map<String, Map<String, List<Phrase>>> PHRASES_BY_TAG = new HashMap<>();

    private VillagerPhrasesData() {}

    public static void load(ResourceManager manager) {
        PHRASES_BY_TAG.clear();

        ResourceLocation id = ResourceLocation.tryParse(VillagerPhrases.MOD_ID + ":dialogue/villager_phrases.json");

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

    static List<Phrase> phrasesFor(String profession, String tag) {
        Map<String, List<Phrase>> byTag = PHRASES_BY_TAG.get(profession);
        if (byTag == null) {
            byTag = PHRASES_BY_TAG.get("generic");
        }
        if (byTag == null) return null;

        List<Phrase> pool = byTag.get(tag);
        if (pool == null || pool.isEmpty()) {
            pool = byTag.get("all");
        }
        return pool;
    }

    public static String professionId(Villager villager) {
        ResourceLocation key = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
        return key != null ? key.getPath() : "none";
    }
}
