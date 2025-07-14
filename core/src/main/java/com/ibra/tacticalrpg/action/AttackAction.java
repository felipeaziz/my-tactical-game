package com.ibra.tacticalrpg.action;

import com.ibra.tacticalrpg.entities.Entity;
import com.ibra.tacticalrpg.map.isometric.GameMap;
import com.ibra.tacticalrpg.map.isometric.Tile;

public class AttackAction implements Action {
    private final GameMap gameMap;
    private final Tile targetTile;

    public AttackAction(GameMap gameMap, Tile targetTile) {
        this.gameMap = gameMap;
        this.targetTile = targetTile;
    }

    @Override
    public void execute(Entity actor, Entity target) {
        if (!isValidAttack(actor, target)) {
            return;
        }

        // Calcula e aplica o dano
        int damage = calculateDamage(actor, target);
        target.getStats().setCurrentHp(target.getStats().getCurrentHp() - damage);
    }

    private boolean isValidAttack(Entity actor, Entity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        // Verifica se o alvo está dentro do alcance de ataque
        return actor.getAttackableCells(gameMap).contains(targetTile);
    }

    private int calculateDamage(Entity attacker, Entity defender) {
        int baseDamage = attacker.getStats().getAttack() - defender.getStats().getDefense();
        return Math.max(1, baseDamage); // Garante pelo menos 1 de dano
    }
}
