package com.playervox.client;

import com.playervox.common.preview.VoxPreviewIndex;
import com.playervox.common.preview.VoxPreviewModels.RadialLine;
import com.playervox.common.preview.VoxPreviewModels.TriggerLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 语音包预览界面（P1：分类 + 列表壳，不播放）。
 */
@OnlyIn(Dist.CLIENT)
public class VoxPreviewScreen extends Screen {

    private enum Tab {
        GENERAL, RADIAL, CUSTOM
    }

    private final Screen parent;
    private final String packId;

    private Tab currentTab = Tab.GENERAL;
    private PreviewList list;

    private Button tabGeneral;
    private Button tabRadial;
    private Button tabCustom;

    public VoxPreviewScreen(Screen parent, String packId) {
        super(Component.translatable("gui.playervox.preview_title", packId));
        this.parent = parent;
        this.packId = packId;
    }

    @Override
    protected void init() {
        super.init();

        int tabY = 32;
        int tabW = 80;
        int gap = 4;
        int totalTabs = tabW * 3 + gap * 2;
        int tabStart = (this.width - totalTabs) / 2;

        this.tabGeneral = Button.builder(Component.translatable("gui.playervox.preview_tab_general"), b -> switchTab(Tab.GENERAL))
                .pos(tabStart, tabY).size(tabW, 20).build();
        this.tabRadial = Button.builder(Component.translatable("gui.playervox.preview_tab_radial"), b -> switchTab(Tab.RADIAL))
                .pos(tabStart + tabW + gap, tabY).size(tabW, 20).build();
        this.tabCustom = Button.builder(Component.translatable("gui.playervox.preview_tab_custom"), b -> switchTab(Tab.CUSTOM))
                .pos(tabStart + (tabW + gap) * 2, tabY).size(tabW, 20).build();

        this.addRenderableWidget(this.tabGeneral);
        this.addRenderableWidget(this.tabRadial);
        this.addRenderableWidget(this.tabCustom);

        // 列表：Tab 下方到返回按钮上方
        this.list = new PreviewList(this.minecraft, this.width, this.height, 56, this.height - 50, 24);
        this.addWidget(this.list);
        rebuildList();

        this.addRenderableWidget(Button.builder(Component.translatable("gui.playervox.preview_back"), b -> onClose())
                .pos(this.width / 2 - 50, this.height - 25).size(100, 20).build());

        updateTabStyles();
    }

    private void switchTab(Tab tab) {
        if (this.currentTab == tab) return;
        VoxPreviewPlayer.stop();
        this.currentTab = tab;
        rebuildList();
        updateTabStyles();
    }

    private void updateTabStyles() {
        // 1.20.1 Button 无 setAlpha 区分选中时，靠 rebuild 即可；可在此扩展
    }

