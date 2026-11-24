package com.playervox.compat.playerrevive;

import com.playervox.common.handler.VanillaTriggerHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import team.creative.playerrevive.api.event.PlayerRevivedEvent;
import team.creative.playerrevive.server.PlayerReviveServer;

/**
 * PlayerRevive：
 * - pr_downed：致命伤被改成倒地（LivingDeath 被取消且已在流血）
 * - pr_revived：被成功救起
 */
public class PlayerReviveTriggerHandler {

    /**
     * 优先级低于 PR 的 HIGHEST，确保先 startBleeding + cancel，再判断。
     */
    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void onDeathForDowned(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.isCanceled()) return;
        if (!PlayerReviveServer.isBleeding(player)) return;

        VanillaTriggerHandler.fireForPlayer(player, "pr_downed", event.getSource(), 0, null, null);
    }

    @SubscribeEvent
    public static void onRevived(PlayerRevivedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        VanillaTriggerHandler.fireForPlayer(player, "pr_revived", null, 0, null, null);
    }
}