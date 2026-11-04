package com.automationera.network;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * 客户端 → 服务端：进服时上报装了哪些生电相关模组（服务端看不到客户端的模组列表），用于“你不再纯净”系列成就。
 */
public record ClientModsPayload(List<String> mods) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientModsPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("automationera", "client_mods"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientModsPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(16)), ClientModsPayload::mods,
            ClientModsPayload::new);

    /** 只上报这些（不发送完整模组列表） */
    public static final List<String> TRACKED = List.of("malilib", "carpet", "sodium", "iris", "entityculling");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
