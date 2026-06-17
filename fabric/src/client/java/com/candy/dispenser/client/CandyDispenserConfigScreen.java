package com.candy.dispenser.client;

import java.util.Collections;
import java.util.List;

import com.candy.dispenser.config.CandyDispenserConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

/**
 * Hand-built vanilla config screen for Candy Dispenser (Fabric) — single-column, full-width layout.
 *
 * <p>Replaces the former Cloth AutoConfig screen, which throws on Minecraft 26.2 (Cloth has no 26.2
 * build). Mirrors the proven Veinminer++ screen: a custom {@link ContainerObjectSelectionList} that
 * places each option on its own full-width row, grouped under centred section headers.
 *
 * <h3>Layout (top to bottom)</h3>
 * <pre>
 *                 Candy Dispenser Config        (title, in HeaderAndFooterLayout header)
 *   ──────────────── General ─────────────────  (HeaderEntry)
 *     Enabled                       [on/off]    (OptionEntry)
 *     Recipe Enabled                [on/off]    (OptionEntry)
 *   ──────────────── Healing ─────────────────  (HeaderEntry)
 *     Boost When Damaged            [on/off]    (OptionEntry)
 *     Healing Hunger Target         [slider]    (OptionEntry)
 *     Healing Saturation Target     [slider]    (OptionEntry)
 *                     [ Done ]                  (footer button)
 * </pre>
 *
 * <p>This class lives in {@code fabric/src/client/} and is referenced only from the ModMenu
 * entrypoint ({@link ModMenuIntegration}), so it never loads on a dedicated server.
 */
public class CandyDispenserConfigScreen extends Screen {

    /** Screen title rendered in the header strip. */
    private static final Component TITLE =
            Component.translatable("candy_dispenser.config.title");

    /** The screen to return to when Done is pressed (or the screen is otherwise closed). */
    private final Screen parent;

    /** The custom scrolling list; non-null after {@link #init()}. */
    private @Nullable ConfigList list;

