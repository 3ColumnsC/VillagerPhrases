package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesReloadListener;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.IEventBus;

@Mod(VillagerPhrases.MOD_ID)
public class VillagerPhrasesNeoForge {

    public VillagerPhrasesNeoForge(IEventBus modEventBus) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onRegisterReloadListeners);
            VillagerPhrasesConfig.load(FMLPaths.CONFIGDIR.get());
            VillagerPhrases.init();
        } else {
            VillagerPhrases.LOGGER.warn("VillagerPhrases is a client-only mod");
        }
    }

    private void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new VillagerPhrasesReloadListener());
    }
}
