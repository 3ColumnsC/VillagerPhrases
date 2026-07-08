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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

public class VillagerPhrasesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VillagerPhrasesConfig.load(FabricLoader.getInstance().getConfigDir());
        VillagerPhrases.init();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return ResourceLocation.tryParse(VillagerPhrases.MOD_ID + ":villager_phrases");
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

            ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
            String profession = profKey != null ? profKey.getPath() : "generic";
            String key = VillagerPhrasesData.nextInteractKey(profession, config);
            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, player);
                player.displayClientMessage(msg, false);
            }
            return InteractionResult.PASS;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClientSide()) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!(entity instanceof Villager villager)) return InteractionResult.PASS;

            VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
            if (config == null || !config.isAnyEnabled()) return InteractionResult.PASS;

            VillagerPhrasesData.markHit(villager);

            if (!config.enableHitPhrases) return InteractionResult.PASS;
            if (world.getRandom().nextFloat() >= 0.7f) return InteractionResult.PASS;

            ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
            String profession = profKey != null ? profKey.getPath() : "generic";
            String key = VillagerPhrasesData.nextHitKey(profession, config);
            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, player);
                player.displayClientMessage(msg, false);
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null) return;

            VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
            if (config == null || !config.isAnyEnabled()) return;

            VillagerPhrasesData.checkDeaths(client.level, client.player, config);

            if (client.level.getGameTime() % 100 != 0) return;

            List<Villager> nearby = client.level.getEntitiesOfClass(
                Villager.class,
                client.player.getBoundingBox().inflate(6)
            );

            if (!nearby.isEmpty() && client.level.getRandom().nextFloat() < 0.5f) {
                Villager villager = nearby.get(client.level.getRandom().nextInt(nearby.size()));
            ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
            String profession = profKey != null ? profKey.getPath() : "generic";
                String key;

                long dayTime = client.level.getDayTime() % 24000L;
                if (client.level.dimensionType().hasSkyLight() && dayTime > 13000L && dayTime < 23000L && config.enableNightPhrases && client.level.getRandom().nextFloat() < 0.6f) {
                    key = VillagerPhrasesData.nextNightKey(profession, config);
                    if (key == null) {
                        key = VillagerPhrasesData.nextProximityKey(profession, config);
                    }
                } else if (client.level.isRaining() && config.enableRainPhrases && client.level.getRandom().nextFloat() < 0.6f) {
                    key = VillagerPhrasesData.nextRainKey(profession, config);
                    if (key == null) {
                        key = VillagerPhrasesData.nextProximityKey(profession, config);
                    }
                } else {
                    key = VillagerPhrasesData.nextProximityKey(profession, config);
                }

                if (key != null) {
                    net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, client.player);
                    client.player.displayClientMessage(msg, false);
                }
            }
        });
    }
}
