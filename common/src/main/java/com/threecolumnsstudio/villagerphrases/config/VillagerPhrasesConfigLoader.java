package com.threecolumnsstudio.villagerphrases.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.threecolumnsstudio.villagerphrases.VillagerPhrases;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class VillagerPhrasesConfigLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static VillagerPhrasesConfig instance;
    private static Path configPath;

    private VillagerPhrasesConfigLoader() {}

    public static VillagerPhrasesConfig getInstance() {
        return instance;
    }

    public static void load(Path configDir) {
        configPath = configDir.resolve("villagerphrases.json");

        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                instance = GSON.fromJson(reader, VillagerPhrasesConfig.class);
                if (instance == null) instance = new VillagerPhrasesConfig();
            } catch (Exception e) {
                VillagerPhrases.LOGGER.warn("Could not read config, using defaults", e);
                instance = new VillagerPhrasesConfig();
                save();
            }
        } else {
            instance = new VillagerPhrasesConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null || configPath == null) return;
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            VillagerPhrases.LOGGER.error("Could not save config", e);
        }
    }
}
