package com.threecolumnsstudio.villagerphrases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VillagerPhrases {
    public static final String MOD_ID = "villagerphrases";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private VillagerPhrases() {}

    public static void init() {
        LOGGER.info("{} initialized", MOD_ID);
    }
}
