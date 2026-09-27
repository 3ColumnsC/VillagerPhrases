package com.threecolumnsstudio.villagerphrases.fabric.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.threecolumnsstudio.villagerphrases.client.screen.VillagerPhrasesConfigScreen;

public final class VillagerPhrasesModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return VillagerPhrasesConfigScreen::new;
    }
}
