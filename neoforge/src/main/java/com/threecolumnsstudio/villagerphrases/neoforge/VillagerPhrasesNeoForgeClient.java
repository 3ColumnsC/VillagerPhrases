package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.villager.Villager;
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

        String profession = villager.getVillagerData().profession()
            .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
        String key = VillagerPhrasesData.nextInteractKey(profession, config);
        if (key != null) {
            net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, Minecraft.getInstance().player);
            Minecraft.getInstance().player.sendSystemMessage(msg);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide()) return;
        if (!(event.getTarget() instanceof Villager villager)) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
        if (config == null || !config.isAnyEnabled() || !config.enableHitPhrases) return;

        if (event.getEntity().getRandom().nextFloat() >= 0.7f) return;

        String profession = villager.getVillagerData().profession()
            .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
        String key = VillagerPhrasesData.nextHitKey(profession, config);
        if (key != null) {
            net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, event.getEntity());
            event.getEntity().sendSystemMessage(msg);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        VillagerPhrasesConfig config = VillagerPhrasesConfig.getInstance();
        if (config == null || !config.isAnyEnabled()) return;

        if (mc.level.getGameTime() % 100 != 0) return;

        List<Villager> nearby = mc.level.getEntitiesOfClass(
            Villager.class,
            mc.player.getBoundingBox().inflate(4),
            v -> true
        );

        if (!nearby.isEmpty() && mc.level.getRandom().nextFloat() < 0.4f) {
            Villager villager = nearby.get(mc.level.getRandom().nextInt(nearby.size()));
            String profession = villager.getVillagerData().profession()
                .unwrapKey().map(key -> key.identifier().getPath()).orElse("generic");
            boolean isNight = mc.level.isDarkOutside();
            String key;
            if (isNight && config.enableNightPhrases && mc.level.getRandom().nextFloat() < 0.6f) {
                key = VillagerPhrasesData.nextNightKey(profession, config);
                if (key == null) {
                    key = VillagerPhrasesData.nextProximityKey(profession, config);
                }
            } else {
                key = VillagerPhrasesData.nextProximityKey(profession, config);
            }
            if (key != null) {
                net.minecraft.network.chat.Component msg = VillagerPhrasesData.formatMessage(villager, key, mc.player);
                mc.player.sendSystemMessage(msg);
            }
        }
    }
}
