package com.playervox.client;

import com.playervox.common.init.ModSounds;
import com.playervox.common.preview.VoxPreviewModels.PreviewEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 预览专用本地播放：不广播、不进冷却。同时最多一段。
 */
@OnlyIn(Dist.CLIENT)
public final class VoxPreviewPlayer {

    private static @Nullable SoundInstance current;
    private static @Nullable String currentKey;
    private static @Nullable String currentSubtitle;

    private VoxPreviewPlayer() {}

    /** 声音自然结束后清状态，避免按钮一直停在「播放中」 */
    public static void tick() {
        if (current == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null || !mc.getSoundManager().isActive(current)) {
            current = null;
            currentKey = null;
            currentSubtitle = null;
        }
    }

    public static boolean isPlaying(@Nullable String key) {
        tick(); // 查询前同步真实播放状态
        return key != null && key.equals(currentKey);
    }

    @Nullable
    public static String getCurrentSubtitle() {
        return currentSubtitle;
    }


    public static void stop() {
        Minecraft mc = Minecraft.getInstance();
        if (current != null && mc.getSoundManager() != null) {
            mc.getSoundManager().stop(current);
        }
        current = null;
        currentKey = null;
        currentSubtitle = null;
    }

    /**
     * 按 weight 随机一条并播放。
     * @param lineKey 行唯一键（用于再点同一行停止），如 "trigger:hurt" 或 "radial:1:2"
     * @return 是否开始播放
     */
    public static boolean play(String lineKey, List<PreviewEntry> entries) {
        if (entries == null || entries.isEmpty()) return false;

        // 再点同一行 → 停止
        if (lineKey.equals(currentKey)) {
            stop();
            return false;
        }

        stop();

        PreviewEntry chosen = pickWeighted(entries);
        if (chosen == null || chosen.sound == null) return false;

        // 客户端无音频缓存则不可播
        if (VoxSoundAssetsManager.INSTANCE.getSoundData(chosen.sound) == null) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        SoundInstance instance = new VoxPositionedSoundInstance(
                ModSounds.VOX.get(),
                SoundSource.MASTER,
                1.0f,
                1.0f,
                mc.player.getX(),
                mc.player.getY(),
                mc.player.getZ(),
                chosen.sound
        );

        mc.getSoundManager().play(instance);
        current = instance;
        currentKey = lineKey;
        currentSubtitle = chosen.text;
        return true;
    }

    private static @Nullable PreviewEntry pickWeighted(List<PreviewEntry> entries) {
        int total = 0;
        for (PreviewEntry e : entries) {
            total += Math.max(1, e.weight);
        }
        if (total <= 0) return null;
        int r = ThreadLocalRandom.current().nextInt(total);
        for (PreviewEntry e : entries) {
            r -= Math.max(1, e.weight);
            if (r < 0) return e;
        }
        return entries.get(entries.size() - 1);
    }
}