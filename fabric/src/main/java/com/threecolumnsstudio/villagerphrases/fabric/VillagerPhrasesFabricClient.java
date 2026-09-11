package com.threecolumnsstudio.villagerphrases.fabric;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;
import com.threecolumnsstudio.villagerphrases.dialogue.VillagerPhrasesData;
import com.threecolumnsstudio.villagerphrases.events.VillagerPhrasesClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class VillagerPhrasesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VillagerPhrasesConfigLoader.load(FabricLoader.getInstance().getConfigDir());
        VillagerPhrases.init();

        registerReloadListener();

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            VillagerPhrasesClientEvents.onInteract(player, world, hand, entity);
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == InteractionHand.MAIN_HAND) {
                VillagerPhrasesClientEvents.onAttack(player, entity);
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null && client.player != null) {
                VillagerPhrasesClientEvents.onClientTick(client.level, client.player);
            }
        });
    }

    private void registerReloadListener() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return ResourceLocation.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "villager_phrases");
                }

                @Override
                public void onResourceManagerReload(ResourceManager manager) {
                    VillagerPhrasesData.load(manager);
                }
            });
    }
}
