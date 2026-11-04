package com.automationera.mixin.rei;

import com.automationera.rei.ReiDisplayCache;
import dev.architectury.networking.NetworkManager;
import me.shedaniel.rei.impl.common.networking.DisplaySyncPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** 记录服务器通过 REI 同步过来的配方，用于本地缓存（见 ReiDisplayCache） */
@Mixin(value = DisplaySyncPacket.class, remap = false)
public abstract class DisplaySyncPacketMixin {
    @Inject(method = "handle", at = @At("HEAD"))
    private void automationera$capture(NetworkManager.PacketContext context, CallbackInfo ci) {
        DisplaySyncPacket self = (DisplaySyncPacket) (Object) this;
        ReiDisplayCache.onServerSync(self.syncType() == DisplaySyncPacket.SyncType.SET, List.copyOf(self.displays()));
    }
}
