package com.automationera.ui;

import com.automationera.OutputRecipe;
import com.automationera.OutputRecipe.MachineInfo;
import com.automationera.OutputRecipe.SelectionBox;
import com.automationera.basic.ExportFile;
import com.automationera.keybinding.ModKeyBinding;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix3x2fStack;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TutorialGroupScreen extends Screen {
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("AutomationEraTutorial");
    private static final float ROTATE_DEG_PER_SEC = 90f;
    private static final int BLOCK_LINES = 6;
    private static final int BLOCK_LIST_W = 110;
    private static final int BLOCK_ROW_H = 16;
    private static final int BOTTOM_BAR_H = 28;

    private final String group;
    private int selectedMac;
    private int currentStep;
    private boolean autoRotate = false;
    private boolean blocklistConsist;
    private IsometricRenderState renderState;

    private List<MachineInfo> choose = List.of();
    private MachineInfo machine;
    private int stepCount;
    private TutorialManager.Structure structure;      // 第 1 步到当前步的累计
    private TutorialManager.Structure stepStructure;  // 只有当前这一步
    private boolean cumulative;                        // 是否为累计渲染的新格式教程
    private List<AnalysisList.Entry> blockList = List.of();
    private int blockScroll = 0;
    private int leftW;
    private int descY;
    private Text exportMessage;
    private long lastFrameNanos = 0;

    public TutorialGroupScreen(String group, int selectedMac, int currentStep, IsometricRenderState renderState, boolean blocklistConsist) {
        super(Text.translatable("tutorial.group." + group));
        this.group = group;
        this.selectedMac = selectedMac;
        this.currentStep = currentStep;
        this.renderState = renderState;
        this.blocklistConsist = blocklistConsist;
    }

    public TutorialGroupScreen(String group, int selectedMac, int currentStep, IsometricRenderState renderState) {
        this(group, selectedMac, currentStep, renderState, false);
    }

    private static List<MachineInfo> machinesOf(String group) {
        return switch (group) {
            case "factory" -> OutputRecipe.factoryMachines;
            case "special" -> OutputRecipe.specialMachines;
            default -> OutputRecipe.farmMachines;
        };
    }

    // ---------------------------------------------------------------- 布局

    private int blockListX() {
        return this.width - BLOCK_LIST_W - 8;
    }

    private int blockListY() {
        return 10;
    }

    private int blockListH() {
        return Math.min(BLOCK_LINES, blockList.size()) * BLOCK_ROW_H;
    }

    private boolean inBlockList(double mouseX, double mouseY) {
        int x = blockListX(), y = blockListY();
        return !blockList.isEmpty() && mouseX >= x - 4 && mouseX < x + BLOCK_LIST_W + 4 && mouseY >= y - 4 && mouseY < y + blockListH() + 4;
    }

    private boolean inStructureView(double mouseX, double mouseY) {
        return mouseX >= leftW && mouseY < this.height - BOTTOM_BAR_H && !inBlockList(mouseX, mouseY);
    }

    @Override
    protected void init() {
        choose = machinesOf(group);
        if (choose.isEmpty()) {
            machine = null;
            return;
        }
        selectedMac = Math.max(0, Math.min(selectedMac, choose.size() - 1));
        machine = choose.get(selectedMac);
        stepCount = TutorialManager.countSteps(machine.id());
        currentStep = Math.max(1, Math.min(currentStep, Math.max(1, stepCount)));
        loadStep();

        leftW = Math.max(110, this.width / 5);
        int listTop = 25;
        int listH = Math.max(40, Math.min(choose.size() * 20 + 4, this.height - listTop - BOTTOM_BAR_H - 70));
        MachineEntryList list = new MachineEntryList(client, leftW, listH, listTop, 20);
        for (int m = 0; m < choose.size(); m++) {
            int idx = m;
            MachineInfo info = choose.get(m);
            list.addEntry(idx, new ItemStack(info.icon()), info.title(), () -> selectMachine(idx));
        }
        list.selectByIndex(selectedMac);
        this.addDrawableChild(list);
        descY = listTop + listH + 6;

        int y = this.height - 25;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("tutorial.ui.close"),
                btn -> this.client.setScreen(new TutorialMainScreen())).dimensions(20, y, 60, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("tutorial.ui.prev"),
                btn -> lastStep()).dimensions(120, y, 40, 20).build()).active = currentStep > 1;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("tutorial.ui.next"),
                btn -> nextStep()).dimensions(160, y, 40, 20).build()).active = currentStep < stepCount;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("tutorial.ui.output"),
                btn -> output()).dimensions(200, y, 60, 20).build()).active = stepCount > 0;
        // 仰角改为上下拖拽调整，原来的仰角按钮已删除
        this.addDrawableChild(ButtonWidget.builder(Text.literal("▶"),
                btn -> autoRotate = !autoRotate).dimensions(260, y, 20, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↺"),
                btn -> resetKey()).dimensions(280, y, 20, 20).build());
        ButtonWidget rei = this.addDrawableChild(ButtonWidget.builder(Text.literal("REI"),
                btn -> openReiPage()).dimensions(300, y, 20, 20).build());
        rei.active = FabricLoader.getInstance().isModLoaded("roughlyenoughitems") && OutputRecipe.reiKeyOfTutorial(machine.id()) != null;
        this.addDrawableChild(ButtonWidget.builder(Text.literal(blocklistConsist ? "DIFF" : "NORM"),
                btn -> {
                    blocklistConsist = !blocklistConsist;
                    loadStep();
                    this.clearAndInit();
                }).dimensions(320, y, 40, 20).build());
    }

    /**
     * 两种教程数据：
     * - 有 steps.json（0.1.9.1 重新拆分的机器）：每个步骤文件只含本步新增方块，第 N 步显示第 1~N 步的累计；
     *   材料表 NORM = 累计到当前步，DIFF = 只有这一步。
     * - 没有 steps.json（特殊机器、小机器，保持原样）：每步就是一个完整投影，按旧逻辑显示。
     */
    private void loadStep() {
        cumulative = !TutorialManager.loadStepMeta(machine.id()).isEmpty();
        NbtCompound now = TutorialManager.loadNbtFromResource(machine.id(), currentStep);
        if (!cumulative) {
            structure = TutorialManager.loadStructure(machine.id(), currentStep);
            stepStructure = null;
            if (now == null) {
                blockList = List.of();
            } else if (blocklistConsist && currentStep > 1) {
                NbtCompound prev = TutorialManager.loadNbtFromResource(machine.id(), currentStep - 1);
                blockList = new AnalysisList(now, prev, machine.blockListConsistent()).getResult();
            } else {
                blockList = new AnalysisList(now).getResult();
            }
            blockScroll = 0;
            return;
        }
        structure = TutorialManager.loadCumulative(machine.id(), currentStep);
        stepStructure = TutorialManager.loadStructure(machine.id(), currentStep);
        if (now == null) {
            blockList = List.of();
        } else if (blocklistConsist) {
            blockList = new AnalysisList(now).getResult();
        } else {
            List<NbtCompound> upto = new ArrayList<>();
            for (int s = 1; s <= currentStep; s++) {
                NbtCompound n = TutorialManager.loadNbtFromResource(machine.id(), s);
                if (n != null) upto.add(n);
            }
            blockList = new AnalysisList(upto).getResult();
        }
        blockScroll = 0;
    }

    /** 步骤说明：「Y 范围 · 这一步的类型」+ 这一步方块在旧教程里对应那一步的说明 */
    private Text stepDescription() {
        TutorialManager.StepMeta meta = TutorialManager.stepMeta(machine.id(), currentStep);
        if (meta == null) {
            return Text.translatable("tutorial." + machine.id() + ".step" + currentStep);
        }
        String y = meta.y0() == meta.y1() ? "Y" + meta.y0() : "Y" + meta.y0() + "–Y" + meta.y1();
        Text head = Text.literal(y + " · ").append(Text.translatable("tutorial.ui.role." + meta.role())).formatted(Formatting.YELLOW);
        return Text.empty().append(head).append("\n")
                .append(Text.translatableWithFallback("tutorial." + machine.id() + ".step" + meta.old(), ""));
    }

    // ---------------------------------------------------------------- 渲染

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x77000000);
        long now = System.nanoTime();
        // 按真实时间旋转，旧版每帧 +2° 会随帧率变化
        if (autoRotate && lastFrameNanos != 0) {
            renderState.addRotation(ROTATE_DEG_PER_SEC * Math.min(0.1f, (now - lastFrameNanos) / 1e9f));
        }
        lastFrameNanos = now;
        if (machine != null) {
            renderStructure(ctx);
        }
        super.render(ctx, mouseX, mouseY, delta);
        Text title = machine == null ? this.title : this.title.copy().append(Text.literal(" - ")).append(machine.title());
        ctx.drawTextWithShadow(this.textRenderer, title, 8, 8, 0xFFFFFFFF);
        if (machine == null) return;

        if (stepCount > 0) {
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(currentStep + " / " + stepCount), 205, this.height - 38, 0xFFBBBBBB);
            drawFittedText(ctx, stepDescription(), 6, descY, leftW - 10, this.height - BOTTOM_BAR_H - 4 - descY, 0xFFFFFFFF);
        } else {
            ctx.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("tutorial.ui.missing"),
                    (leftW + this.width) / 2, this.height / 2, 0xFFFFAA00);
        }
        if (exportMessage != null) {
            ctx.drawTextWithShadow(this.textRenderer, exportMessage, leftW + 4, this.height - BOTTOM_BAR_H - 12, 0xFF55FF55);
        }
        renderBlockList(ctx);
    }

    /** 自动换行；放不下时缩小字号（英文步骤说明比中文长很多，以前会压到底部按钮上） */
    private void drawFittedText(DrawContext ctx, Text text, int x, int y, int maxWidth, int maxHeight, int color) {
        float scale = 1f;
        List<OrderedText> lines = this.textRenderer.wrapLines(text, maxWidth);
        while (lines.size() * 9 * scale > maxHeight && scale > 0.55f) {
            scale -= 0.05f;
            lines = this.textRenderer.wrapLines(text, (int) (maxWidth / scale));
        }
        Matrix3x2fStack m = ctx.getMatrices();
        m.pushMatrix();
        m.translate(x, y);
        m.scale(scale, scale);
        for (int i = 0; i < lines.size(); i++) {
            ctx.drawTextWithShadow(this.textRenderer, lines.get(i), 0, i * 9, color);
        }
        m.popMatrix();
    }

    private void renderStructure(DrawContext ctx) {
        if (structure == null || structure.blocks().isEmpty()) return;
        int x1 = leftW, y1 = 0, x2 = this.width, y2 = this.height - BOTTOM_BAR_H;
        // 保持 0.1.9 的构图：中心点在屏幕 (2/3 宽 - 30, 5/8 高 - 40)，大小相对屏幕宽度
        float centerX = this.width * 2f / 3f - 30 + renderState.xc - x1;
        float centerY = this.height * 5f / 8f - 40 + renderState.yc - y1;
        float blockSize = renderState.scale * this.width / structure.maxDimension() / 1.2f;
        List<SelectionBox> boxes = List.of();
        if (cumulative) {
            // 框出这一步新增方块的范围（步骤是自动拆分的，旧的手写选区框对不上）
            if (stepStructure != null && !stepStructure.blocks().isEmpty()) {
                boxes = List.of(new SelectionBox(stepStructure.minX(), stepStructure.minY(), stepStructure.minZ(),
                        stepStructure.maxX(), stepStructure.maxY(), stepStructure.maxZ()));
            }
        } else if (machine.selectionBoxes() != null && currentStep - 1 < machine.selectionBoxes().size()) {
            boxes = machine.selectionBoxes().get(currentStep - 1);
        }
        float alpha = 0.5f + 0.3f * (float) Math.abs(Math.sin(System.currentTimeMillis() / 300.0));
        ctx.state.addSpecialElement(new TutorialStructureRenderState(structure, renderState.rotation, renderState.pitch,
                centerX, centerY, blockSize, boxes, alpha, x1, y1, x2, y2, null));
    }

    private void renderBlockList(DrawContext ctx) {
        if (blockList.isEmpty()) return;
        int total = blockList.size();
        int x = blockListX(), y0 = blockListY(), height = blockListH();
        ctx.fill(x - 4, y0 - 4, x + BLOCK_LIST_W + 4, y0 + height + 4, 0x40FFFFFF);
        int maxScroll = Math.max(0, total - BLOCK_LINES);
        blockScroll = Math.max(0, Math.min(blockScroll, maxScroll));
        int textMaxW = BLOCK_LIST_W - 18 - 38 - (maxScroll > 0 ? 6 : 0);
        for (int i = 0; i < BLOCK_LINES && (i + blockScroll) < total; i++) {
            AnalysisList.Entry entry = blockList.get(i + blockScroll);
            int y = y0 + i * BLOCK_ROW_H;
            ctx.drawItem(entry.stack, x, y);
            String name = entry.displayName.getString();
            if (this.textRenderer.getWidth(name) > textMaxW) {
                name = this.textRenderer.trimToWidth(name, textMaxW - this.textRenderer.getWidth("…")) + "…";
            }
            ctx.drawText(this.textRenderer, name, x + 18, y + 4, 0xFFEBEBEB, true);
            String count = entry.countBoxGroup();
            int countX = x + BLOCK_LIST_W - (maxScroll > 0 ? 8 : 2) - this.textRenderer.getWidth(count);
            ctx.drawText(this.textRenderer, count, countX, y + 4, 0xFFFF512C, true);
        }
        if (maxScroll > 0) {
            int barX = x + BLOCK_LIST_W - 4, barW = 4;
            ctx.fill(barX, y0, barX + barW, y0 + height, 0x22000000);
            int sliderH = Math.max(12, height * BLOCK_LINES / total);
            int sliderY = y0 + (height - sliderH) * blockScroll / maxScroll;
            ctx.fill(barX + 1, sliderY, barX + barW - 1, sliderY + sliderH, 0xFFAAAAAA);
        }
    }

    // ---------------------------------------------------------------- 输入

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (click.button() == 0 && inStructureView(click.x(), click.y())) {
            if (this.client.isShiftPressed()) {
                // Shift + 拖拽：平移
                renderState.addPan((float) offsetX, (float) offsetY);
            } else {
                // 左右拖拽：旋转；上下拖拽：仰角（向下拖 = 更俯视）
                renderState.addRotation((float) offsetX);
                renderState.addPitch((float) -offsetY);
            }
            autoRotate = false;
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (inBlockList(mouseX, mouseY)) {
            int maxScroll = Math.max(0, blockList.size() - BLOCK_LINES);
            blockScroll = Math.max(0, Math.min(maxScroll, blockScroll - (int) Math.signum(vertical)));
            return true;
        }
        if (inStructureView(mouseX, mouseY)) {
            renderState.addScale(1.0f + (float) (vertical + horizontal) * 0.08f);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int total = blockList.size();
        int maxScroll = Math.max(0, total - BLOCK_LINES);
        int barX = blockListX() + BLOCK_LIST_W - 4, y0 = blockListY(), listH = blockListH();
        if (maxScroll > 0 && click.x() >= barX && click.x() < barX + 4 && click.y() >= y0 && click.y() < y0 + listH) {
            int sliderH = Math.max(12, listH * BLOCK_LINES / total);
            int relY = (int) click.y() - y0 - sliderH / 2;
            blockScroll = Math.max(0, Math.min(maxScroll, relY * maxScroll / Math.max(1, listH - sliderH)));
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (ModKeyBinding.openTutorialKey.matchesKey(input)) {
            this.client.setScreen(new TutorialMainScreen());
            return true;
        }
        if (ModKeyBinding.leftKey.matchesKey(input)) { rotateKey(-10f); return true; }
        if (ModKeyBinding.rightKey.matchesKey(input)) { rotateKey(10f); return true; }
        if (ModKeyBinding.downKey.matchesKey(input)) { pitchKey(-5f); return true; }
        if (ModKeyBinding.upKey.matchesKey(input)) { pitchKey(5f); return true; }
        if (ModKeyBinding.inKey.matchesKey(input)) { zoomKey(1.2f); return true; }
        if (ModKeyBinding.outKey.matchesKey(input)) { zoomKey(1 / 1.2f); return true; }
        if (ModKeyBinding.nextKey.matchesKey(input)) { nextStep(); return true; }
        if (ModKeyBinding.prevKey.matchesKey(input)) { lastStep(); return true; }
        if (ModKeyBinding.nextTKey.matchesKey(input)) { nextTutorialKey(); return true; }
        if (ModKeyBinding.prevTKey.matchesKey(input)) { prevTutorialKey(); return true; }
        if (ModKeyBinding.resetKey.matchesKey(input)) { resetKey(); return true; }
        return super.keyPressed(input);
    }

    // ---------------------------------------------------------------- 操作

    public void rotateKey(float delta) {
        renderState.addRotation(delta);
    }

    public void pitchKey(float delta) {
        renderState.addPitch(delta);
    }

    /** factor: 缩放倍数（>1 放大）。旧版传的是 ±0.2 加到乘数上，会直接把缩放变成最小值 */
    public void zoomKey(float factor) {
        renderState.addScale(factor);
    }

    private void selectMachine(int idx) {
        if (idx == selectedMac) return;
        selectedMac = idx;
        currentStep = 1;
        exportMessage = null;
        this.clearAndInit();
    }

    public void nextStep() {
        if (machine != null && currentStep < stepCount) {
            currentStep++;
            exportMessage = null;
            this.clearAndInit();
        }
    }

    public void lastStep() {
        if (machine != null && currentStep > 1) {
            currentStep--;
            exportMessage = null;
            this.clearAndInit();
        }
    }

    public void nextTutorialKey() {
        if (selectedMac < choose.size() - 1) selectMachine(selectedMac + 1);
    }

    public void prevTutorialKey() {
        if (selectedMac > 0) selectMachine(selectedMac - 1);
    }

    public void resetKey() {
        renderState = new IsometricRenderState();
        autoRotate = false;
        this.clearAndInit();
    }

    /** 导出整台机器（_full.nbt） */
    public void output() {
        if (machine == null || stepCount <= 0) return;
        Path out = ExportFile.exportTutorialFolder(machine.id(), stepCount);
        exportMessage = out == null
                ? Text.translatable("tutorial.ui.output.fail")
                : Text.translatable("tutorial.ui.output.done", "schematics/AutomationEra/" + out.getFileName());
    }

    private void openReiPage() {
        if (machine == null || !FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) return;
        String key = OutputRecipe.reiKeyOfTutorial(machine.id());
        if (key != null) {
            com.automationera.rei.reiPlugin.openMachinePage(key);
        }
    }
}
