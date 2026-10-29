package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = VillagerPhrases.MOD_ID, dist = Dist.CLIENT)
public class VillagerPhrasesNeoForge {

    public VillagerPhrasesNeoForge(IEventBus modEventBus) {
        VillagerPhrasesNeoForgeClient.init(modEventBus);
    }
}
