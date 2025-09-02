package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.AreaSkill;
import com.ibra.tacticalrpg.skill.LineSkill;
import com.ibra.tacticalrpg.skill.Skill;
import com.ibra.tacticalrpg.skill.TargetType;

import java.util.List;

import static com.ibra.tacticalrpg.grid.IsometricGridUtils.getTilesInRange;

public class SkillUseAction implements Action {
    private final Skill skill;
    private final Tile targetTile;
    private final GameMap grid;

    public SkillUseAction(Skill skill, Tile targetTile, GameMap gameMap) {
        this.skill = skill;
        this.targetTile = targetTile;
        this.grid = gameMap;
    }

    public SkillUseAction(GameMap gameMap, Skill skill) {
        this(skill, null, gameMap); // Para skill sem target definido
    }

    @Override
    public void execute(Entity actor, Entity target) {
        if (!isValidAction(actor)) {
            return;
        }

        Entity actualTarget = determineTarget(target);
        if (actualTarget == null && skill.getTargetType() != TargetType.AREA && skill.getTargetType() != TargetType.LINE) {
            System.out.println("Alvo inválido para usar a habilidade!");
            return;
        }

        executeSkill(actor, actualTarget);
    }

    private boolean isValidAction(Entity actor) {
        return skill != null && actor != null;
    }

    private Entity determineTarget(Entity target) {
        if (targetTile != null && targetTile.isOccupied()) {
            return targetTile.getOccupant();
        }
        return target;
    }

    private void executeSkill(Entity actor, Entity target) {
        switch (skill.getTargetType()) {
            case SELF -> skill.use(actor, actor);
            case ALLY, ENEMY, ANY -> executeTargetedSkill(actor, target);
            case AREA -> executeAreaSkill(actor);
            case LINE -> executeLineSkill(actor);
        }
    }

    private void executeTargetedSkill(Entity actor, Entity target) {
        if (target == null) {
            System.out.println("Alvo inválido para usar a habilidade!");
            return;
        }
        skill.use(actor, target);
    }

    private void executeAreaSkill(Entity actor) {
        if (targetTile == null) {
            System.out.println("Tile alvo inválido para usar a habilidade!");
            return;
        }
        AreaSkill areaSkill = (AreaSkill) skill;
        areaSkill.useOnArea(actor, targetTile, grid);
    }

    private void executeLineSkill(Entity actor) {
        if (targetTile == null) {
            System.out.println("Tile alvo inválido para usar a habilidade!");
            return;
        }
        LineSkill lineSkill = (LineSkill) skill;
        lineSkill.useOnLine(actor, targetTile, grid);
    }

    public Skill getSkill() {
        return skill;
    }
}
