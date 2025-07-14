package com.ibra.tacticalrpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.ibra.tacticalrpg.action.Action;
import com.ibra.tacticalrpg.action.AttackAction;
import com.ibra.tacticalrpg.action.MoveAction;
import com.ibra.tacticalrpg.controller.EventLogger;
import com.ibra.tacticalrpg.controller.GameController;
import com.ibra.tacticalrpg.controller.PlayerController;
import com.ibra.tacticalrpg.entities.EnemyEntity;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.TerrainType;
import com.ibra.tacticalrpg.map.orthogonal.Tile;
import com.ibra.tacticalrpg.ui.EventLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BattleScreen implements Screen, EventLogger {
    public static final int CELL_SIZE = 40;
    public static final int GRID_ORIGIN_X = 50;
    public static final int GRID_ORIGIN_Y = 20;
    private static final int LOG_SIZE = 5;

    private final TacticalRPG game;
    private final Texture background;
    private final Map<TerrainType, Texture> terrainTextures = new HashMap<>();
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private enum TurnState { PLAYER, GAME_OVER }
    private TurnState turnState = TurnState.PLAYER;
    private EventLog eventLog;
    private GameContext gameContext;

    private float highlightTimer = 0f;

    public BattleScreen(TacticalRPG game) {
        this.game = game;
        background = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        initializeGame();
    }

    private void initializeGame() {
        this.eventLog = new EventLog(LOG_SIZE);
        this.gameContext = new GameContext(new GameController(), new PlayerController(), null, this);
        turnState = TurnState.PLAYER;
        for (Entity ent : gameContext.getGameController().getEntities()) {
            if (ent instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) ent;
                player.setGameContext(gameContext);
            }
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        highlightTimer += delta;
        ScreenUtils.clear(0, 0, 0, 1);
        if (turnState == TurnState.GAME_OVER && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            restartGame();
        }
        checkEndGame();
        updateEntityMovement(delta);
        handleTurns();
        drawGrid();
        drawEntities();
        renderUI();
    }

    private void restartGame() {
        this.initializeGame();
    }

    private void renderUI() {
        game.getBatch().begin();
        drawCurrentTurnInfo();
        drawActionHints();
        drawLogs();
        game.getBatch().end();
    }

    private void drawCurrentTurnInfo() {
        if (gameContext.getGameController().getCurrentEntityIndex() < gameContext.getGameController().getEntities().size()) {
            Entity ent = gameContext.getGameController().getCurrentEntity();
            StringBuilder turno = new StringBuilder("Turno do ").append(ent.getName());
            font.setColor(Color.YELLOW);
            font.draw(game.getBatch(), turno.toString(), GRID_ORIGIN_X, GRID_ORIGIN_Y - 5);
        }
    }

    private void drawActionHints() {
        if(gameContext.getGameController().getCurrentEntityIndex() >= gameContext.getGameController().getEntities().size()
            || gameContext.getGameController().getCurrentEntity() instanceof EnemyEntity) {
            return;
        }

        PlayerEntity currentPlayer = (PlayerEntity) gameContext.getGameController().getCurrentEntity();
        font.setColor(Color.WHITE);
        StringBuilder actionHint = new StringBuilder("Ações: ");
        actionHint.append(currentPlayer.hasMoved() ? "" : "[1] Mover  ");
        actionHint.append(currentPlayer.hasActed() ? "" : "[2] Atacar  ");
        actionHint.append("[3] Encerrar turno");
//        font.draw(game.getBatch(),
//            actionHint.toString(),
//            GRID_ORIGIN_X,
//            GRID_ORIGIN_Y + gameContext.getGameController().getGrid().getHeight() * CELL_SIZE + 30);
    }

    private void drawLogs() {
//        font.setColor(Color.LIGHT_GRAY);
//        int logY = GRID_ORIGIN_Y + gameContext.getGameController().getGrid().getHeight() * CELL_SIZE;
//        int logX = GRID_ORIGIN_X + gameContext.getGameController().getGrid().getWidth() * CELL_SIZE + 10;
//        font.draw(game.getBatch(), "Log de eventos:", logX, logY);
//        int offset = 0;
//        for (String logMsg : eventLog.getLog()) {
//            font.draw(game.getBatch(), logMsg, logX, logY - 15 * (++offset));
//        }
    }

    private void checkEndGame() {
        if (turnState == TurnState.GAME_OVER) return;  // Se já está em GAME_OVER, não precisa verificar novamente

//        gameContext.getGameController().updateGameStatus();
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

    private void handleTurns() {
        if (turnState == TurnState.GAME_OVER) return;
//        gameContext.getGameController().advanceTurn();
    }

    @Override
    public void log(String msg) {
        eventLog.add(msg);
    }

    private void drawGrid() {
        drawTextures();
//        drawGridCells();
        drawHighlightedCells();
        drawGridLines();
    }

    private void drawTextures() {
        Arrays.stream(TerrainType.values()).forEach(type -> {
            if (!terrainTextures.containsKey(type)) {
                String texturePath = "terrain/" + type.getTextureFileName();
                Texture texture = new Texture(Gdx.files.internal(texturePath));
                terrainTextures.put(type, texture);
            }
        });
        game.getBatch().begin();
//        for(int x = 0; x < gameContext.getGameController().getGrid().getWidth(); x++) {
//            for (int y = 0; y < gameContext.getGameController().getGrid().getHeight(); y++) {
//                Tile cell = gameContext.getGameController().getGrid().getTile(x, y);
//                int drawY = GRID_ORIGIN_Y + (gameContext.getGameController().getGrid().getHeight() - 1 - y) * CELL_SIZE;
//                int drawX = GRID_ORIGIN_X + x * CELL_SIZE;
//                Texture texture = terrainTextures.get(cell.getTerrainType());
//                if (texture != null) {
//                    game.getBatch().draw(texture, drawX, drawY, CELL_SIZE, CELL_SIZE);
//                }
//            }
//        }
        game.getBatch().end();
    }

    private void drawHighlightedCells() {
        // Highlights apenas para jogadores
        if (gameContext.getGameController().getCurrentEntityIndex() < gameContext.getGameController().getEntities().size()
            && gameContext.getGameController().getCurrentEntity() instanceof PlayerEntity) {
            Set<Tile> highlights = gameContext.getPlayerController().getHighlightedCells();
            Action currentAction = ((PlayerEntity) gameContext.getGameController().getCurrentEntity()).getCurrentAction();
            if (highlights != null) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                if (currentAction instanceof MoveAction) {
                    shapeRenderer.setColor(1f, 1f, 0.6f, 0.4f);
                } else if (currentAction instanceof AttackAction) {
                    shapeRenderer.setColor(1f, 0.4f, 0.4f, 0.4f);
                }
//                for (Tile cell : highlights) {
//                    int drawY = GRID_ORIGIN_Y + (gameContext.getGameController().getGrid().getHeight() - 1 - cell.getY()) * CELL_SIZE;
//                    int drawX = GRID_ORIGIN_X + cell.getX() * CELL_SIZE;
//                    shapeRenderer.rect(drawX, drawY, CELL_SIZE, CELL_SIZE);
//                }
                shapeRenderer.end();
            }
        }
    }

    private void drawGridLines() {
        // Sempre desenha as linhas do grid
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.LIGHT_GRAY);
//        for (int x = 0; x <= gameContext.getGameController().getGrid().getWidth(); x++) {
//            shapeRenderer.line(GRID_ORIGIN_X + x * CELL_SIZE,
//                GRID_ORIGIN_Y,
//                GRID_ORIGIN_X + x * CELL_SIZE,
//                GRID_ORIGIN_Y + gameContext.getGameController().getGrid().getHeight() * CELL_SIZE);
//        }
//        for (int y = 0; y <= gameContext.getGameController().getGrid().getHeight(); y++) {
//            int drawY = GRID_ORIGIN_Y + (gameContext.getGameController().getGrid().getHeight() - y) * CELL_SIZE;
//            shapeRenderer.line(GRID_ORIGIN_X,
//                drawY,
//                GRID_ORIGIN_X + gameContext.getGameController().getGrid().getWidth() * CELL_SIZE,
//                drawY);
//        }
//
//        Tile hovered = getTileUnderMouse();
//        if (hovered != null) {
//            int drawX = GRID_ORIGIN_X + hovered.getX() * CELL_SIZE;
//            int drawY = GRID_ORIGIN_Y + (gameContext.getGameController().getGrid().getHeight() - 1 - hovered.getY()) * CELL_SIZE;
//            shapeRenderer.setColor(Color.GOLD); // cor de destaque
//            shapeRenderer.rect(drawX, drawY, CELL_SIZE, CELL_SIZE);
//        }
//        shapeRenderer.end();
    }

