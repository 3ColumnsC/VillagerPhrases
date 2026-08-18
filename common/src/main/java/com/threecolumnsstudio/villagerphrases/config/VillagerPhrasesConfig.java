package com.threecolumnsstudio.villagerphrases.config;

public class VillagerPhrasesConfig {

    public boolean enableNormalPhrases = true;
    public boolean enableHumorPhrases = true;
    public boolean enableNightPhrases = true;
    public boolean enableHitPhrases = true;
    public boolean enableRainPhrases = true;
    public boolean enableDeathPhrases = true;

    public boolean isAnyEnabled() {
        return enableNormalPhrases || enableHumorPhrases || enableNightPhrases
            || enableHitPhrases || enableRainPhrases || enableDeathPhrases;
    }
}
