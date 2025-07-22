package com.ibra.tacticalrpg.isometric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ibra.tacticalrpg.GameContext;
import com.ibra.tacticalrpg.TacticalRPG;
import com.ibra.tacticalrpg.controller.CameraController;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.ui.EventLog;
import com.ibra.tacticalrpg.ui.GameUIRenderer;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GameScreen extends ScreenAdapter implements EventLogger {
    private static final int LOG_SIZE = 5;

    private final TacticalRPG game;
    private final OrthographicCamera camera;
    private final CameraController cameraController;
    private final GameController gameController;
    private final PlayerController playerController;
    private GameMap map;
    private final GameUIRenderer uiRenderer;
    private final BitmapFont font;
    private EventLog eventLog;
    private GameContext gameContext;

    private enum TurnState {PLAYER, GAME_OVER}

    private TurnState turnState = TurnState.PLAYER;

    public GameScreen(TacticalRPG game) {
        this.game = game;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, (9 + 9) * 32 / 4f, 0); // Start at the origin
        cameraController = new CameraController(camera);
        gameController = new GameController();
        gameController.setLogger(this);
        playerController = new PlayerController();

        this.font = new BitmapFont();
        this.uiRenderer = new GameUIRenderer(font);
        initializeGame();
    }

    private void initializeGame() {
        this.eventLog = new EventLog(LOG_SIZE);
        this.map = new GameMap();
        this.gameContext = new GameContext(gameController, playerController, cameraController, this.map, this);
        gameController.setup(map.getEntities());
        turnState = TurnState.PLAYER;
        for (Entity ent : map.getEntities()) {
            ent.setGameContext(gameContext);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);// Set the clear color to black with full opacity
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// Clear the screen
        game.getBatch().setProjectionMatrix(camera.combined);

        if (turnState == TurnState.GAME_OVER && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            this.initializeGame();
        }
        cameraController.update(delta);

        map.render(game.getBatch(), camera);
        checkEndGame();
        updateEntityMovement(delta);
        handleTurns();

        Entity currentPlayer = gameController.getCurrentEntity();
        if(cameraController.isCameraStable() && currentPlayer instanceof PlayerEntity && !currentPlayer.isMoving()) {
            PlayerEntity player = (PlayerEntity) currentPlayer;
            uiRenderer.renderActionMenu(game.getBatch(), map, player);
        }
        renderEventLog();
    }

    private void updateEntityMovement(float delta) {
        Entity current = gameContext.getGameController().getCurrentEntity();
        if (current != null && current.isMoving()) {
            current.updateMovement(this.map, delta);
        }
    }

    private void handleTurns() {
        if (turnState == TurnState.GAME_OVER) return;
        gameContext.getGameController().handleTurns();
    }

    private void checkEndGame() {
        if (turnState == TurnState.GAME_OVER) return;  // Se já está em GAME_OVER, não precisa verificar novamente

        if (gameContext.getGameController().getGameStatus() == GameController.GameStatus.PLAYER_DEFEAT) {
            turnState = TurnState.GAME_OVER;
            eventLog.add("Você perdeu!");
            eventLog.add("Pressione R para reiniciar.");
        } else if (gameContext.getGameController().getGameStatus() == GameController.GameStatus.PLAYER_VICTORY) {
            turnState = TurnState.GAME_OVER;
            eventLog.add("Você venceu!");
            eventLog.add("Pressione R para reiniciar.");
        }
    }

    private void renderEventLog() {
        float padding = 10f;
        float lineHeight = 20f;
        // Começa a desenhar com coordenadas de tela
        game.getBatch().setProjectionMatrix(uiRenderer.getUiMatrix());
        game.getBatch().begin();

        float x = Gdx.graphics.getWidth() - 300f;  // 300px da borda direita da tela
        float y = Gdx.graphics.getHeight() - padding;

        int line = 0;
        for (String msg : eventLog.getLog()) {
            font.draw(game.getBatch(), msg, x, y - line * lineHeight);
            line++;
        }

        game.getBatch().end();
    }

    @Override
    public void dispose() {
        font.dispose();
        uiRenderer.dispose();
    }

    @Override
    public void log(String message) {
        eventLog.add(message);
    }
}
