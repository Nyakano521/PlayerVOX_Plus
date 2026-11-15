package com.playervox.compat.sbw;

import com.playervox.PlayerVoxMod;
import net.minecraftforge.common.MinecraftForge;

/**
 * Superb Warfare 兼容：仅在 SBW 已加载时注册事件。
 */
public class SbwCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(SbwTriggerHandler.class);
        PlayerVoxMod.LOGGER.info("PlayerVOX: Superb Warfare detected, SBW gun triggers enabled.");
    }
}