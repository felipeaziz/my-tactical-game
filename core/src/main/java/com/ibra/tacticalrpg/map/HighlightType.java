package com.ibra.tacticalrpg.map;

import com.badlogic.gdx.graphics.Color;

public enum HighlightType {
    NONE(null),
    MOVE(new Color(0f, 0.5f, 1f, 0.35f)),
    ATTACK(new Color(1f, 0f, 0f, 0.35f));

    private final Color color;

    HighlightType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
