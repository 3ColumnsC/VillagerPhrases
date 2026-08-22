package com.threecolumnsstudio.villagerphrases.config;

public class VillagerPhrasesConfig {

    public boolean enableNormalPhrases = true;
    public boolean enableHumorPhrases = true;
    public boolean enableNightPhrases = true;
    public boolean enableHitPhrases = true;
    public boolean enableRainPhrases = true;
    public boolean enableDeathPhrases = true;

    // 20 ticks = 1s
    // 160 = 8s between non-death messages
    // 0 disables the cooldown
    public int globalMessageCooldownTicks = 160;

    public boolean isAnyEnabled() {
        return enableNormalPhrases || enableHumorPhrases || enableNightPhrases
            || enableHitPhrases || enableRainPhrases || enableDeathPhrases;
    }
}
