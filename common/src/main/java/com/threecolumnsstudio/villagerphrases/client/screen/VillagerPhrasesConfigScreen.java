package com.threecolumnsstudio.villagerphrases.client.screen;

import java.util.function.Consumer;

import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfig;
import com.threecolumnsstudio.villagerphrases.config.VillagerPhrasesConfigLoader;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class VillagerPhrasesConfigScreen extends Screen {

    private static final int WIDGET_WIDTH = 280;
    private static final int WIDGET_HEIGHT = 20;
    private static final int ROW_SPACING = 6;
    private static final int INITIAL_SCROLL_HEIGHT = 120;
    private static final int BUTTON_WIDTH = 100;
    private static final int CONTENT_TOP_PADDING = 8;
    private static final int CONTENT_BOTTOM_PADDING = 4;

    private final Screen parent;
    private final HeaderAndFooterLayout layout;
    private final TabManager tabManager;

    private TabNavigationBar tabNavigationBar;
    private ScrollableLayout scrollArea;

    private boolean normal;
    private boolean humor;
    private boolean night;
    private boolean rain;
    private boolean hit;
    private boolean death;
    private int globalCooldown;
    private boolean ironGolemProximity;
    private boolean ironGolemHit;
    private boolean ironGolemDeath;
    private int ironGolemCooldown;

    public VillagerPhrasesConfigScreen(Screen parent) {
        super(Component.translatable("villagerphrases.config.title"));
        this.parent = parent;
        this.layout = new HeaderAndFooterLayout(this);
        this.tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
        readConfig();
    }

    private void readConfig() {
        VillagerPhrasesConfig config = VillagerPhrasesConfigLoader.getInstance();
        if (config == null) {
            return;
        }
        this.normal = config.enableNormalPhrases;
        this.humor = config.enableHumorPhrases;
        this.night = config.enableNightPhrases;
        this.rain = config.enableRainPhrases;
        this.hit = config.enableHitPhrases;
        this.death = config.enableDeathPhrases;
        this.globalCooldown = config.globalMessageCooldownTicks;
        this.ironGolemProximity = config.enableIronGolemProximityPhrases;
        this.ironGolemHit = config.enableIronGolemHitPhrases;
        this.ironGolemDeath = config.enableIronGolemDeathPhrases;
        this.ironGolemCooldown = config.ironGolemMessageCooldownTicks;
    }

    @Override
    protected void init() {
        this.scrollArea = new ScrollableLayout(this.minecraft, buildContentGrid(), INITIAL_SCROLL_HEIGHT);

        this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
            .addTabs(new ConfigTab(Component.translatable("villagerphrases.config.tab.general"), this.scrollArea))
            .build();
        this.addRenderableWidget(this.tabNavigationBar);

        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("villagerphrases.config.apply"), button -> apply())
            .width(BUTTON_WIDTH)
            .build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
            .width(BUTTON_WIDTH)
            .build());

        this.layout.visitWidgets(this::addRenderableWidget);
        this.tabNavigationBar.selectTab(0, false);
        this.repositionElements();
    }

    private GridLayout buildContentGrid() {
        GridLayout grid = new GridLayout();
        grid.columnSpacing(0).rowSpacing(ROW_SPACING);

        GridLayout.RowHelper rows = grid.createRowHelper(1);
        rows.addChild(toggle("villagerphrases.config.normal", this.normal, value -> this.normal = value));
        rows.addChild(toggle("villagerphrases.config.humor", this.humor, value -> this.humor = value));
        rows.addChild(toggle("villagerphrases.config.night", this.night, value -> this.night = value));
        rows.addChild(toggle("villagerphrases.config.rain", this.rain, value -> this.rain = value));
        rows.addChild(toggle("villagerphrases.config.hit", this.hit, value -> this.hit = value));
        rows.addChild(toggle("villagerphrases.config.death", this.death, value -> this.death = value));
        rows.addChild(cooldownSlider("villagerphrases.config.global_cooldown", this.globalCooldown, value -> this.globalCooldown = value));
        rows.addChild(toggle("villagerphrases.config.iron_golem_proximity", this.ironGolemProximity, value -> this.ironGolemProximity = value));
        rows.addChild(toggle("villagerphrases.config.iron_golem_hit", this.ironGolemHit, value -> this.ironGolemHit = value));
        rows.addChild(toggle("villagerphrases.config.iron_golem_death", this.ironGolemDeath, value -> this.ironGolemDeath = value));
        rows.addChild(cooldownSlider("villagerphrases.config.iron_golem_cooldown", this.ironGolemCooldown, value -> this.ironGolemCooldown = value));

        return grid;
    }

    private CycleButton<Boolean> toggle(String translationKey, boolean initialValue, Consumer<Boolean> setter) {
        return CycleButton.onOffBuilder(initialValue)
            .create(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Component.translatable(translationKey),
                (button, value) -> setter.accept(value));
    }

    private CooldownSlider cooldownSlider(String translationKey, int initialTicks, java.util.function.IntConsumer setter) {
        return new CooldownSlider(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT,
            Component.translatable(translationKey), initialTicks, setter);
    }

    private void apply() {
        VillagerPhrasesConfig config = VillagerPhrasesConfigLoader.getInstance();
        if (config != null) {
            config.enableNormalPhrases = this.normal;
            config.enableHumorPhrases = this.humor;
            config.enableNightPhrases = this.night;
            config.enableRainPhrases = this.rain;
            config.enableHitPhrases = this.hit;
            config.enableDeathPhrases = this.death;
            config.globalMessageCooldownTicks = this.globalCooldown;
            config.enableIronGolemProximityPhrases = this.ironGolemProximity;
            config.enableIronGolemHitPhrases = this.ironGolemHit;
            config.enableIronGolemDeathPhrases = this.ironGolemDeath;
            config.ironGolemMessageCooldownTicks = this.ironGolemCooldown;
            VillagerPhrasesConfigLoader.save();
        }
        onClose();
    }

    @Override
    protected void repositionElements() {
        if (this.tabNavigationBar == null) {
            return;
        }
        this.tabNavigationBar.setWidth(this.width);
        this.tabNavigationBar.arrangeElements();
        int headerBottom = this.tabNavigationBar.getRectangle().bottom();
        ScreenRectangle tabArea = new ScreenRectangle(0, headerBottom, this.width,
            this.height - this.layout.getFooterHeight() - headerBottom);
        this.tabManager.setTabArea(tabArea);
        this.layout.setHeaderHeight(headerBottom);
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics graphics) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND,
            0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderMenuBackground(graphics, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR,
            0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
    }

    private static final class ConfigTab implements Tab {

        private final Component title;
        private final ScrollableLayout content;

        private ConfigTab(Component title, ScrollableLayout content) {
            this.title = title;
            this.content = content;
        }

        @Override
        public Component getTabTitle() {
            return this.title;
        }

        @Override
        public Component getTabExtraNarration() {
            return Component.empty();
        }

        @Override
        public void visitChildren(Consumer<AbstractWidget> consumer) {
            this.content.visitWidgets(consumer);
        }

        @Override
        public void doLayout(ScreenRectangle area) {
            this.content.arrangeElements();
            int viewportHeight = area.height() - CONTENT_TOP_PADDING - CONTENT_BOTTOM_PADDING;
            this.content.setMaxHeight(viewportHeight);
            this.content.arrangeElements();
            int centeredX = area.left() + (area.width() - this.content.getWidth()) / 2;
            this.content.setPosition(centeredX, area.top() + CONTENT_TOP_PADDING);
        }
    }
}
