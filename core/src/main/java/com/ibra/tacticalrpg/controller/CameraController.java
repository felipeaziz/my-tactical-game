package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class CameraController extends InputAdapter {
    private final OrthographicCamera camera;
    private float zoom = 1f;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
        Gdx.input.setInputProcessor(this); // Captura eventos do mouse (scroll)
    }

    public void update() {
        handleKeyboardInput();
        camera.update();
    }

    private void handleKeyboardInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 1;
        }
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        zoom += amountY * 0.1f;
        zoom = MathUtils.clamp(zoom, 0.5f, 2f);
        camera.zoom = zoom;
        return true;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
