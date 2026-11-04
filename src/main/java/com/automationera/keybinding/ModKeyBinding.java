package com.automationera.keybinding;

import com.automationera.ui.TutorialMainScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class ModKeyBinding {
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger("AutomationEraKeyBinding");
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("automationera", "main"));
    public static KeyBinding openTutorialKey;
    public static KeyBinding leftKey;
    public static KeyBinding rightKey;
    public static KeyBinding upKey;
    public static KeyBinding downKey;
    public static KeyBinding inKey;
    public static KeyBinding outKey;
    public static KeyBinding nextKey;
    public static KeyBinding prevKey;
    public static KeyBinding nextTKey;
    public static KeyBinding prevTKey;
    public static KeyBinding resetKey;

    public static void register() {
        openTutorialKey = registerKey("key.automationera.open_tutorial_ui", GLFW.GLFW_KEY_G);
        leftKey = registerKey("key.automationera.tutorial_rotate_left", -1);
        rightKey = registerKey("key.automationera.tutorial_rotate_right", -1);
        upKey = registerKey("key.automationera.tutorial_move_up", -1);
        downKey = registerKey("key.automationera.tutorial_move_down", -1);
        inKey = registerKey("key.automationera.tutorial_zoom_in", -1);
        outKey = registerKey("key.automationera.tutorial_zoom_out", -1);
        nextKey = registerKey("key.automationera.tutorial_next_step", GLFW.GLFW_KEY_EQUAL);
        prevKey = registerKey("key.automationera.tutorial_last_step", GLFW.GLFW_KEY_MINUS);
        nextTKey = registerKey("key.automationera.tutorial_next_tutorial", -1);
        prevTKey = registerKey("key.automationera.tutorial_last_tutorial", -1);
        resetKey = registerKey("key.automationera.tutorial_reset", GLFW.GLFW_KEY_R);

        // 其余按键只在教程界面里生效，由 TutorialGroupScreen.keyPressed 处理（界面打开时 KeyBinding 不会收到按键）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openTutorialKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new TutorialMainScreen());
                }
            }
        });
    }

    private static KeyBinding registerKey(String translationKey, int keyCode) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                translationKey,
                InputUtil.Type.KEYSYM,
                keyCode,
                CATEGORY
        ));
    }
}
