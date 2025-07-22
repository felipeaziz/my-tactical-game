package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class CameraController extends InputAdapter {
    private final OrthographicCamera camera;
    private float zoom = 1f;

    private float previousX, previousY, previousZoom;
    private float stableTimer = 0f;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
        Gdx.input.setInputProcessor(this); // Captura eventos do mouse (scroll)
    }

    public void update(float delta) {
        handleKeyboardInput();
        camera.update();

        float dx = Math.abs(camera.position.x - previousX);
        float dy = Math.abs(camera.position.y - previousY);
        float dz = Math.abs(camera.zoom - previousZoom);

        boolean isMoving = dx > 0.5f || dy > 0.5f || dz > 0.01f;

        if (isMoving) {
            stableTimer = 0;
        } else {
            stableTimer += delta;
        }

        previousX = camera.position.x;
        previousY = camera.position.y;
        previousZoom = camera.zoom;
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

    public boolean isCameraStable() {
        return stableTimer > 0.25f; // só estável se parada por mais de 250ms
    }
}
