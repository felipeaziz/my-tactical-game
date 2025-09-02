package com.ibra.tacticalrpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ibra.tacticalrpg.action.SkillUseAction;
import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.entities.PlayerEntity;
import com.ibra.tacticalrpg.map.HighlightType;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;
import com.ibra.tacticalrpg.ui.SkillMenuState;

import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.findEntityTile;
import static com.ibra.tacticalrpg.grid.IsometricGridUtils.getTilesInRange;

public class SkillMenuController {
    private SkillMenuState menuState = SkillMenuState.CLOSED;
    private List<Skill> skills;
    private int selectedSkillIndex = 0;
    private Skill selectedSkill;
    private final ActionController actionController;

    public SkillMenuController(ActionController actionController) {
        this.actionController = actionController;
    }

    /**
     * Lida com input relacionado ao menu de skills
     */
    public void handleSkillMenuInput(GameMap grid,
                                     List<Entity> entities,
                                     EventLogger logger,
                                     PlayerEntity player,
                                     OrthographicCamera camera) {
        switch (menuState) {
            case SELECTING_SKILL:
                handleSkillSelection(grid);
                break;
            case SELECTING_TARGET:
                handleTargetSelection(grid, entities, logger, player, camera);
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
     * @param skillIndex
     * @param grid
     * @param logger
     * @param player
     */
    public void handleSkillClick(int skillIndex, GameMap grid, EventLogger logger, PlayerEntity player) {
        if (menuState != SkillMenuState.SELECTING_SKILL) {
            return;
        }

        if (skillIndex >= 0 && skillIndex < skills.size()) {
            selectedSkillIndex = skillIndex;
            selectedSkill = skills.get(selectedSkillIndex);
            menuState = SkillMenuState.SELECTING_TARGET;
            //TODO also handle other target types like enemies, area or allies
            if (selectedSkill.getTargetType().equals(TargetType.ANY)) {
                highlightValidTargets(grid, player, selectedSkill);
                logger.log("Selecione um alvo para " + selectedSkill.getName());
            } else {
                //TODO Use skill without target selection
                logger.log("Habilidade " + selectedSkill.getName() + " usada!");
            }
        }
    }

    /**
     * Destaca tiles válidos para usar a skill
     */
    private void highlightValidTargets(GameMap grid, PlayerEntity player, Skill skill) {
        actionController.clearHighlights(grid);
        Tile entityTile = findEntityTile(grid, player);
        switch (skill.getTargetType()) {
            case ANY, AREA:
                // Destaca todos os tiles dentro do alcance da skill
                List<Tile> targetableTiles = getTilesInRange(grid, entityTile, skill.getRange());
                for (Tile tile : targetableTiles) {
                    tile.setHighlighted(true);
                    tile.setHighlightType(HighlightType.SKILL);
                }
                break;
            case SELF:
                // Destaca apenas o tile onde o jogador está
                if (entityTile != null) {
                    entityTile.setHighlighted(true);
                    entityTile.setHighlightType(HighlightType.SKILL);
                }
                break;
            case ALLY:
                // Destaca tiles com aliados dentro do alcance da skill
                List<Tile> allyTiles = getTilesInRange(grid, entityTile, skill.getRange());
                for (Tile tile : allyTiles) {
                    if (tile.isOccupied() && tile.getOccupant() != null && tile.getOccupant().isAllyOf(player)) {
                        tile.setHighlighted(true);
                        tile.setHighlightType(HighlightType.SKILL);
                    }
                }
                break;
            case ENEMY:
                // Destaca tiles com inimigos dentro do alcance da skill
                List<Tile> enemyTiles = getTilesInRange(grid, entityTile, skill.getRange());
                for (Tile tile : enemyTiles) {
                    if (tile.isOccupied() && tile.getOccupant() != null && !tile.getOccupant().isAllyOf(player)) {
                        tile.setHighlighted(true);
                        tile.setHighlightType(HighlightType.SKILL);
                    }
                }
                break;
            case ALL_ENTITIES:
                //TODO implementar destaque para todos os entities
        }
    }

    /**
     * Lida com a seleção de alvo para a skill
     */
    private void handleTargetSelection(GameMap grid,
                                       List<Entity> entities,
                                       EventLogger logger,
                                       PlayerEntity player,
                                       OrthographicCamera camera) {

        // Cancelar seleção de alvo
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuState = SkillMenuState.SELECTING_SKILL;
            actionController.clearHighlights(grid);
            return;
        }

        // Seleção por clique
        if (Gdx.input.justTouched()) {
            Vector3 screenMouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            Vector3 worldMouse3D = camera.unproject(screenMouse);
            Vector2 worldMouse = new Vector2(worldMouse3D.x, worldMouse3D.y);

            Tile targetTile = null;
            for (Tile tile : grid.getBaseTiles()) {
                if (tile.isHighlighted() && tile.isPointInsideDiamond(worldMouse)) {
                    targetTile = tile;
                    break;
                }
            }

            if (targetTile != null) {
                //TODO - se for skill em area, exibir area afetada antes de confirmar.
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

    public List<Skill> getSkills() {
        return skills;
    }

    public Skill getSelectedSkill() {
        return selectedSkill;
    }

    public SkillMenuState getMenuState() {
        return menuState;
    }

    /**
     * Fecha o menu de skill
     */
    public void closeSkillMenu(GameMap grid) {
        menuState = SkillMenuState.CLOSED;
        selectedSkill = null;
        skills = null;
        selectedSkillIndex = 0;
        actionController.clearHighlights(grid);
    }
}
