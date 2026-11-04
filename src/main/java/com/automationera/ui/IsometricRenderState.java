package com.automationera.ui;

public class IsometricRenderState {
    public static final float PITCH_LIMIT = 90f;
    private static final float PAN_LIMIT = 600f;

    public float rotation = 60f; // 旋转角度（度）
    public float pitch = -30f;   // 仰角（度），负数为俯视；范围 [-90, 90]
    public float scale = 0.4f;   // 缩放倍数
    public float xc = 0f;        // 平移（GUI 像素）
    public float yc = 0f;

    public void addRotation(float delta) { rotation += delta; }
    public void addPitch(float delta) { pitch = Math.max(-PITCH_LIMIT, Math.min(PITCH_LIMIT, pitch + delta)); }
    public void addScale(float factor) { scale = Math.max(0.2f, Math.min(1.5f, scale * factor)); }
    public void addPan(float dx, float dy) {
        xc = Math.max(-PAN_LIMIT, Math.min(PAN_LIMIT, xc + dx));
        yc = Math.max(-PAN_LIMIT, Math.min(PAN_LIMIT, yc + dy));
    }
}
