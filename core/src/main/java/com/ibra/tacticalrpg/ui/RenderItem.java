package com.ibra.tacticalrpg.ui;

public class RenderItem {
    public final float drawOrderY;
    public final Runnable render;

    public RenderItem(float drawOrderY, Runnable render) {
        this.drawOrderY = drawOrderY;
        this.render = render;
    }
}
