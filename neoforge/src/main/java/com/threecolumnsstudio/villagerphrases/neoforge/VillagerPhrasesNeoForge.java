package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesReloadListener;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@Mod(VillagerPhrases.MOD_ID)
public class VillagerPhrasesNeoForge {

    public VillagerPhrasesNeoForge(IEventBus modEventBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onRegisterReloadListeners);
            VillagerPhrasesConfigLoader.load(FMLPaths.CONFIGDIR.get());
            VillagerPhrases.init();
        } else {
            VillagerPhrases.LOGGER.warn("VillagerPhrases is a client-only mod");
        }
    }

    private void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
            ResourceLocation.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "dialogue"),
            new VillagerPhrasesReloadListener()
        );
    }
}