//    private Tile getTileUnderMouse() {
//        int mouseX = Gdx.input.getX();
//        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY(); // LibGDX inverte o Y
//        int gridX = (mouseX - GRID_ORIGIN_X) / CELL_SIZE;
//        int gridY = (mouseY - GRID_ORIGIN_Y) / CELL_SIZE;
//        // Ajuste para o sistema de coordenadas do seu grid
//        gridY = gameContext.getGameController().getGrid().getHeight() - 1 - gridY;
//        return gameContext.getGameController().getGrid().getTile(gridX, gridY);
//    }

    private void drawEntities() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        for (int x = 0; x < gameContext.getGameController().getGrid().getWidth(); x++) {
//            for (int y = 0; y < gameContext.getGameController().getGrid().getHeight(); y++) {
//                Tile cell = gameContext.getGameController().getGrid().getTile(x, y);
//                Entity occupant = cell.getOccupant();
//                if (occupant != null) {
//                    int drawY = GRID_ORIGIN_Y + (gameContext.getGameController().getGrid().getHeight() - 1 - y) * CELL_SIZE + 4;
//                    int drawX = GRID_ORIGIN_X + x * CELL_SIZE + 4;
//                    // Verifica o tipo de entidade e desenha apropriadamente
//                    if (occupant instanceof PlayerEntity) {
//                        PlayerEntity player = (PlayerEntity) occupant;
//                        if (!player.isAlive()) {
//                            shapeRenderer.setColor(Color.PURPLE);
//                        } else {
//                            shapeRenderer.setColor(Color.BLUE);
//                        }
//                    } else if (occupant instanceof EnemyEntity) {
//                        EnemyEntity enemy = (EnemyEntity) occupant;
//                        if (!enemy.isAlive()) {
//                            shapeRenderer.setColor(Color.BROWN);
//                        } else {
//                            shapeRenderer.setColor(Color.RED);
//                        }
//                    }
//                    shapeRenderer.rect(drawX, drawY, CELL_SIZE - 8, CELL_SIZE - 8);
//                    highlightCurrentPlayer(occupant, drawX, drawY);
//                    drawEntityHPBar(drawX, drawY, cell);
//                }
//            }
//        }
        shapeRenderer.end();
    }

    private void highlightCurrentPlayer(Entity occupant, int drawX, int drawY) {
        Entity currentEntity = gameContext.getGameController().getCurrentEntity();
        if (currentEntity != null && currentEntity.equals(occupant)
            && currentEntity instanceof PlayerEntity && currentEntity.isAlive()) {
            shapeRenderer.end(); // fecha o bloco atual
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            float alpha = 0.5f + 0.5f * (float)Math.sin(highlightTimer * 6); // valor entre 0 e 1
            shapeRenderer.setColor(1f, 1f, 0f, alpha); // cor amarela com alpha dinâmico

            shapeRenderer.rect(drawX - 2, drawY - 2, CELL_SIZE, CELL_SIZE);
            shapeRenderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // volta ao modo anterior
        }
    }

    private void drawEntityHPBar(int drawX, int drawY, Tile cell) {
        Entity occupant = cell.getOccupant();
        // --- Barra de HP ---
        int barWidth = CELL_SIZE - 8;
        int barHeight = 6;
        int barY = drawY + (CELL_SIZE - 8) + 2; // logo acima da entidade

        float hpPercent = (float)  occupant.getStats().getCurrentHp()
            / occupant.getStats().getMaxHp();

        // Fundo da barra (vermelho)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(drawX, barY, barWidth, barHeight);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(drawX, barY, barWidth, barHeight);

        // HP atual (verde)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(drawX, barY, barWidth * hpPercent, barHeight);
    }

    private void updateEntityMovement(float delta) {
        Entity current = gameContext.getGameController().getCurrentEntity();
        if (current != null && current.isMoving()) {
//            current.updateMovement(gameContext.getGameController().getGrid(), delta);
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        shapeRenderer.dispose();
        terrainTextures.values().forEach(Texture::dispose);
        font.dispose();
    }
}
