package com.threecolumnsstudio.villagerphrases.chat;

import com.threecolumnsstudio.villagerphrases.dialogue.VillagerPhrasesData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public final class PhraseMessageFormatter {

    private PhraseMessageFormatter() {}

    public static Component formatMessage(Entity speaker, String key, Player player) {
        Component phrase = Component.translatable(key, player.getName());
        int color = colorFor(speaker);
        Component name = coloredPrefix(speaker, color);

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

    private static int colorFor(Entity speaker) {
        if (speaker instanceof Villager villager) {
            return ProfessionColors.colorFor(VillagerPhrasesData.professionId(villager));
        }
        if (speaker instanceof IronGolem) {
            return ProfessionColors.IRON_GOLEM;
        }
        return ProfessionColors.NO_COLOR;
    }

    private static Component coloredPrefix(Entity speaker, int color) {
        Component prefix = buildPrefix(speaker);
        if (color == ProfessionColors.NO_COLOR) {
            return prefix;
        }
        return prefix.copy().withStyle(style -> style.withColor(color));
    }

    private static Component buildPrefix(Entity speaker) {
        if (speaker.hasCustomName()) {
            return speaker.getCustomName();
        }
        if (speaker instanceof Villager villager) {
            String profId = VillagerPhrasesData.professionId(villager);
            if (profId.equals("none")) {
                return Component.translatable("entity.minecraft.villager");
            }
            return Component.translatable("entity.minecraft.villager." + profId);
        }
        return Component.translatable(speaker.getType().getDescriptionId());
    }
}
