package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.events.VillagerPhrasesClientEvents;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "villagerphrases", value = Dist.CLIENT)
public class VillagerPhrasesNeoForgeClient {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        VillagerPhrasesClientEvents.onInteract(
            event.getEntity(),
            event.getLevel(),
            event.getHand(),
            event.getTarget()
        );
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        VillagerPhrasesClientEvents.onAttack(event.getEntity(), event.getTarget());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            VillagerPhrasesClientEvents.onClientTick(mc.level, mc.player);
        }
    }
}