    private void rebuildList() {
        this.list.resetEntries();
        switch (this.currentTab) {
            case GENERAL -> {
                for (TriggerLine line : VoxPreviewIndex.buildGeneral(packId)) {
                    this.list.addEntry(PreviewEntry.trigger(line));
                }
            }
            case CUSTOM -> {
                for (TriggerLine line : VoxPreviewIndex.buildCustom(packId)) {
                    this.list.addEntry(PreviewEntry.trigger(line));
                }
            }
            case RADIAL -> {
                for (RadialLine line : VoxPreviewIndex.buildRadial(packId)) {
                    this.list.addEntry(PreviewEntry.radial(line));
                }
            }
        }
        this.list.setScrollAmount(0);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        this.list.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

        // ---- 底部试听字幕 ----
        String sub = VoxPreviewPlayer.getCurrentSubtitle();
        if (sub != null && !sub.isEmpty()) {
            Component subComp = resolveSubtitle(sub);
            int maxWidth = Math.min(this.width - 40, 320);
            var lines = this.font.split(subComp, maxWidth);
            int lineH = this.font.lineHeight + 2;
            int maxLines = 4;
            int shown = Math.min(lines.size(), maxLines);
            int boxH = shown * lineH + 8;
            int boxW = maxWidth + 16;
            int boxX = (this.width - boxW) / 2;
            int boxY = this.height - 30 - boxH; // 在返回按钮上方

            // 半透明底
            graphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0x88000000);

            int textY = boxY + 4;
            for (int i = 0; i < shown; i++) {
                var line = lines.get(i);
                int textW = this.font.width(line);
                int textX = (this.width - textW) / 2;
                // 超出行数时最后一行可仍完整显示 split 结果；需要省略可再改
                graphics.drawString(this.font, line, textX, textY, 0xFFFFFF, false);
                textY += lineH;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /** 与正式播放一致：像翻译键则 translate，否则当明文 */
    private Component resolveSubtitle(String text) {
        if (text.indexOf('.') > 0 && text.indexOf(' ') < 0) {
            return Component.translatable(text);
        }
        return Component.literal(text);
    }

    @Override
    public void onClose() {
        VoxPreviewPlayer.stop();
        // 不要复用旧的选择界面实例，避免返回后按钮点不动
        this.minecraft.setScreen(new VoxSelectScreen());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        VoxPreviewPlayer.tick();
    }

    // ---- 列表 ----

    @OnlyIn(Dist.CLIENT)
    class PreviewList extends ObjectSelectionList<PreviewEntry> {

        public PreviewList(Minecraft mc, int width, int height, int y0, int y1, int itemHeight) {
            super(mc, width, height, y0, y1, itemHeight);
            this.setRenderBackground(true);
            this.setRenderTopAndBottom(true);
        }

        void resetEntries() {
            super.clearEntries();
        }

        // 若 clearEntries 私有，用：
        // protected void clear() { this.children().clear(); this.setScrollAmount(0); }

        @Override
        public int addEntry(PreviewEntry entry) {
            return super.addEntry(entry);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }

        @Override
        public int getRowWidth() {
            return 280;
        }
    }

    // ---- 行 ----

    @OnlyIn(Dist.CLIENT)
    static class PreviewEntry extends ObjectSelectionList.Entry<PreviewEntry> {

        final boolean playable;
        final Component label;
        final String lineKey;
        final List<com.playervox.common.preview.VoxPreviewModels.PreviewEntry> entries;

        private PreviewEntry(boolean playable, Component label, String lineKey,
                             List<com.playervox.common.preview.VoxPreviewModels.PreviewEntry> entries) {
            this.playable = playable;
            this.label = label;
            this.lineKey = lineKey;
            this.entries = entries;
        }

        static PreviewEntry trigger(TriggerLine line) {
            return new PreviewEntry(
                    line.canClick(),
                    Component.literal(line.triggerName),
                    "trigger:" + line.triggerName,
                    line.entries
            );
        }

        static PreviewEntry radial(RadialLine line) {
            return new PreviewEntry(
                    line.canClick(),
                    Component.literal("P" + line.page + " #" + line.slot + "  " + line.label),
                    "radial:" + line.page + ":" + line.slot,
                    line.entries
            );
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left,
                           int width, int height, int mouseX, int mouseY,
                           boolean hovered, float partialTick) {
            boolean playing = VoxPreviewPlayer.isPlaying(lineKey);
            int color;
            if (!playable) {
                color = 0x666666;
            } else if (playing) {
                color = 0x55FF55;
            } else if (hovered) {
                color = 0xFFFFFF;
            } else {
                color = 0xCCCCCC;
            }

            var font = Minecraft.getInstance().font;
            graphics.drawString(font, label, left + 4, top + 6, color, false);

            String trail = !playable ? "—" : (playing ? "\u25A0" : "\u25B6");
            int trailX = left + width - font.width(trail) - 8;
            graphics.drawString(font, trail, trailX, top + 6, color, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!playable || button != 0) return false;
            VoxPreviewPlayer.play(lineKey, entries);
            return true;
        }

        @Override
        public Component getNarration() {
            return label;
        }
    }
}