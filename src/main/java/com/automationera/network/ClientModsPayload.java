package com.automationera.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 客户端 → 服务端：进服时上报装了哪些生电相关模组（服务端看不到客户端的模组列表），用于“你不再纯净”系列成就。
 */
public record ClientModsPayload(List<String> mods) implements CustomPayload {
    public static final CustomPayload.Id<ClientModsPayload> ID = new CustomPayload.Id<>(Identifier.of("automationera", "client_mods"));
    public static final PacketCodec<RegistryByteBuf, ClientModsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING.collect(PacketCodecs.toList(16)), ClientModsPayload::mods,
            ClientModsPayload::new);

    /** 只上报这些（不发送完整模组列表） */
    public static final List<String> TRACKED = List.of("malilib", "carpet", "sodium", "iris", "entityculling");

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
