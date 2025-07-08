package com.ibra.tacticalrpg.map.terraineffect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.ibra.tacticalrpg.map.TerrainType;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private static final Map<TerrainType, Texture> cache = new HashMap<>();

    public static Texture getTexture(TerrainType type) {
        return cache.computeIfAbsent(type,
            t -> new Texture(Gdx.files.internal("terrain/isometric/" + t.getTextureFileName())));
    }

    public static void disposeAll() {
        for (Texture tex : cache.values()) {
            tex.dispose();
        }
        cache.clear();
    }
}
