package com.playervox.compat.playerrevive;

import com.playervox.PlayerVoxMod;
import net.minecraftforge.common.MinecraftForge;

/**
 * PlayerRevive 兼容：仅在 PR 已加载时注册。
 */
public class PlayerReviveCompat {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(PlayerReviveTriggerHandler.class);
        PlayerVoxMod.LOGGER.info("PlayerVOX: PlayerRevive detected, pr_downed / pr_revived enabled.");
    }
}