package com.threecolumnsstudio.villagerphrases.client.screen;

import java.util.function.IntConsumer;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public final class CooldownSlider extends AbstractSliderButton {

    private static final int MIN_TICKS = 0;
    private static final int MAX_TICKS = 600;
    private static final int STEP_TICKS = 20;
    private static final int TICKS_PER_SECOND = 20;

    private final Component label;
    private final IntConsumer onValueChanged;

    public CooldownSlider(int x, int y, int width, int height, Component label, int initialTicks, IntConsumer onValueChanged) {
        super(x, y, width, height, Component.empty(), toSliderValue(initialTicks));
        this.label = label;
        this.onValueChanged = onValueChanged;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        if (this.label == null) {
            return;
        }
        setMessage(Component.translatable(
            "villagerphrases.config.cooldown_value",
            this.label,
            currentTicks() / TICKS_PER_SECOND
        ));
    }

    @Override
    protected void applyValue() {
        updateMessage();
        if (this.onValueChanged != null) {
            this.onValueChanged.accept(currentTicks());
        }
    }

    private int currentTicks() {
        int stepped = (int) Math.round(this.value * (MAX_TICKS - MIN_TICKS) / STEP_TICKS) * STEP_TICKS;
        return Math.max(MIN_TICKS, Math.min(MAX_TICKS, stepped));
    }

    private static double toSliderValue(int ticks) {
        int clamped = Math.max(MIN_TICKS, Math.min(MAX_TICKS, ticks));
        int stepped = Math.round((float) clamped / STEP_TICKS) * STEP_TICKS;
        return (double) stepped / (MAX_TICKS - MIN_TICKS);
    }
}
