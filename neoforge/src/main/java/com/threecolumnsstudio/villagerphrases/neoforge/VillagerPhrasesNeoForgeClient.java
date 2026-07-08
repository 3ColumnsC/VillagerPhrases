package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber(modid = "villagerphrases", value = Dist.CLIENT)
public class VillagerPhrasesNeoForgeClient {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof Villager villager)) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
        String profession = profKey != null ? profKey.getPath() : "generic";
        String key = VillagerPhrasesData.nextInteractKey(profession, config);
        if (key != null) {
            net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, Minecraft.getInstance().player);
            Minecraft.getInstance().player.displayClientMessage(msg, false);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide()) return;
        if (!(event.getTarget() instanceof Villager villager)) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        VillagerPhrasesData.markHit(villager);

        if (!config.enableHitPhrases) return;
        if (event.getEntity().getRandom().nextFloat() >= 0.7f) return;

        ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
        String profession = profKey != null ? profKey.getPath() : "generic";
        String key = VillagerPhrasesData.nextHitKey(profession, config);
        if (key != null) {
            net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, event.getEntity());
            event.getEntity().displayClientMessage(msg, false);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        VillagerPhrasesData.checkDeaths(mc.level, mc.player, config);

        if (mc.level.getGameTime() % 100 != 0) return;

        List<Villager> nearby = mc.level.getEntitiesOfClass(
            Villager.class,
            mc.player.getBoundingBox().inflate(6)
        );

        if (!nearby.isEmpty() && mc.level.getRandom().nextFloat() < 0.5f) {
            Villager villager = nearby.get(mc.level.getRandom().nextInt(nearby.size()));
            ResourceLocation profKey = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
            String profession = profKey != null ? profKey.getPath() : "generic";
            String key;

            long dayTime = mc.level.getDayTime() % 24000L;
            if (mc.level.dimensionType().hasSkyLight() && dayTime > 13000L && dayTime < 23000L && config.enableNightPhrases && mc.level.getRandom().nextFloat() < 0.6f) {
                key = VillagerPhrasesData.nextNightKey(profession, config);
                if (key == null) {
                    key = VillagerPhrasesData.nextProximityKey(profession, config);
                }
            } else if (mc.level.isRaining() && config.enableRainPhrases && mc.level.getRandom().nextFloat() < 0.6f) {
                key = VillagerPhrasesData.nextRainKey(profession, config);
                if (key == null) {
                    key = VillagerPhrasesData.nextProximityKey(profession, config);
                }
            } else {
                key = VillagerPhrasesData.nextProximityKey(profession, config);
            }

            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, mc.player);
                mc.player.displayClientMessage(msg, false);
            }
        }
    }
}
