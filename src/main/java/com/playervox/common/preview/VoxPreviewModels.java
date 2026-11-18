package com.playervox.common.preview;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 预览界面用的只读数据模型（P0）。
 */
public final class VoxPreviewModels {

    private VoxPreviewModels() {}

    /** 一般 / 自定义 列表行 */
    public static final class TriggerLine {
        /** 触发器名，如 hurt、tacz_reload */
        public final String triggerName;
        /** 包内是否有该触发器配置 */
        public final boolean configured;
        /**
         * 结构上是否可试听：已配置且至少有一条带 sound 的 entry。
         * 客户端是否真有音频文件，留给 P2 再收紧。
         */
        public final boolean playable;
        /** 用于随机试听；未配置时为空列表 */
        public final List<PreviewEntry> entries;

        public TriggerLine(String triggerName, boolean configured, boolean playable,
                           List<PreviewEntry> entries) {
            this.triggerName = triggerName;
            this.configured = configured;
            this.playable = playable;
            this.entries = entries;
        }

        /** 界面：可点击 = playable（不单独显示 configured 文案） */
        public boolean canClick() {
            return playable;
        }
    }

    /** 轮盘列表行，按 page → slot 排序后给出 */
    public static final class RadialLine {
        public final int page;
        public final int slot;
        public final String label;
        public final boolean playable;
        public final List<PreviewEntry> entries;

        public RadialLine(int page, int slot, String label, boolean playable,
                          List<PreviewEntry> entries) {
            this.page = page;
            this.slot = slot;
            this.label = label;
            this.playable = playable;
            this.entries = entries;
        }

        public boolean canClick() {
            return playable;
        }
    }

    /** 试听用的单条 entry */
    public static final class PreviewEntry {
        public final ResourceLocation sound;
        public final int weight;
        /** 对应 JSON text，可能为 null */
        @Nullable
        public final String text;

        public PreviewEntry(ResourceLocation sound, int weight, @Nullable String text) {
            this.sound = sound;
            this.weight = weight;
            this.text = text;
        }
    }
}