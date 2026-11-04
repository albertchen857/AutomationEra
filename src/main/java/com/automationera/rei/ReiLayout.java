package com.automationera.rei;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.resource.Resource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * REI 页面排版工具：按像素宽度换行（不再按固定字数切），放不下时整体缩小字号；
 * 贴图按真实尺寸等比缩放，避免不同宽度的图片被拉伸/挤窄。
 */
public final class ReiLayout {
    private static final int LINE_HEIGHT = 9;
    private static final float SCALE_STEP = 0.05f;
    private static final Map<Identifier, int[]> TEXTURE_SIZE_CACHE = new ConcurrentHashMap<>();

    private ReiLayout() {
    }

    /**
     * 在 (x, y, maxWidth, maxHeight) 区域内绘制自动换行的文字。
     * 先用原始字号，放不下时逐步缩小到 minScale；minScale 仍放不下就按 minScale 画（尽量不溢出宽度）。
     */
    public static Widget text(String str, int x, int y, int maxWidth, int maxHeight, int color, float minScale) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        Text text = Text.literal(str);
        float scale = 1f;
        List<OrderedText> lines = tr.wrapLines(text, maxWidth);
        while (lines.size() * LINE_HEIGHT * scale > maxHeight && scale - SCALE_STEP >= minScale - 1e-4f) {
            scale -= SCALE_STEP;
            lines = tr.wrapLines(text, (int) (maxWidth / scale));
        }
        final float s = scale;
        final List<OrderedText> finalLines = lines;
        return Widgets.createDrawableWidget((ctx, mouseX, mouseY, delta) -> {
            Matrix3x2fStack m = ctx.getMatrices();
            m.pushMatrix();
            m.translate(x, y);
            m.scale(s, s);
            for (int i = 0; i < finalLines.size(); i++) {
                ctx.drawText(tr, finalLines.get(i), 0, i * LINE_HEIGHT, color, false);
            }
            m.popMatrix();
        });
    }

    /**
     * 把贴图等比缩放放进 (x, y, boxW, boxH)，水平居中、底部对齐。
     */
    public static Widget fitTexture(Identifier id, int x, int y, int boxW, int boxH) {
        int[] size = textureSize(id);
        int w = size[0], h = size[1];
        float s = Math.min((float) boxW / w, (float) boxH / h);
        int dw = Math.round(w * s), dh = Math.round(h * s);
        return Widgets.createTexturedWidget(id,
                new Rectangle(x + (boxW - dw) / 2, y + boxH - dh, dw, dh),
                0, 0, w, h, w, h);
    }

    /** 读取 PNG 头里的宽高；读取失败时按正方形处理。 */
    public static int[] textureSize(Identifier id) {
        return TEXTURE_SIZE_CACHE.computeIfAbsent(id, key -> {
            Resource res = MinecraftClient.getInstance().getResourceManager().getResource(key).orElse(null);
            if (res != null) {
                try (InputStream is = res.getInputStream(); DataInputStream in = new DataInputStream(is)) {
                    in.skipNBytes(16); // PNG 签名(8) + IHDR 长度(4) + "IHDR"(4)
                    int w = in.readInt(), h = in.readInt();
                    if (w > 0 && h > 0) return new int[]{w, h};
                } catch (Exception ignored) {
                }
            }
            return new int[]{1, 1};
        });
    }

    public static void clearCache() {
        TEXTURE_SIZE_CACHE.clear();
    }
}
