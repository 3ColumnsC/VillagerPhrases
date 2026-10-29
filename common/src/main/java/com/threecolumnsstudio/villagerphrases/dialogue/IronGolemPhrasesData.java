package com.threecolumnsstudio.villagerphrases.dialogue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
        Identifier id = Identifier.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "dialogue/iron_golem_phrases.json");

        try {
            var resource = manager.getResource(id)
                .orElseThrow(() -> new RuntimeException("Could not find " + id));
            JsonObject json;
            try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            }

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

            Map<String, List<Phrase>> loaded = new HashMap<>();
            for (String tag : QUERY_TAGS) {
                List<Phrase> filtered = new ArrayList<>();
                for (Phrase phrase : all) {
                    if (phrase.tags().contains(tag)) {
                        filtered.add(phrase);
                    }
                }
                loaded.put(tag, filtered);
            }

            PHRASES_BY_TAG.clear();
            PHRASES_BY_TAG.putAll(loaded);

            VillagerPhrases.LOGGER.info("Loaded {} iron golem phrases", all.size());
        } catch (Exception e) {
            VillagerPhrases.LOGGER.error("Failed to load iron golem phrases ({})", e.getClass().getSimpleName());
        }
    }

    static List<Phrase> phrasesFor(String tag) {
        return PHRASES_BY_TAG.getOrDefault(tag, Collections.emptyList());
    }
}
