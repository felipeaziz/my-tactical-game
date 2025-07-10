package com.ibra.tacticalrpg.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ibra.tacticalrpg.TacticalRPG;
import com.ibra.tacticalrpg.controller.CameraController;
import com.ibra.tacticalrpg.map.isometric.GameMap;

public class GameScreen extends ScreenAdapter {
    private final TacticalRPG game;
    private final OrthographicCamera camera;
    private final CameraController cameraController;
    private final GameMap map;

    public GameScreen(TacticalRPG game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, (9 + 9) * 32 / 4f, 0); // Start at the origin
        cameraController = new CameraController(camera);
        this.map = new GameMap();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);// Set the clear color to black with full opacity
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT | Gdx.gl.GL_DEPTH_BUFFER_BIT);// Clear the screen
        game.getBatch().setProjectionMatrix(camera.combined);

        cameraController.update();

        map.render(game.getBatch(), camera);
    }
}
