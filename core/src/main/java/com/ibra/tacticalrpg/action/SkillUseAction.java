package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.grid.IsometricGridUtils;
import com.ibra.tacticalrpg.item.consumable.ConsumableItem;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;
import com.ibra.tacticalrpg.skill.AreaSkill;
import com.ibra.tacticalrpg.skill.Skill;

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
        if (skill == null || actor == null) {
            return;
        }
        if (targetTile != null && targetTile.isOccupied()) {
            target = targetTile.getOccupant();
        }

        switch (skill.getTargetType()) {
            case SELF -> skill.use(actor, actor);
            case ALLY, ENEMY, ANY -> {
                if (target == null) {
                    System.out.println("Alvo inválido para usar a habilidade!");
                    return;
                }
                skill.use(actor, target);
            }
            case AREA -> {
                if (targetTile == null) {
                    System.out.println("Tile alvo inválido para usar a habilidade!");
                    return;
                }
                AreaSkill areaSkill = (AreaSkill) skill;
                areaSkill.useOnArea(actor, targetTile, grid);
            }
            case ALL_ENTITIES -> {
                //TODO Implementar lógica para afetar todas as entidades
            }
            //TODO implementar LINE skill
        }
    }
}
