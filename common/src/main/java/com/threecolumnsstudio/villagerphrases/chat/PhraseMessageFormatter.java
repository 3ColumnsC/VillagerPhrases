package com.threecolumnsstudio.villagerphrases.chat;

import com.threecolumnsstudio.villagerphrases.dialogue.VillagerPhrasesData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public final class PhraseMessageFormatter {

    private PhraseMessageFormatter() {}

    public static Component formatMessage(Villager villager, String key, Player player) {
        Component phrase = Component.translatable(key, player.getName());
        int color = ProfessionColors.colorFor(VillagerPhrasesData.professionId(villager));
        Component name = coloredPrefix(villager);

        if (color == ProfessionColors.NO_COLOR) {
            return Component.literal("<")
                .append(name)
                .append(Component.literal(">: "))
                .append(phrase);
        }

        Style frame = Style.EMPTY.withColor(color);
        return Component.literal("<").withStyle(frame)
            .append(name)
            .append(Component.literal(">").withStyle(frame))
            .append(Component.literal(": "))
            .append(phrase);
    }

    private static Component coloredPrefix(Villager villager) {
        Component prefix = buildPrefix(villager);
        int color = ProfessionColors.colorFor(VillagerPhrasesData.professionId(villager));
        if (color == ProfessionColors.NO_COLOR) {
            return prefix;
        }
        return prefix.copy().withStyle(style -> style.withColor(color));
    }

    private static Component buildPrefix(Villager villager) {
        if (villager.hasCustomName()) {
            return villager.getCustomName();
        }
        String profId = VillagerPhrasesData.professionId(villager);
        if (profId.equals("none")) {
            return Component.translatable("entity.minecraft.villager");
        }
        return Component.translatable("entity.minecraft.villager." + profId);
    }
}