    /** Manages the header (title), scrollable contents (list), and footer (Done button). */
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public CandyDispenserConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    // -------------------------------------------------------------------------
    // Screen lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void init() {
        layout.addTitleHeader(TITLE, font);
        list = layout.addToContents(new ConfigList(minecraft, width, this));
        layout.addToFooter(
                Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                      .width(200)
                      .build());
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (list != null) {
            list.updateSize(width, layout);
        }
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }

    /**
     * Persist on close — whether via Done, Escape, or any other navigation. The option listeners in
     * {@link ConfigList} have already written their values into the {@link CandyDispenserConfig}
     * POJO; {@link CandyDispenserConfig#save()} clamps and flushes to disk.
     */
    @Override
    public void removed() {
        CandyDispenserConfig.save();
    }

    // =========================================================================
    // Custom scrolling list
    // =========================================================================

    static final class ConfigList
            extends ContainerObjectSelectionList<ConfigList.AbstractEntry> {

        /** Row width matches OptionsList (310 px). */
        private static final int ROW_WIDTH = 310;

        /** Height of a standard option row — matches OptionsList's DEFAULT_ITEM_HEIGHT. */
        private static final int OPTION_ROW_HEIGHT = 25;

        /** Height of a section header row (single line of text, plus a little padding). */
        private static final int HEADER_ROW_HEIGHT = 18;

        /** Owning screen, so option entries can read {@code screen.width}. */
        private final Screen screen;

        ConfigList(Minecraft minecraft, int screenWidth, Screen screen) {
            // height and y are set via updateSize() from repositionElements(); pass 0 for now.
            super(minecraft, screenWidth, 0, 0, OPTION_ROW_HEIGHT);
            this.centerListVertically = false;
            this.screen = screen;
            populateEntries();
        }

        @Override
        public int getRowWidth() {
            return ROW_WIDTH;
        }

        // ---------------------------------------------------------------------
        // Populate
        // ---------------------------------------------------------------------

        private void populateEntries() {
            CandyDispenserConfig.CandyDispenserSettings cfg =
                    CandyDispenserConfig.get().candyDispenser;

            // ---- General ----
            addHeader("candy_dispenser.config.section.general");
            addOption(OptionInstance.createBoolean(
                    "candy_dispenser.config.enabled",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("candy_dispenser.config.enabled.tooltip")),
                    cfg.enabled,
                    val -> CandyDispenserConfig.get().candyDispenser.enabled = val));
            addOption(OptionInstance.createBoolean(
                    "candy_dispenser.config.recipeEnabled",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("candy_dispenser.config.recipeEnabled.tooltip")),
                    cfg.recipeEnabled,
                    val -> CandyDispenserConfig.get().candyDispenser.recipeEnabled = val));

            // ---- Healing ----
            addHeader("candy_dispenser.config.section.healing");
            addOption(OptionInstance.createBoolean(
                    "candy_dispenser.config.boostWhenDamaged",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("candy_dispenser.config.boostWhenDamaged.tooltip")),
                    cfg.boostWhenDamaged,
                    val -> CandyDispenserConfig.get().candyDispenser.boostWhenDamaged = val));
            addOption(new OptionInstance<>(
                    "candy_dispenser.config.healingHungerTarget",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("candy_dispenser.config.healingHungerTarget.tooltip")),
                    (caption, val) -> Component.translatable("options.generic_value", caption, Component.literal(String.valueOf(val))),
                    new OptionInstance.IntRange(18, 20),
                    cfg.healingHungerTarget,
                    val -> CandyDispenserConfig.get().candyDispenser.healingHungerTarget = val));
            addOption(new OptionInstance<>(
                    "candy_dispenser.config.healingSaturationTarget",
                    OptionInstance.cachedConstantTooltip(
                            Component.translatable("candy_dispenser.config.healingSaturationTarget.tooltip")),
                    (caption, val) -> Component.translatable("options.generic_value", caption, Component.literal(String.valueOf(val))),
                    new OptionInstance.IntRange(15, 20),
                    cfg.healingSaturationTarget,
                    val -> CandyDispenserConfig.get().candyDispenser.healingSaturationTarget = val));
        }

        private void addHeader(String langKey) {
            addEntry(new HeaderEntry(Component.translatable(langKey), minecraft), HEADER_ROW_HEIGHT);
        }

        private void addOption(OptionInstance<?> option) {
            addEntry(new OptionEntry(option, minecraft, screen), OPTION_ROW_HEIGHT);
        }

        // =====================================================================
        // Entry types
        // =====================================================================

        /** Base type shared by both entry kinds — no behaviour of its own. */
        abstract static class AbstractEntry
                extends ContainerObjectSelectionList.Entry<AbstractEntry> {
        }

        // ---------------------------------------------------------------------
        // HeaderEntry — centred, non-interactive section title
        // ---------------------------------------------------------------------

        static final class HeaderEntry extends AbstractEntry {

            private final StringWidget widget;

            HeaderEntry(Component title, Minecraft minecraft) {
                this.widget = new StringWidget(ROW_WIDTH, 9, title, minecraft.font);
            }

            @Override
            public void extractContent(
                    GuiGraphicsExtractor graphics,
                    int mouseX, int mouseY,
                    boolean hovered,
                    float a) {
                int textWidth = this.widget.getWidth();
                int centreX = this.getContentXMiddle() - textWidth / 2;
                int centreY = this.getContentY() + (this.getContentHeight() - 9) / 2;
                this.widget.setPosition(centreX, centreY);
                this.widget.extractRenderState(graphics, mouseX, mouseY, a);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return Collections.emptyList();
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(widget);
            }
        }

        // ---------------------------------------------------------------------
        // OptionEntry — one full-width option widget per row
        // ---------------------------------------------------------------------

        static final class OptionEntry extends AbstractEntry {

            private final AbstractWidget widget;
            private final Screen screen;

            OptionEntry(OptionInstance<?> option, Minecraft minecraft, Screen screen) {
                this.screen = screen;
                this.widget = option.createButton(minecraft.options, 0, 0, ROW_WIDTH);
            }

            @Override
            public void extractContent(
                    GuiGraphicsExtractor graphics,
                    int mouseX, int mouseY,
                    boolean hovered,
                    float a) {
                int widgetX = this.getContentXMiddle() - this.widget.getWidth() / 2;
                this.widget.setPosition(widgetX, this.getContentY());
                this.widget.extractRenderState(graphics, mouseX, mouseY, a);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(widget);
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(widget);
            }
        }
    }
}
