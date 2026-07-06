package com.threecolumnsstudio.villagerphrases.fabric;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.List;

public class VillagerPhrasesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VillagerPhrasesConfig.load(FabricLoader.getInstance().getConfigDir());
        VillagerPhrases.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "villager_phrases");
                }

                @Override
                public void onResourceManagerReload(ResourceManager manager) {
                    VillagerPhrasesData.load(manager);
                    VillagerPhrases.LOGGER.info("VillagerPhrases dialog data reloaded");
                }
            });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!(entity instanceof Villager villager)) return InteractionResult.PASS;

            VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
            if (config == null || !config.isAnyEnabled()) return InteractionResult.PASS;

            String profession = villager.getVillagerData().profession()
                .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
            String key = VillagerPhrasesData.nextInteractKey(profession, config);
            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, player);
                player.sendSystemMessage(msg);
            }
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!(entity instanceof Villager villager)) return InteractionResult.PASS;

            VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
            if (config == null || !config.isAnyEnabled() || !config.enableHitPhrases) return InteractionResult.PASS;

            if (world.getRandom().nextFloat() >= 0.7f) return InteractionResult.PASS;

            String profession = villager.getVillagerData().profession()
                .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
            String key = VillagerPhrasesData.nextHitKey(profession, config);
            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, player);
                player.sendSystemMessage(msg);
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null) return;

            VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
            if (config == null || !config.isAnyEnabled()) return;

            if (client.level.getGameTime() % 100 != 0) return;

            List<Villager> nearby = client.level.getEntitiesOfClass(
                Villager.class,
                client.player.getBoundingBox().inflate(4)
            );

            if (!nearby.isEmpty() && client.level.getRandom().nextFloat() < 0.4f) {
                Villager villager = nearby.get(client.level.getRandom().nextInt(nearby.size()));
                String profession = villager.getVillagerData().profession()
                    .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
                boolean isNight = client.level.isDarkOutside();
                String key;
                if (isNight && config.enableNightPhrases && client.level.getRandom().nextFloat() < 0.6f) {
                    key = VillagerPhrasesData.nextNightKey(profession, config);
                    if (key == null) {
                        key = VillagerPhrasesData.nextProximityKey(profession, config);
                    }
                } else {
                    key = VillagerPhrasesData.nextProximityKey(profession, config);
                }
                if (key != null) {
                    net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, client.player);
                    client.player.sendSystemMessage(msg);
                }
            }
        });
    }
}
