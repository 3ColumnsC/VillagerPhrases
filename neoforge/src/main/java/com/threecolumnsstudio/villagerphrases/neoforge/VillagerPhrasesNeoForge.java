package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(VillagerPhrases.MOD_ID)
public class VillagerPhrasesNeoForge {

    public VillagerPhrasesNeoForge(IEventBus modEventBus) {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            VillagerPhrasesNeoForgeClient.init(modEventBus);
        } else {
            VillagerPhrases.LOGGER.warn("VillagerPhrases is a client-only mod");
        }
    }
}
