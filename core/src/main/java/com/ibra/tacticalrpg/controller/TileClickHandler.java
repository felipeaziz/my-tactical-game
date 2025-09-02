package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

public interface TileClickHandler {
    /**
     * Encontra o tile destacado que foi clicado
     */
    default Tile findClickedHighlightedTile(GameMap grid, OrthographicCamera camera, float mouseX, float mouseY) {
        Vector3 screenMouse = new Vector3(mouseX, mouseY, 0);
        Vector3 worldMouse3D = camera.unproject(screenMouse);
        Vector2 worldMouse = new Vector2(worldMouse3D.x, worldMouse3D.y);

        return grid.getBaseTiles().stream()
            .filter(tile -> tile.isHighlighted() && tile.isPointInsideDiamond(worldMouse))
            .findFirst()
            .orElse(null);
    }
}
