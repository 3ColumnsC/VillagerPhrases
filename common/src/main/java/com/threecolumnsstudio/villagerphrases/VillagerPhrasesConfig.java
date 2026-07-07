package com.threecolumnsstudio.villagerphrases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class VillagerPhrasesConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static VillagerPhrasesConfig INSTANCE;
    private static Path CONFIG_PATH;

    public boolean enableNormalPhrases = true;
    public boolean enableHumorPhrases = true;
    public boolean enableNightPhrases = true;
    public boolean enableHitPhrases = true;
    public boolean enableRainPhrases = true;
    public boolean enableDeathPhrases = true;

    public static VillagerPhrasesConfig getInstance() {
        return INSTANCE;
    }

    public static void load(Path configDir) {
        CONFIG_PATH = configDir.resolve("villagerphrases.json");

        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, VillagerPhrasesConfig.class);
                if (INSTANCE == null) INSTANCE = new VillagerPhrasesConfig();
            } catch (Exception e) {
                VillagerPhrases.LOGGER.warn("Could not read config, using defaults", e);
                INSTANCE = new VillagerPhrasesConfig();
                save();
            }
        } else {
            INSTANCE = new VillagerPhrasesConfig();
            save();
        }
    }

    public static void save() {
        if (INSTANCE == null || CONFIG_PATH == null) return;
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            VillagerPhrases.LOGGER.error("Could not save config", e);
        }
    }

    public boolean isAnyEnabled() {
        return enableNormalPhrases || enableHumorPhrases || enableNightPhrases || enableHitPhrases || enableRainPhrases || enableDeathPhrases;
    }
}
