package com.automationera;

import com.automationera.keybinding.ModKeyBinding;
import com.automationera.network.ClientModsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import java.util.List;
import com.automationera.ui.TutorialStructureRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifier -> Atlas
*minecraft:textures/atlas/blocks.png -> minecraft:blocks
*[22:06:28] [Render thread/INFO] (AutomationEraClient) minecraft:textures/atlas/signs.png -> minecraft:signs
*[22:06:28] [Render thread/INFO] (AutomationEraClient) minecraft:textures/atlas/shield_patterns.png -> minecraft:shield_patterns
*[22:06:28] [Render thread/INFO] (AutomationEraClient) minecraft:textures/atlas/banner_patterns.png -> minecraft:banner_patterns
*[22:06:28] [Render thread/INFO] (AutomationEraClient) minecraft:textures/atlas/armor_trims.png -> minecraft:armor_trims
*/
public class AutomationeraClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("AutomationEraClient");
    @Override
    public void onInitializeClient() {
        ModKeyBinding.register();
        PictureInPictureRendererRegistry.register(ctx -> new TutorialStructureRenderer(ctx.vertexConsumers()));
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            com.automationera.rei.ReiDisplayCache.init();
        }
        // 进服时把装了的生电模组告诉服务端（“你不再纯净”系列成就）；服务端没装本模组时不发送
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!ClientPlayNetworking.canSend(ClientModsPayload.ID)) return;
            List<String> mods = ClientModsPayload.TRACKED.stream().filter(FabricLoader.getInstance()::isModLoaded).toList();
            if (!mods.isEmpty()) ClientPlayNetworking.send(new ClientModsPayload(mods));
        });
    }
}
