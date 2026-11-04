package com.playervox.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@OnlyIn(Dist.CLIENT)
public class ClientVoxSoundTracker {

    private static final Map<Integer, SoundInstance> activeVoxSounds = new ConcurrentHashMap<>();

    public static SoundInstance get(int entityId) {
        return activeVoxSounds.get(entityId);
    }

    public static void put(int entityId, SoundInstance instance) {
        activeVoxSounds.put(entityId, instance);
    }

    public static void cleanupStopped(Minecraft mc) {
        Iterator<Map.Entry<Integer, SoundInstance>> it = activeVoxSounds.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, SoundInstance> entry = it.next();
            if (!mc.getSoundManager().isActive(entry.getValue())) {
                it.remove();
            }
        }
    }
}