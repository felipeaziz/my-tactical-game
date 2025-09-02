package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ibra.tacticalrpg.action.SkillUseAction;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.LineSkill;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.ui.SkillMenuState;

import java.util.ArrayList;
import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.*;

public class SkillMenuController implements TileClickHandler {
    private SkillMenuState menuState = SkillMenuState.CLOSED;
    private List<Skill> skills;
    private int selectedSkillIndex = 0;
    private Skill selectedSkill;

    /**
     * @param actionController referência mantida para possível uso futuro em funcionalidades de interface
     */
    public SkillMenuController(ActionController actionController) {
        // Constructor mantido para compatibilidade futura
    }

    /**
     * Lida com input relacionado ao menu de skills
     */
    public void handleSkillMenuInput(GameMap grid, EventLogger logger,
                                   PlayerEntity player, OrthographicCamera camera) {
        switch (menuState) {
            case SELECTING_SKILL:
                handleSkillSelection(grid);
                break;
            case SELECTING_TARGET:
                handleTargetSelection(grid, logger, player, camera);
                break;
        }
    }

    /**
     * Abre o menu de seleção de skills
     */
    public void openSkillMenu(PlayerEntity player) {
        skills = player.getJob().getSkills();
        if (skills.isEmpty()) {
            System.out.println(player.getName() + "não possui nenhuma habilidade!");
            return;
        }
        menuState = SkillMenuState.SELECTING_SKILL;
        selectedSkillIndex = 0;
    }

    /**
     * Lida com a seleção da skill no menu
     */
    private void handleSkillSelection(GameMap grid) {
        // Cancelar seleção
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            closeSkillMenu(grid);
        }
    }

    /**
     * Lida com a seleção da skill no menu
     *
     * @param skillIndex índice da skill selecionada na lista
     * @param grid mapa do jogo para destacar células
     * @param logger logger para mensagens do sistema
     * @param player jogador que está usando a skill
     */
    public void handleSkillClick(int skillIndex, GameMap grid, EventLogger logger, PlayerEntity player) {
        if (menuState != SkillMenuState.SELECTING_SKILL || skillIndex < 0 || skillIndex >= skills.size()) {
            return;
        }

        selectedSkillIndex = skillIndex;
        selectedSkill = skills.get(selectedSkillIndex);
        menuState = SkillMenuState.SELECTING_TARGET;
        highlightValidTargets(grid, player, selectedSkill);
        logger.log("Selecione um alvo para " + selectedSkill.getName());
    }

    /**
     * Destaca tiles válidos para usar a skill
     */
    private void highlightValidTargets(GameMap grid, PlayerEntity player, Skill skill) {
        grid.clearHighlights();
        Tile entityTile = findEntityTile(grid, player);
        if (entityTile == null) return;

        List<Tile> targetableTiles = getTargetableTilesForSkill(grid, entityTile, player, skill);
        targetableTiles.forEach(tile -> {
            tile.setHighlighted(true);
            tile.setHighlightType(HighlightType.SKILL);
        });
    }

    private List<Tile> getTargetableTilesForSkill(GameMap grid, Tile entityTile, PlayerEntity player, Skill skill) {
        List<Tile> targetableTiles = new ArrayList<>();

        switch (skill.getTargetType()) {
            case ANY, AREA:
                targetableTiles.addAll(getTilesInRange(grid, entityTile, skill.getRange()));
                break;
            case SELF:
                targetableTiles.add(entityTile);
                break;
            case ALLY:
                getTilesInRange(grid, entityTile, skill.getRange()).stream()
                        .filter(tile -> tile.isOccupied() && tile.getOccupant() != null && tile.getOccupant().isAllyOf(player))
                        .forEach(targetableTiles::add);
                break;
            case ENEMY:
                getTilesInRange(grid, entityTile, skill.getRange()).stream()
                        .filter(tile -> tile.isOccupied() && tile.getOccupant() != null && !tile.getOccupant().isAllyOf(player))
                        .forEach(targetableTiles::add);
                break;
            case LINE:
                targetableTiles.addAll(getLineSkillTargets(grid, entityTile, (LineSkill) skill));
                break;
        }

        return targetableTiles;
    }

    private List<Tile> getLineSkillTargets(GameMap grid, Tile entityTile, LineSkill skill) {
        List<Tile> lineTiles = new ArrayList<>();
        int lineLength = skill.getLineLength();

        // Direções cardeais
        Tile[] directions = {
                grid.getTile(entityTile.getGridPositionX(), entityTile.getGridPositionY() + 1), // Norte
                grid.getTile(entityTile.getGridPositionX(), entityTile.getGridPositionY() - 1), // Sul
                grid.getTile(entityTile.getGridPositionX() + 1, entityTile.getGridPositionY()), // Leste
                grid.getTile(entityTile.getGridPositionX() - 1, entityTile.getGridPositionY())  // Oeste
        };

        for (Tile direction : directions) {
            if (direction != null) {
                lineTiles.addAll(getTilesInLineDirection(grid, entityTile, direction, lineLength));
            }
        }

        return lineTiles;
    }

    /**
     * Lida com a seleção de alvo para a skill
     */
    private void handleTargetSelection(GameMap grid, EventLogger logger,
                                       PlayerEntity player, OrthographicCamera camera) {

        // Cancelar seleção de alvo
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuState = SkillMenuState.SELECTING_SKILL;
            grid.clearHighlights();
            return;
        }

        // Seleção por clique
        if (Gdx.input.justTouched()) {
            Tile targetTile = findClickedHighlightedTile(grid, camera, Gdx.input.getX(), Gdx.input.getY());
            if (targetTile != null) {
                executeUseSkill(grid, logger, player, selectedSkill, targetTile);
            }
        }
    }

    /**
     * Executa o uso da skill
     */
    private void executeUseSkill(GameMap grid, EventLogger logger, PlayerEntity player,
                                 Skill skill, Tile targetTile) {
        SkillUseAction useAction = new SkillUseAction(skill, targetTile, grid);
        player.setCurrentAction(useAction);
        useAction.execute(player, null);

        String targetName = targetTile.isOccupied() ? targetTile.getOccupant().getName() : "área vazia";
        logger.log("Você usou " + skill.getName() + " em " + targetName);
        player.setActedThisTurn(true);
        closeSkillMenu(grid);
    }

    /**
     * Fecha o menu de skill
     */
    public void closeSkillMenu(GameMap grid) {
        menuState = SkillMenuState.CLOSED;
        selectedSkill = null;
        skills = null;
        selectedSkillIndex = 0;
        grid.clearHighlights();
    }

    // Getters
    public List<Skill> getSkills() { return skills; }
    public Skill getSelectedSkill() { return selectedSkill; }
    public SkillMenuState getMenuState() { return menuState; }
}
