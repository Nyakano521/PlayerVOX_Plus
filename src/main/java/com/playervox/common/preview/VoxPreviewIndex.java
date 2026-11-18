package com.playervox.common.preview;

import com.playervox.common.preview.VoxPreviewModels.PreviewEntry;
import com.playervox.common.preview.VoxPreviewModels.RadialLine;
import com.playervox.common.preview.VoxPreviewModels.TriggerLine;
import com.playervox.common.radial.RadialEntry;
import com.playervox.common.radial.RadialRegistry;
import com.playervox.common.radial.RadialSlotDefinition;
import com.playervox.common.trigger.TriggerDefinition;
import com.playervox.common.trigger.TriggerEntry;
import com.playervox.common.trigger.TriggerRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * P0：按语音包构建预览列表（一般 / 自定义 / 轮盘）。
 * 不依赖客户端，不播放声音。
 */
public final class VoxPreviewIndex {

    /**
     * 「一般」分类白名单：原版 + 官方联动。
     * 顺序即预览列表默认顺序。
     */
    public static final List<String> GENERAL_TRIGGERS = List.of(
            "hurt", "death", "kill", "low_health", "fall",
            "login", "respawn", "sleep", "pickup", "advancement", "draw",
            "tacz_shoot", "tacz_reload", "tacz_draw", "tacz_melee", "tacz_kill",
            "sbw_reload", "sbw_shoot"
    );

    private static final Set<String> GENERAL_SET = new LinkedHashSet<>(GENERAL_TRIGGERS);

    private VoxPreviewIndex() {}

    public static boolean isGeneralTrigger(String name) {
        return GENERAL_SET.contains(name);
    }

    /** 一般：白名单全列出；未配置则 configured=false、playable=false */
    public static List<TriggerLine> buildGeneral(String packId) {
        List<TriggerLine> lines = new ArrayList<>();
        for (String name : GENERAL_TRIGGERS) {
            lines.add(buildTriggerLine(packId, name));
        }
        return Collections.unmodifiableList(lines);
    }

    /**
     * 自定义：包内有、且不在一般白名单的触发器名。
     * 不含轮盘（轮盘不在 TriggerRegistry）。
     */
    public static List<TriggerLine> buildCustom(String packId) {
        List<TriggerLine> lines = new ArrayList<>();
        for (String name : TriggerRegistry.getTriggerNames(packId)) {
            if (isGeneralTrigger(name)) continue;
            lines.add(buildTriggerLine(packId, name));
        }
        return Collections.unmodifiableList(lines);
    }

    /** 轮盘：已按 page、slot 排序（RadialRegistry 保证） */
    public static List<RadialLine> buildRadial(String packId) {
        List<RadialLine> lines = new ArrayList<>();
        for (RadialSlotDefinition slot : RadialRegistry.getSlots(packId)) {
            List<PreviewEntry> entries = new ArrayList<>();
            for (RadialEntry e : slot.entries) {
                if (e.sound != null) {
                    entries.add(new PreviewEntry(e.sound, Math.max(1, e.weight), e.subtitle));
                }
            }
            boolean playable = !entries.isEmpty();
            lines.add(new RadialLine(slot.page, slot.slot, slot.label, playable,
                    Collections.unmodifiableList(entries)));
        }
        return Collections.unmodifiableList(lines);
    }

    private static TriggerLine buildTriggerLine(String packId, String triggerName) {
        List<TriggerDefinition> defs = TriggerRegistry.getByTrigger(packId, triggerName);
        boolean configured = !defs.isEmpty();
        List<PreviewEntry> entries = new ArrayList<>();
        if (configured) {
            for (TriggerDefinition def : defs) {
                for (TriggerEntry e : def.entries) {
                    if (e.sound != null) {
                        entries.add(new PreviewEntry(e.sound, Math.max(1, e.weight), e.subtitle));
                    }
                }
            }
        }
        boolean playable = configured && !entries.isEmpty();
        return new TriggerLine(triggerName, configured, playable,
                Collections.unmodifiableList(entries));
    }
}