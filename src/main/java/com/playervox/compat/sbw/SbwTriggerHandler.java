package com.playervox.compat.sbw;

import com.atsuishio.superbwarfare.api.event.ReloadEvent;
import com.atsuishio.superbwarfare.api.event.ShootEvent;
import com.playervox.common.handler.VanillaTriggerHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 监听 Superb Warfare 的换弹 / 射击事件。
 * 仅在 SBW 已加载时由 SbwCompat.register() 注册。
 */
public class SbwTriggerHandler {

    /** 换弹开始（Pre） */
    @SubscribeEvent
    public static void onReload(ReloadEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        String gunId = getGunId(event.getStack());
        VanillaTriggerHandler.fireForPlayer(player, "sbw_reload", null, 0, null, gunId);
    }

    /** 射击（用 Post，表示已经开了一枪） */
    @SubscribeEvent
    public static void onShoot(ShootEvent.Post event) {
        if (!(event.getShooter() instanceof ServerPlayer player)) return;
        String gunId = getGunId(player.getMainHandItem());
        VanillaTriggerHandler.fireForPlayer(player, "sbw_shoot", null, 0, null, gunId);
    }

    private static String getGunId(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null ? id.toString() : "";
    }
}