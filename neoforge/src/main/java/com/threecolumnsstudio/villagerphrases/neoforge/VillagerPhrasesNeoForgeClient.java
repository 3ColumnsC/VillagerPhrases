package com.threecolumnsstudio.villagerphrases.neoforge;

import com.threecolumnsstudio.villagerphrases.VillagerPhrases;
import com.threecolumnsstudio.villagerphrases.VillagerPhrasesReloadListener;
import com.threecolumnsstudio.villagerphrases.client.screen.VillagerPhrasesConfigScreen;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;
import com.threecolumnsstudio.villagerphrases.events.VillagerPhrasesClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "villagerphrases", value = Dist.CLIENT)
public class VillagerPhrasesNeoForgeClient {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(VillagerPhrasesNeoForgeClient::onRegisterReloadListeners);
        VillagerPhrasesConfigLoader.load(FMLPaths.CONFIGDIR.get());
        VillagerPhrases.init();
        registerConfigScreen();
    }

    private static void registerConfigScreen() {
        ModList.get().getModContainerById(VillagerPhrases.MOD_ID).ifPresent(container ->
            container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> new VillagerPhrasesConfigScreen(parent)));
    }

    private static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
            Identifier.fromNamespaceAndPath(VillagerPhrases.MOD_ID, "dialogue"),
            new VillagerPhrasesReloadListener()
        );
    }

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
