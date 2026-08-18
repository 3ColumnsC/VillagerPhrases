package com.threecolumnsstudio.villagerphrases.chat;

import java.util.Map;

/**
 * Chat colors applied to the villager prefix in messages, one per profession.
 * Tones are inspired by each profession's vanilla attire. Unemployed villagers
 * ("none") and unknown profession ids get {@link #NO_COLOR} and render plain.
 */
public final class ProfessionColors {

    public static final int NO_COLOR = -1;

    private static final Map<String, Integer> COLORS = Map.ofEntries(
        Map.entry("farmer",        0xFFC89B5A),
        Map.entry("fisherman",     0xFF5CA6CC),
        Map.entry("shepherd",      0xFFB8976A),
        Map.entry("fletcher",      0xFFC7A88A),
        Map.entry("librarian",     0xFFEDE4D3),
        Map.entry("cartographer",  0xFFB8D8E8),
        Map.entry("cleric",        0xFF9B6FC4),
        Map.entry("armorer",       0xFF9A9A9A),
        Map.entry("weaponsmith",   0xFF9C7A60),
        Map.entry("toolsmith",     0xFFA99985),
        Map.entry("butcher",       0xFFD96B62),
        Map.entry("leatherworker", 0xFFC98A54),
        Map.entry("mason",         0xFFBCB0A0)
    );

    private ProfessionColors() {}

    public static int colorFor(String professionId) {
        return COLORS.getOrDefault(professionId, NO_COLOR);
    }
}
