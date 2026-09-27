package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesReloadListener;
import com.threecolumnsstudio.villagerphrases.client.screen.VillagerPhrasesConfigScreen;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(VillagerPhrases.MOD_ID)
public class VillagerPhrasesNeoForge {

    public VillagerPhrasesNeoForge(IEventBus modEventBus) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onRegisterReloadListeners);
            VillagerPhrasesConfigLoader.load(FMLPaths.CONFIGDIR.get());
            VillagerPhrases.init();
            registerConfigScreen();
        } else {
            VillagerPhrases.LOGGER.warn("VillagerPhrases is a client-only mod");
        }
    }

    private void registerConfigScreen() {
        ModList.get().getModContainerById(VillagerPhrases.MOD_ID).ifPresent(container ->
            container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> new VillagerPhrasesConfigScreen(parent)));
    }

    private void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new VillagerPhrasesReloadListener());
    }
}
