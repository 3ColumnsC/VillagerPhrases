package com.threecolumnsstudio.villagerphrases.dialogue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class IronGolemPhrasesData {

    private static final String ROOT_KEY = "iron_golem";
    private static final Set<String> QUERY_TAGS = Set.of("normal", "hit", "death");

    private static final Map<String, List<Phrase>> PHRASES_BY_TAG = new HashMap<>();

    private IronGolemPhrasesData() {}

    public static void load(ResourceManager manager) {
        PHRASES_BY_TAG.clear();

        ResourceLocation id = ResourceLocation.tryParse(VillagerPhrases.MOD_ID + ":dialogue/iron_golem_phrases.json");

        try {
            var resource = manager.getResource(id)
                .orElseThrow(() -> new RuntimeException("Could not find " + id));
            JsonObject json = JsonParser.parseReader(
                new InputStreamReader(resource.open())
            ).getAsJsonObject();

            JsonArray arr = json.getAsJsonArray(ROOT_KEY);
            List<Phrase> all = new ArrayList<>();
            if (arr != null) {
                for (JsonElement element : arr) {
                    JsonObject obj = element.getAsJsonObject();
                    String key = obj.get("key").getAsString();
                    List<String> tags = new ArrayList<>();
                    obj.getAsJsonArray("tags").forEach(t -> tags.add(t.getAsString()));
                    all.add(new Phrase(key, tags));
                }
            }

            Map<String, List<Phrase>> byTag = new HashMap<>();
            byTag.put("all", all);
            for (String tag : QUERY_TAGS) {
                List<Phrase> filtered = new ArrayList<>();
                for (Phrase phrase : all) {
                    if (phrase.tags().contains(tag)) {
                        filtered.add(phrase);
                    }
                }
                byTag.put(tag, filtered);
            }
            PHRASES_BY_TAG.putAll(byTag);

            VillagerPhrases.LOGGER.info("Loaded {} iron golem phrases", all.size());
        } catch (Exception e) {
            VillagerPhrases.LOGGER.error("Failed to load iron golem phrases", e);
        }
    }

    static List<Phrase> phrasesFor(String tag) {
        List<Phrase> pool = PHRASES_BY_TAG.get(tag);
        if (pool == null || pool.isEmpty()) {
            pool = PHRASES_BY_TAG.get("all");
        }
        return pool;
    }
}
