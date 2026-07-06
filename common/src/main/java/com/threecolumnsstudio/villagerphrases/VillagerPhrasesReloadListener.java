package com.threecolumnsstudio.villagerphrases;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class VillagerPhrasesReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        VillagerPhrasesData.load(manager);
        VillagerPhrases.LOGGER.info("VillagerPhrases dialog data reloaded");
    }
}
