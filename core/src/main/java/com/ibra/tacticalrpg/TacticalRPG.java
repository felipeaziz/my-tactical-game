package com.ibra.tacticalrpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TacticalRPG extends Game {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new BattleScreen(this));
        image = new Texture("libgdx.png");
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
        image.dispose();
    }
}

